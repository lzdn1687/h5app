package com.kesitong.shop.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kesitong.shop.util.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static final String TAG = WXPayEntryActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    public interface CallBack {
        void onResp(String result);
    }


    @Override
    public void onResp(BaseResp resp) {

        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);
        Log.e(TAG, "onPayFinish, errStr = " + resp.errStr);
        Log.e(TAG, "onPayFinish, transaction = " + resp.transaction);
        Log.e(TAG, "onPayFinish, openId = " + resp.openId);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                //支付成功
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Constants.PAY_RECEIVER_ACTION_SUCCESS));
            } else if (resp.errCode == -2) {
//                Toast.makeText(this, "没有交易", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Constants.PAY_RECEIVER_ACTION_CANCEL));
            } else {
//                Toast.makeText(this, resp.errCode, Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Constants.PAY_RECEIVER_ACTION_FAIL));
            }
            finish();
        }
    }
}