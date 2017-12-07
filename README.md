# ScrollViewEventTest
How to send a system scroll event from ScrollView and listen for it in WebView.

[Video demo on Android](https://youtu.be/q_Uj-C11eww)

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
public static final long DELAY_TIME = 500;
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
@property NSTimeInterval timeStamp;
 
- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
     if(self.timeStamp + 20.0 < [[NSDate date] timeIntervalSince1970]){
        self.timeStamp = [[NSDate date] timeIntervalSince1970];
        NSString *js = [[NSString alloc]initWithFormat:
            @"jQuery(document).ready(function($){
	        $(window).trigger(\"scroll\", [{scrollTop: %f, scrollLeft: %f}]);
	    });",scrollView.contentOffset.y,scrollView.contentOffset.x];
        [self.webView evaluateJavaScript:js completionHandler:nil];
    }
}
```

# HTML:
 http://htmlpreview.github.io/?https://github.com/Pulimet/ScrollViewEventTest/blob/master/html/scroll_check.html
```htm
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script>
		$(window).scroll(function(event, data) {
			console.log("LOG: scrollTop: " + data.scrollTop + "  scrolLeft: " + data.scrolLeft);
			$("#list").append("<li>LOG: scrollTop: " + data.scrollTop + "  scrolLeft: " + data.scrolLeft);
		});	

		
		$(document).ready(function() {
			$("button").click(function() {
  				$(window).trigger("scroll", [{scrollTop: 20, scrolLeft: 30}]);
  			});
		});

	</script>
</head>
<body style="overflow: hidden;">
	<h1 id="elem">Hello!</h1>
	<button id="button" type="button" style="
		background-color: #4CAF50; 
    	border: none;
    	color: white;
    	padding: 15px 32px;
    	text-align: center;
    	text-decoration: none;
    	display: inline-block;
    	font-size: 16px;">SEND EVENT</button>
	
	<ul id="list">
	<ul>
</body>
</html>
```

# HTML only to recieve and print the event:
 http://htmlpreview.github.io/?https://github.com/Pulimet/ScrollViewEventTest/blob/master/html/index.html
```html
<html><head><title></title>
     <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script>
         $(window).scroll(function(event, data) {
                      $("#list").append("<li>scrollTop: " + data.scrollTop + "  scrolLeft: " + data.scrollLeft);
         });       
        </script>
</head>
<body style="overflow: hidden;"><ul id="list"><ul></body></html>
```
    

<img src="https://raw.githubusercontent.com/Pulimet/ScrollViewEventTest/master/art/webviewlogs.png">


