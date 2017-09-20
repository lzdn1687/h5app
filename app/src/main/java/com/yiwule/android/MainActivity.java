package com.yiwule.android;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

import com.xys.libzxing.zxing.activity.CaptureActivity;

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
    public static final String URL = "http://www.cst01.com/?temp=wap";
    public static final String URL_BASE = "http://www.cst01.com/";


    private TextView progress;
    public static final int REQUEST_SCAN_CODE = 0;
    public static final int REQUEST_CAMERA_PERMISSION = 10000;

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


    }

    private void initSettings() {

        webView.setWebViewClient(new WebViewClient() {
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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


    //设置返回键动作（防止按返回键直接退出程序)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String url = webView.getUrl();
        Log.d(TAG, "onKeyDown: url = " + url);

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当webview处于第一页面时,直接退出程序
            if (webView.getUrl().equals(URL) || webView.getUrl().equals(URL_BASE)) {
                if (!isExit) {
                    isExit = true;
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    // 利用handler延迟发送更改状态信息
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    return true;
                } else {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else {
                //当webview不是处于第一页面时，返回上一个页面
                webView.goBack();
                return true;
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    //设置返回键动作（防止按返回键直接退出程序)
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        String url = webView.getUrl();
//        String originalUrl = webView.getOriginalUrl();
//        Log.d(TAG, "onKeyDown: url = "+url);
//        Log.d(TAG, "onKeyDown: originalUrl = "+originalUrl);
//
//        if(keyCode==KeyEvent.KEYCODE_BACK) {
//            if(webView.canGoBack()) {//当webview不是处于第一页面时，返回上一个页面
//                webView.goBack();
//                return true;
//            } else /*if (webView.getUrl().equals(URL))*/{//当webview处于第一页面时,直接退出程序
//                if (!isExit) {
//                    isExit = true;
//                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                            Toast.LENGTH_SHORT).show();
//                    // 利用handler延迟发送更改状态信息
//                    mHandler.sendEmptyMessageDelayed(0, 2000);
//                    return true;
//                } else {
//                    finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//
//            }
//
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private boolean isExit;


}
