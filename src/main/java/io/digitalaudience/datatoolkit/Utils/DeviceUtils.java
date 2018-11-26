package io.digitalaudience.datatoolkit.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceUtils {

    private boolean debugMode = false;
    private Device Device;

    private void Init(Context appCtx, String identifier, String data, boolean debuggable) {

        debugMode = debuggable;
        if (Device == null) {
            Device = new Device();
        }
        Device.setIdentifier(identifier);
        if (data != null) {
            Device.setData(data);
        }
        Device.setPhoneNumber(getDevicePhoneNumber(appCtx));
    }

    private String getDevicePhoneNumber(Context appCtx) {
        if (appCtx.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) appCtx.getSystemService(Context.TELEPHONY_SERVICE);
            return tMgr.getLine1Number();
        }

        return null;
    }

    private String AddModelToData (String Data) throws JSONException {
        JSONObject payload = new JSONObject(Data);
        payload.put("Model", Build.MODEL);
        payload.put("Brand", Build.BRAND);
        return payload.toString();
    }

    public void sendDevice(final Context appCtx, final String data, final String pub, final boolean debuggable) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    AdvertisingIdClient client = new AdvertisingIdClient();
                    AdvertisingIdClient.AdInfo adInfo = client.getAdvertisingIdInfo(appCtx);
                    if (!adInfo.isLimitAdTrackingEnabled()) {
                        Init(appCtx, adInfo.getId(), data, debuggable);
                        /// SEND TO DA
                        // setting parameters for rest call
                        RequestParams params = new RequestParams();
                        params.put("Did", Device.getIdentifier());
                        params.put("Pub", pub);
                        params.put("Mob", Device.getPhoneNumber());
                        params.put("PlatformId", 3);
                        params.put("Data", AddModelToData(Device.getData()));
                        // response handler
                        AsyncHttpResponseHandler handler = CreateAsyncHandler();
                        // client
                        DigitalAudienceRestClient restClient = new DigitalAudienceRestClient();
                        restClient.post("m", params, handler);
                    }
                } catch (Exception e) {
                    if (debuggable) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private AsyncHttpResponseHandler CreateAsyncHandler() {

        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                String result = new String(bytes);
            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                // Failed
                String Somthinghappend = "";
            }

            // ----New Overridden method
            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }

        };
    }
}

class Device {
    private String Identifier;

    private String PhoneNumber;

    private String Data;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}