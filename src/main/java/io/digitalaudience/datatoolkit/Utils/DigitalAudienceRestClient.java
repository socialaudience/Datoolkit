package io.digitalaudience.datatoolkit.Utils;

import com.loopj.android.http.*;

class DigitalAudienceRestClient {
    //private static final String BASE_URL = "https://target.digitalaudience.io/bakery/pix/";
    private String BASE_URL = "https://socialaudience-cookiebaker.conveyor.cloud/pix/";

    private AsyncHttpClient client = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
