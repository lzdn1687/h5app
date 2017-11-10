package com.kesitong.shop;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 天朗信息技术有限公司
 *
 * @author vintage
 *         Created by Asus on 2017/10/30.
 * @link http://www.sd0534.com/
 * $desc$
 */
public class ScrollWebView extends WebView {

    public static final String TAG = ScrollWebView.class.getSimpleName();

    public OnScrollChangeListener listener;

    public ScrollWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        float webcontent = getContentHeight() * getScale();// webview的高度
        float webnow = getHeight() + getScrollY();// 当前webview的高度
//        Log.e(TAG, "webview.getScrollY()====>>" + getScrollY());
//        if (Math.abs(webcontent - webnow) < 1) {
//            // 已经处于底端
//            // Log.i("TAG1", "已经处于底端");
//            listener.onPageEnd(l, t, oldl, oldt);
//        } else if (getScrollY() == 0) {
//            // Log.i("TAG1", "已经处于顶端");
//            listener.onPageTop(l, t, oldl, oldt);
//        } else {
//            listener.onScrollChanged(l, t, oldl, oldt);
//        }

        // Log.i("TAG1", "已经处于顶端");
        listener.onPageTop(getScrollY() == 0);

    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {

        this.listener = listener;

    }

    public interface OnScrollChangeListener {
//        public void onPageEnd(int l, int t, int oldl, int oldt);

//        public void onPageTop(int l, int t, int oldl, int oldt);

        //        public void onScrollChanged(int l, int t, int oldl, int oldt);
        void onPageTop(boolean top);
    }
}
