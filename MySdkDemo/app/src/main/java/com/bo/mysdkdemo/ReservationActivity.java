package com.bo.mysdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wifire.vport_third_sdk.constants.Constants;
import com.wifire.vport_third_sdk.interfaces.CreateChannelService;
import com.wifire.vport_third_sdk.interfaces.IMIAPI;
import com.wifire.vport_third_sdk.openapi.IMIAPIFactory;
import com.wifire.vport_third_sdk.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fu.shibo on 2017/4/10.
 */

public class ReservationActivity extends Activity implements CreateChannelService {

    private static final String TAG = "ReservationActivity";

    private TextView mTvReservation;
    private TextView mTvResult;
    private TextView mTvDown;
    private ImageButton mBack;

    private String name = "阿里巴巴";
    private String version = "2.0";
    private String topicId = null;
    private String scope = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        mTvResult = (TextView) findViewById(R.id.tv_result);
        mTvReservation = (TextView) findViewById(R.id.tv_real_name_reservation);

        mTvReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThirdAccredit();
            }
        });
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void testThirdAccredit() {

//        scope = Constants.TYPE_THIRD_LOGIN;
        scope = Constants.TYPE_THIRD_ACCREDIT_IDCARD;
//        scope = Constants.TYPE_THIRD_LOGIN + "," + Constants.TYPE_THIRD_ACCREDIT_IDCARD;

        NetWorkRequest.createChannel(this, version, scope, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    topicId = resultJson.getString("topicId");
                    scope = resultJson.getString("scope");
                    if (!TextUtils.isEmpty(topicId)) {
                        IMIAPI imiapiFactory = IMIAPIFactory.createIMIAPI(ReservationActivity.this, true);
                        if (imiapiFactory.isIMIAppInstalled()) {
                            imiapiFactory.reqAuthorize(scope, name, ReservationActivity.this);
                        } else {
                            Toast.makeText(ReservationActivity.this, "您没有安装IMI", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d(TAG, "CreateChannel success:" + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.d(TAG, "Authorize fail -> " + "statusCode: " + statusCode + " error:" + error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            NetWorkRequest.getAuthorizationInfo(this, topicId, Constants.TYPE_THIRD_ACCREDIT_IDCARD, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    LogUtils.d(TAG, "GetAuthorizationInfo success:" + response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    LogUtils.d(TAG, "GetAuthorizationInfo fail -> " + "statusCode: " + statusCode + " error:" + error);
                }
            });
        }
    }

    @Override
    public String createChannelBlock() {
        if (TextUtils.isEmpty(topicId)) {
            return null;
        }
        return topicId;
    }
}
