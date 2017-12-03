# ScrollViewEventTest
How to send a system scroll event from ScrollView and listen for it in WebView.

# Listen for system scroll event
```javascript
jQuery(document).ready(function($){
	$(window).scroll(function(event, data) {
  		console.log("LOG: scrollTop: " + data.scrollTop + "  scrollLeft: " + data.scrollLeft);
	});	
});
```

# Sending event:
```javascript
jQuery(document).ready(function($){
	$(window).trigger("scroll", [{scrollTop: 20, scrollLeft: 30}]);
});
```

# Sending event (Android):
```java
public static final long DELAY_TIME = 1000;
public static final boolean IS_DELAY_DISABLED = false;
...
private void setScrollViewListener() {
  mScrollView = findViewById(R.id.scrollView);
  mScrollView.getViewTreeObserver().addOnScrollChangedListener(
          new ViewTreeObserver.OnScrollChangedListener() {
    @Override
    public void onScrollChanged() {
      if (IS_DELAY_DISABLED || mTime + DELAY_TIME < System.currentTimeMillis()) {
        mTime = System.currentTimeMillis();
        sendScrollEvent(mScrollView.getScrollY(), mScrollView.getScrollX());
      }
    }
  });
}

private void sendScrollEvent(int top, int left) {
  invokeJavaScriptCode(
    String.format("" +
      "jQuery(document).ready(" +
        "function($){" +
          "$(window).trigger(\"scroll\", [{scrollTop: %d , scrollLeft: %d}]);" +
        "}" +
     ");"
   , top, left)
 );
}

private void invokeJavaScriptCode(String code) {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
     mWebView.evaluateJavascript(code, null);
   } else {
     mWebView.loadUrl(String.format("javascript:%s", code));
   }
}

```    

# Sending event (IOS):
```swift
- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    NSString *js = [[NSString alloc]initWithFormat:
            @"jQuery(document).ready(function($){
	        $(window).trigger(\"scroll\", [{scrollTop: %f, scrollLeft: %f}]);
	    });",scrollView.contentOffset.y,scrollView.contentOffset.x];
    [self.webView evaluateJavaScript:js completionHandler:nil];
}
```
    

<img src="https://raw.githubusercontent.com/Pulimet/ScrollViewEventTest/master/art/webviewlogs.png">


