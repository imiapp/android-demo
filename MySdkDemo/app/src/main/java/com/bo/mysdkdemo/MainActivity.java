package com.bo.mysdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity implements CreateChannelService {

    private static final String TAG = "MainActivity";

    private View mLayoutLogin;
    private TextView mTvResult;
    private TextView mTvDown;
    private LinearLayout mLayoutThirdLogin;
    private Button mBtnLogin;

    private String name = "阿里巴巴";
    private String version = "2.0";
    private String topicId = null;
    private String scope = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mLayoutLogin = findViewById(R.id.layout_login);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mLayoutThirdLogin = (LinearLayout) findViewById(R.id.ll_third_login);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mLayoutThirdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThirdLogin();
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReservationActivity.class));
            }
        });
    }

    public void testThirdLogin() {

        scope = Constants.TYPE_THIRD_LOGIN;
//        scope = Constants.TYPE_THIRD_ACCREDIT_IDCARD;
//        scope = Constants.TYPE_THIRD_LOGIN + "," + Constants.TYPE_THIRD_ACCREDIT_IDCARD;

        NetWorkRequest.createChannel(this, version, scope, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    topicId = resultJson.getString("topicId");
                    if (!TextUtils.isEmpty(topicId)) {
                        IMIAPI imiapiFactory = IMIAPIFactory.createIMIAPI(MainActivity.this, true);
                        if (imiapiFactory.isIMIAppInstalled()) {
                            imiapiFactory.reqLogin(name, MainActivity.this);
                        } else {
                            Toast.makeText(MainActivity.this, "您没有安装IMI", Toast.LENGTH_SHORT).show();
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

            NetWorkRequest.getAuthorizationInfo(this, topicId, Constants.TYPE_THIRD_LOGIN, new AsyncHttpResponseHandler() {
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
