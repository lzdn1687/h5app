package com.kesitong.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.kesitong.shop.util.Constants;
import com.kesitong.shop.util.PayResult;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //易物乐
//    public static final String URL = "http://yiman976.com";

    //云南三农网
//    public static final String URL = "http://www.ynsnw.com/mobile";

    private WebView webView;
    private ProgressBar pg1;
    public static final String TAG = "WebViewActivity";
    public static final String URL1 = "http://qll.jiahetianlang.com/mobile/";

    //聚会合成
    public static final String URL2 = "http://www.cst01.com";
    //    public static final String URL = "http://www.cst01.com/?temp=wap";
    public static final String URL_BASE = "http://www.cst01.com/";
    public static final String URL = "file:///android_asset/scan.html";

    private TextView progress;
    public static final int REQUEST_SCAN_CODE = 0;
    public static final int REQUEST_CAMERA_PERMISSION = 10000;

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.wv);
        pg1 = (ProgressBar) findViewById(R.id.progressBar1);
        progress = (TextView) findViewById(R.id.progress);

        initSettings();
        webView.addJavascriptInterface(this, "android");
        webView.loadUrl(URL);

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
    }

    private void initSettings() {

        webView.setWebViewClient(new WebViewClient() {
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //为webview设置监听
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                    progress.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progress.setText(newProgress + "%");//设置进度值
                }
            }
        });


        //夜间模式-false
//        webView.setDayOrNight(true);
        //不显示滚动条
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings webSettings = webView.getSettings();

        //缓存策略（有缓存读取缓存否则加载网路）
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        //存储
        webSettings.setDatabaseEnabled(true);
        //不显示缩放按钮
        webSettings.setDisplayZoomControls(false);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        //开启JavaScript支持
        webSettings.setJavaScriptEnabled(true);
        // enable navigator.geolocation
        webSettings.setGeolocationEnabled(true);

        //解决5.0部分图片不显示
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);

        //【解决html5网页不能加载（空白或显示不完全）】
        webSettings.setDomStorageEnabled(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
    }

    /**
     * 如果您已经接入支付宝手机网站支付，可以通过接入我们的SDK将手机网站支付转为Native支付。
     * 接入过程极其简单，只需拦截手机网站支付的url，将该url转交给SDK进行处理；
     * 无需接入者解析参数字段，接入者的服务端也无需改造。
     * <p>
     * 调用本接口对支付宝支付URL进行拦截和支付转化。
     * <p>
     * 当接口调用完成后，该接口会返回一个boolen类型的同步拦截结果，如果同步结果返回值为true，
     * 说明传入的URL为支付宝支付URL，支付宝SDK已经成功拦截该URL，并转化为APP支付方式，商户容器无需再加载该URL；
     * 如果返回值为false，说明传入的URL并非支付宝支付URL，商户容器需要继续加载该URL；
     */
    private boolean payInterceptorWithUrl(String url, final WebView view) {


        if (!(url.startsWith("http") || url.startsWith("https"))) {
            return true;
        }

        /**
         * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
         */
        final PayTask task = new PayTask(MainActivity.this);
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                final String url = result.getReturnUrl();
                if (!TextUtils.isEmpty(url)) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(url);
                        }
                    });
                }
            }
        });

        /**
         * 判断是否成功拦截
         * 若成功拦截，则无需继续加载该URL；否则继续加载
         */
        if (!isIntercepted)
            view.loadUrl(url);
        return true;
    }


    //定义的方法
    @JavascriptInterface
    public void scan(String token) {
        Log.d(TAG, "scan: token = " + token);//object
        startScan();
    }

    //获取客户端类型的方法
    @JavascriptInterface
    public String getClientType() {
        return "android";
    }


    //微信支付
    @JavascriptInterface
    public void wxpay() {
//        Toast.makeText(this, "微信支付", Toast.LENGTH_SHORT).show();

        // TODO: 2017/9/21 请求支付接口，得到所需参数
        String content = "";
        PayReq req = new PayReq();

        try {
            JSONObject json = new JSONObject(content);
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            req.extData = "app data"; // optional
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
        api.sendReq(req);
    }

    //支付宝支付
    @JavascriptInterface
    public void alipay() {
//        Toast.makeText(this, "支付宝支付", Toast.LENGTH_SHORT).show();
        // TODO: 2017/9/21 支付宝支付接口
        nativeAlipay();

        //
    }


    /**
     * 原生支付宝支付
     */
    private void nativeAlipay() {

        final String orderInfo = "app_id=2015052600090779&biz_content=%7B%22timeout_express%22%3A%2230m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%220.01%22%2C%22subject%22%3A%221%22%2C%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22out_trade_no%22%3A%22IQJZSRC1YMQB5HU%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fdomain.merchant.com%2Fpayment_notify&sign_type=RSA2&timestamp=2016-08-25%2020%3A26%3A31&version=1.0&sign=cYmuUnKi5QdBsoZEAbMXVMmRWjsuUj%2By48A2DvWAVVBuYkiBj13CFDHu2vZQvmOfkjE0YqCUQE04kqm9Xg3tIX8tPeIGIFtsIyp%2FM45w1ZsDOiduBbduGfRo1XRsvAyVAv2hCrBLLrDI5Vi7uZZ77Lo5J0PpUUWwyQGt0M4cj8g%3D";   // 订单信息

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                default:
                    break;
            }
        }

        ;
    };

    /**
     * 扫描方法
     */
    private void startScan() {
        Intent openCameraIntent = new Intent(this, CaptureActivity.class);
        startActivityForResult(openCameraIntent, REQUEST_SCAN_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        Bundle bundle = data.getExtras();
        String scanResult = bundle.getString("result");
        Log.d(TAG, "onActivityResult: scanResult = " + scanResult);

        if (scanResult != null) {
            webView.loadUrl(scanResult);
        }
    }


    private long time;

    //设置回退键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //若位于首页
            String url = webView.getUrl();
            Log.d(TAG, "onKeyDown: url = " + url);
            if (url.equals(URL)) {
                if (System.currentTimeMillis() - time < 3000) {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
//                    return super.onKeyDown(keyCode, event);
                } else {
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    time = System.currentTimeMillis();
                    return true;
                }
            } else {
                //不在首页时，执行WebView后退
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
