package com.songbai.futurex.http;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.sbai.httplib.BitmapCfg;
import com.sbai.httplib.BitmapRequest;
import com.sbai.httplib.CookieManger;
import com.sbai.httplib.GsonRequest;
import com.sbai.httplib.MultipartRequest;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.sbai.httplib.ReqHeaders;
import com.sbai.httplib.ReqIndeterminate;
import com.sbai.httplib.ReqLogger;
import com.sbai.httplib.ReqParams;
import com.sbai.httplib.RequestManager;
import com.songbai.futurex.App;
import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.AppInfo;
import com.songbai.futurex.utils.LanguageUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Modified by john on 23/01/2018
 * <p>
 * Description: 请求 Api 基础类
 * <p>
 */
public class Api extends RequestManager {

    private static Set<String> sCurrentUrls = new HashSet<>();
    private static String FIXED_HOST;

    private int mMethod;
    private String mId;
    private String mTag;
    private String mApi;
    private String mJsonBody;
    private ReqCallback<?> mCallback;
    private ReqParams mReqParams;
    private ReqHeaders mReqHeaders;
    private ReqIndeterminate mIndeterminate;
    private int mTimeout;
    private String mHost;

    private File mFile;
    private String mFileName;

    private BitmapCfg mBitmapCfg;

    private Api() {
        mMethod = GET;
        mReqHeaders = new ReqHeaders();
    }

    public static Api get(String api) {
        Api reqApi = new Api();
        reqApi.mApi = api;
        return reqApi;
    }

    public static Api get(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }

    public static Api put(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mMethod = PUT;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }

    public static Api post(String api) {
        return post(api, null);
    }

    public static Api post(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mMethod = POST;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }

    public static Api post(String api, ReqParams reqParams, String jsonBody) {
        Api reqApi = new Api();
        reqApi.mMethod = POST;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        reqApi.mJsonBody = jsonBody;
        return reqApi;
    }

    public static Api post(String uri, ReqParams apiParams, String filePartName, File file) {
        Api api = new Api();
        api.mMethod = POST;
        api.mApi = uri;
        api.mReqParams = apiParams;
        api.mFile = file;
        api.mFileName = filePartName;
        return api;
    }

    public Api id(String id) {
        mId = id;
        return this;
    }

    public Api tag(String tag) {
        mTag = tag;
        return this;
    }

    public Api callback(ReqCallback<?> callback) {
        mCallback = callback;
        return this;
    }

    public Api host(String host) {
        mHost = host;
        return this;
    }

    public Api indeterminate(ReqIndeterminate indeterminate) {
        mIndeterminate = indeterminate;
        return this;
    }

    public Api header(String key, Object value) {
        mReqHeaders.put(key, value);
        return this;
    }

    public Api bitmapCfg(BitmapCfg bitmapCfg) {
        mBitmapCfg = bitmapCfg;
        return this;
    }

    public Api timeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public void fire() {
        String url = getUrl();
        synchronized (sCurrentUrls) {
            if (sCurrentUrls.add(mTag + "#" + url)) {
                createReqThenEnqueue(url);
            }
        }
    }

    public void fireFreely() {
        String url = getUrl();
        createReqThenEnqueue(url);
    }

    private void createReqThenEnqueue(String url) {
        setupHeaders(mReqHeaders);

        // setup Callback
        if (mCallback != null) {
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
            mCallback.setId(mId);
            mCallback.setTag(mTag);
            mCallback.setIndeterminate(mIndeterminate);
        } else { // with a default callback for handle request finish event
            mCallback = new ReqCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                }

                @Override
                public void onFailure(ReqError reqError) {
                }
            };
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
        }

        // new request
        Request request;
        Type type = mCallback.getGenericType();
        if (mFile != null && !TextUtils.isEmpty(mFileName)) {
            request = new MultipartRequest(mMethod, url, mReqHeaders, mFileName, mFile, mReqParams, type, mCallback);
        } else if (mBitmapCfg != null && isBitmapType(type)) {
            request = new BitmapRequest(url, mBitmapCfg.getMaxWidth(), mBitmapCfg.getMaxHeight(),
                    mBitmapCfg.getScaleType(), mBitmapCfg.getConfig(), (ReqCallback<Bitmap>) mCallback);
        } else {
            request = new GsonRequest(mMethod, url, mReqHeaders, mReqParams, mJsonBody, type, mCallback);
        }
        request.setTag(mTag);

        if (mTimeout != 0) {
            request.setRetryPolicy(new DefaultRetryPolicy(mTimeout, 1, 1));
        }

        // start and enqueue
        mCallback.onStart();
        enqueue(request);
    }

    private boolean isBitmapType(Type type) {
        return type instanceof Class && ((Class) type).getSimpleName().equals(Bitmap.class.getSimpleName());
    }

    private void setupHeaders(ReqHeaders headers) {
        String cookies = headers.get("Cookie");

        String userToken = LocalUser.getUser().getToken();
        if (TextUtils.isEmpty(cookies)) {
            cookies = userToken;
        } else {
            cookies += "; " + userToken;
        }

        String imageSign = CookieManger.getInstance().getNameValuePair("img_sign");
        if (TextUtils.isEmpty(cookies)) {
            cookies = imageSign;
        } else {
            cookies += "; " + imageSign;
        }

        Locale userLocale = LanguageUtils.getUserLocale(App.getAppContext());
        StringBuilder language = new StringBuilder();
        language.append(userLocale.getLanguage());
        String country = userLocale.getCountry();
        String languageStr = "language=" + language + (TextUtils.isEmpty(country) ? "" : "_" + country);

        if (TextUtils.isEmpty(cookies)) {
            cookies = languageStr;
        } else {
            cookies += "; " + languageStr;
        }

        if (!TextUtils.isEmpty(cookies)) {
            headers.put("Cookie", cookies);
        }

        headers.put("ex-version", AppInfo.getVersionName(App.getAppContext()))
                .put("ex-device", AppInfo.getDeviceHardwareId(App.getAppContext()))
                .put("ex-channel", "android:" + AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
    }

    private static class RequestFinishedListener implements ReqCallback.onFinishedListener {

        public void onFinished(String tag, String url) {
            if (sCurrentUrls != null) {
                sCurrentUrls.remove(tag + "#" + url);
            }
        }
    }

    private String getUrl() {
        if (mReqParams != null) {
            mApi = mReqParams.replaceHolders(mApi);
        }

        String host = TextUtils.isEmpty(mHost) ? getFixedHost() : mHost;
        String url = new StringBuilder(host).append(mApi).toString();
        if (mMethod == GET && mReqParams != null) {
            url += mReqParams.toString();
            mReqParams = null;
        }

        return url;
    }

    public static void setFixedHost(String fixedHost) {
        FIXED_HOST = fixedHost;
    }

    public static String getFixedHost() {
        if (!TextUtils.isEmpty(FIXED_HOST)) {
            return FIXED_HOST;
        }

        if (!BuildConfig.IS_PROD) {
            return "http://" + BuildConfig.HOST;
        }
        return "https://" + BuildConfig.HOST;
    }

    public static void cancel(String tag) {
        RequestManager.cancel(tag);
        Iterator<String> iterator = sCurrentUrls.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (str.startsWith(tag + "#")) {
                iterator.remove();

                ReqLogger logger = getLogger();
                if (logger != null) {
                    logger.onTag("req of " + tag + " is not finish, cancel (" + str + ")");
                }
            }
        }
    }
}
