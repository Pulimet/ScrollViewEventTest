package net.alexandroid.scrollvieweventtest;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    public static final long DELAY_TIME = 500;
    public static final boolean IS_DELAY_DISABLED = false;

    private WebView mWebView;
    private CustomScrollView mScrollView;
    private long mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWebView();
        setScrollViewListener();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        mWebView = findViewById(R.id.webView);
        //mWebView.loadUrl("http:///www.alexandroid.net");
        mWebView.loadUrl("http://htmlpreview.github.io/?https://github.com/Pulimet/ScrollViewEventTest/blob/master/html/index13.html");

        setWebViewLogsListener();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // API 17+
            WebSettings webViewSettings = mWebView.getSettings();
            webViewSettings.setJavaScriptEnabled(true);
            setOnPageLoadedListener();
        }
    }

    /**
     * Just prints the logs of WebView
     */
    private void setWebViewLogsListener() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewLogs", consoleMessage.message());
                return true;
            }
        });
    }

    private void setOnPageLoadedListener() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                onPageLoaded();
            }
        });
    }

    private void setScrollViewListener() {
        mScrollView = findViewById(R.id.scrollView);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                onScrollViewScroll(false);
            }
        });

        mScrollView.setOnScrollStoppedListener(new CustomScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped() {
                onScrollViewScroll(true);
            }
        });

    }

    private void onScrollViewScroll(boolean force) {
        if (IS_DELAY_DISABLED || force || mTime + DELAY_TIME < System.currentTimeMillis()) {
            mTime = System.currentTimeMillis();
            sendScrollEvent(mScrollView.getScrollY(), mScrollView.getScrollX());
        }
    }

    /**
     * Called when page is loaded.
     * Simulate event listener.
     * Web developers probably would use it to subscribe for scrolling events.
     */
    private void onPageLoaded() {
        Log.e("WebViewLogs", "onPageLoaded");
        // Register scroll event listener
        invokeJavaScriptCode("        jQuery(document).ready(function($) {\n" +
                "            $(window).scroll(function(event, data) {\n" +
                "                console.log(\n" +
                "                        \"LOG: scrollTop: \" + data.scrollTop +\n" +
                "                                \"  scrolLeft: \" + data.scrolLeft +\n" +
                "                                \"  webViewYOrigin: \" + data.webViewYOrigin +\n" +
                "                                \"  screenHeight: \" + data.screenHeight\n" +
                "                );\n" +
                "            });\n" +
                "        });");
    }

    @SuppressLint("DefaultLocale")
    private void sendScrollEvent(int top, int left) {
        Log.d("WebViewLogs", "sendScrollEvent: top: " + top);
        int webViewTop = mWebView.getTop();
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        invokeJavaScriptCode(
                String.format("" +
                                "jQuery(document).ready(" +
                                "function($){" +
                                "$(window).trigger(\"scroll\", " +
                                "[{scrollTop: %d , scrollLeft: %d, webViewYOrigin: %d, screenHeight: %d}]);" +
                                "}" +
                                ");"
                        , top, left, webViewTop, screenHeight)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private void invokeJavaScriptCode(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(code, null);
        } else {
            mWebView.loadUrl(String.format("javascript:%s", code));
        }
    }
}
