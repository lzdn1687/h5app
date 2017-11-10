package com.yiwule.android;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public static final String TAG = "ExampleUnitTest";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testString() throws Exception {
        String scanResult = "http://qll.jiahetianlang.com/App/goodsQrCode.html?act=setOrderStatus&orderid=11099";
        int index = scanResult.lastIndexOf("&");
//        Log.d(TAG, "testString: index = " + index);
        String url = scanResult.substring(0, index);
//        Log.d(TAG, "testString: url = " + url);

//        assertEquals(url,
//                "http://qll.jiahetianlang.com/App/goodsQrCode.html?act=setOrderStatus");

        int index2 = scanResult.lastIndexOf("=") + 1;
        String orderid = scanResult.substring(index2, scanResult.length());
        assertEquals(orderid, "11099");
    }
}