package net.alexandroid.scrollvieweventtest;

import android.annotation.SuppressLint;
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
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private ScrollView mScrollView;
    public static final long DELAY_TIME = 1000;
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
        mWebView.loadUrl("http:///www.alexandroid.net");
        setWebViewLogsListener();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WebSettings webViewSettings = mWebView.getSettings();
            webViewSettings.setJavaScriptEnabled(true);

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    onPageLoaded();
                }
            });
        }

    }

    private void setScrollViewListener() {
        mScrollView = findViewById(R.id.scrollView);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mTime + DELAY_TIME < System.currentTimeMillis()) {
                    mTime = System.currentTimeMillis();
                    sendScrollEvent(mScrollView.getScrollY(), mScrollView.getScrollX());
                }
            }
        });
    }

    private void onPageLoaded() {
        Log.e("WebViewLogs", "onPageLoaded");

        // Register scroll event listener
        invokeJavaScriptCode("" +
                "jQuery(document).ready(function($){" +
                "$(window).scroll(function(event, data) {" +
                "console.log(\"LOG: top: \" + data.top + \"  left: \" + data.left);" +
                "});" +
                "});");
    }

    @SuppressLint("DefaultLocale")
    private void sendScrollEvent(int top, int left) {
        invokeJavaScriptCode(
                String.format("" +
                                "jQuery(document).ready(" +
                                "function($){" +
                                "$(window).trigger(\"scroll\", [{top: %d , left: %d}]);" +
                                "}" +
                                ");"
                        , top, left)
        );
    }

    private void setWebViewLogsListener() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewLogs", consoleMessage.message());
                return true;
            }
        });
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
