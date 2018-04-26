package org.chengpx.mylib;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create at 2018/4/23 14:50 by chengpx
 */
public class HttpUtils {

    private static HttpUtils sHttpUtils = new HttpUtils();

    private final OkHttpClient mOkHttpClient;
    private String tag = "org.chengpx.a2trafficlight.util.HttpUtils";

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient();
    }

    public <Req> void sendPost(String url, Req req, final Callback callBack) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), SignalInstancePool.newGson().toJson(req)))
                .build();
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(tag, Thread.currentThread().getName());
                Message message = Message.obtain();
                message.obj = response.body().string();
                callBack.sendMessage(message);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });

    }

    public static HttpUtils getInstance() {
        return sHttpUtils;
    }

    public static abstract class Callback<Result> extends Handler {

        private final Class<Result> mResultClass;

        public Callback(Class<Result> resultClass) {
            mResultClass = resultClass;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object obj = msg.obj;
            if (!(obj instanceof String)) {
                return;
            }
            String json = (String) obj;
            try {
                JSONObject jsonObject = new JSONObject(json);
                String serverInfo = jsonObject.getString("serverInfo");
                if (serverInfo != null) {
                    onSuccess(SignalInstancePool.newGson().fromJson(serverInfo, mResultClass));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected abstract void onSuccess(Result result);

    }

}
