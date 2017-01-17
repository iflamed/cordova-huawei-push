package com.iflamed.huaweipush;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiAvailability.OnUpdateListener;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.entity.push.TokenResp;

import android.util.Log;
import java.lang.Thread;
import android.app.Activity;
/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaHuaweiPush extends CordovaPlugin implements HuaweiApiClient.ConnectionCallbacks,
HuaweiApiClient.OnConnectionFailedListener,
OnUpdateListener {
    public static HuaweiApiClient huaweiApiClient;
    // 接收Push消息
    public static final int RECEIVE_PUSH_MSG = 0x100;

    // 接收Push Token消息
    public static final int RECEIVE_TOKEN_MSG = 0x101;

    // 接收Push 自定义通知消息内容
    public static final int RECEIVE_NOTIFY_CLICK_MSG = 0x102;

    public static final int RECEIVE_TAG_MSG = 0x103;

    public static final int RECEIVE_STATUS_MSG = 0x104;

    public static final int OTHER_MSG = 0x105;

    public static final String NORMAL_MSG_ENABLE = "normal_msg_enable";

    public static final String NOTIFY_MSG_ENABLE = "notify_msg_enable";

    public static String TAG = "HuaweiPushPlugin";
    public static String token = "";
    public static int openNotificationId=0;
    public static String openNotificationExtras;

    private static CordovaHuaweiPush instance;
    private static Activity activity;
    private CallbackContext initCallback;

    public CordovaHuaweiPush() {
        instance = this;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
        //如果是首次启动，并且点击的通知消息，则处理消息
        if (openNotificationId != 0) {
            notificationOpened(openNotificationId, openNotificationExtras);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            this.init(callbackContext);
            return true;
        }
        if (action.equals("stop")) {
            this.delToken(callbackContext);
        }
        return false;
    }

    private void init(CallbackContext callbackContext) {
        huaweiApiClient = new HuaweiApiClient.Builder(this.cordova.getActivity())
        .addApi(HuaweiPush.PUSH_API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
        huaweiApiClient.connect();
        this.initCallback = callbackContext;
    }

     @Override
    public void onDestroy() {
        super.onDestroy();
        huaweiApiClient = null;
    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    public void onConnected() {
        this.getToken();
    }

    @Override
    public void onUpdateFailed(ConnectionResult result) {
    }


    private void getToken() {
        if (!huaweiApiClient.isConnected()) {
            initCallback.error("{status:\"failed\"}");
            return;
        }
        // 异步调用方式
        try {
            // 异步调用方式
            PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient);
            tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

                @Override
                public void onResult(TokenResult result) {
                }

            });
            initCallback.success("{status:\"success\"}");
        } catch (Exception e) {
            initCallback.error("{status:\"failed\"}");
            Log.e(TAG, e.toString(), e);
        }
    }

    public void onConnectionSuspended(int cause) {
    }

    public static void onTokenRegistered(String regId) {
        Log.e(TAG, "-------------onTokenRegistered------------------" + regId);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("token",regId);
            String format = "window.cordova.plugins.huaweipush.tokenRegistered(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delToken(final CallbackContext callbackContext) {
       new Thread() {
           @Override
           public void run() {
               try {
                   if (token != "" && null != huaweiApiClient) {
                       HuaweiPush.HuaweiPushApi.deleteToken(huaweiApiClient, token);
                       callbackContext.success();
                   } else {
                       Log.w(TAG, "delete token's params is invalid.");
                       callbackContext.error("token not exists");
                   }
               } catch (Exception e) {
                    callbackContext.error("error occered when delete token");
                    Log.e("PushLog", "delete token exception, " + e.toString());
               }
           }
       }.start();
    }

    public static void pushMsgReceived (String msg) {
        Log.e(TAG, "-------------onTokenRegistered------------------" + msg);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("extras",msg);
            String format = "window.cordova.plugins.huaweipush.pushMsgReceived(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void notificationOpened (int notifyId,String msg) {
        CordovaHuaweiPush.openNotificationId = notifyId;
        CordovaHuaweiPush.openNotificationExtras = msg;
        Log.e(TAG, "-------------onTokenRegistered------------------" + msg);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("extras",msg);
            String format = "window.cordova.plugins.huaweipush.notificationOpened(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CordovaHuaweiPush.openNotificationId = 0;
        CordovaHuaweiPush.openNotificationExtras = "";
    }
}
