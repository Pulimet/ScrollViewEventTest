# ScrollViewEventTest
How to send a system scroll event from ScrollView and listen for it in WebView.

# Listen for system scroll event
* jQuery
```javascript
jQuery(document).ready(function($){
	$(window).scroll(function(event, data) {
		console.log(
			"LOG: scrollTop: " + data.scrollTop + 
			"  scrollLeft: " + data.scrollLeft +
			"  webViewYOrigin: " + data.webViewYOrigin +
			"  screenHeight: " + data.screenHeight 
		);
	});	
});
```
* Vanilla
```javascript
document.addEventListener('scroll', function(data){ 
	console.log(
        	"LOG: scrollTop: " + data.scrollTop + 
		"  scrollLeft: " + data.scrollLeft +
		"  webViewYOrigin: " + data.webViewYOrigin +
		"  screenHeight: " + data.screenHeight 
	);
}, true);
```
* Custom event
```javascript
document.addEventListener('nativescroll', function (e) { console.log(
	'scrollTop: ' + e.detail.scrollTop +
	' scrollLeft: ' + e.detail.scrollLeft +
	' webViewYOrigin: ' + e.detail.webViewYOrigin +
	' screenHeight: ' + e.detail.screenHeight
	); }, false);
```


# Sending event:
* jQuery
```javascript
jQuery(document).ready(function($){
	$(window).trigger("scroll", [{scrollTop: 20, scrollLeft: 30, 
			webViewYOrigin : 200, screenHeight: 1200}]);
});
```

* Vanilla
```javascript
var event = new Event(
	'scroll', [{scrollTop: 20, 
			scrollLeft: 30, 	
			webViewYOrigin : 200, 
			screenHeight: 1200}]);
document.dispatchEvent(event);
```

* Custom event
```javascript
document.dispatchEvent(new CustomEvent('nativescroll', 
	{"detail": {"scrollTop": 1 , "scrollLeft": 2, "webViewYOrigin": 3, "screenHeight":4}}));
```


# Sending event (Android):
```java
public static final long DELAY_TIME = 500;
public static final boolean IS_DELAY_DISABLED = false;
private CustomScrollView mScrollView;
...
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
        @Override
        public void onTouchActionUp() {
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


private void sendScrollEvent(int top, int left) {
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

private void invokeJavaScriptCode(String code) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         mWebView.evaluateJavascript(code, null);
    } else {
         mWebView.loadUrl(String.format("javascript:%s", code));
    }
}

// Custom ScrollView
public class CustomScrollView extends ScrollView {

    private boolean mFling;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        mFling = true;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mFling && Math.abs(y - oldy) < 2) {
            mFling = false;
            if(onScrollStoppedListener != null) {
                onScrollStoppedListener.onScrollStopped();
            }
        }
    }
}


```    

# Sending event (IOS):
```swift
@property NSTimeInterval timeStamp;
 //Objective
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

//Swift
if(self.timeStamp + 0.5 < Date().timeIntervalSince1970){
	self.timeStamp = Date().timeIntervalSince1970
	let js = "jQuery(document).ready(function($){$(window).trigger(\"scroll\", [{scrollTop: \(scrollView.contentOffset.y > 0 ? scrollView.contentOffset.y : 0), scrollLeft: \(scrollView.contentOffset.x)}]);});"
	webView.evaluateJavaScript(js, completionHandler: nil)
}

// Debug logs:
func injectScrool() -> String{
	return "jQuery(document).ready(function($){$(window).scroll(function(event, data) {console.log(\"LOG: scrollTop: \" + data.scrollTop +\"  scrollLeft: \" + data.scrollLeft);});});"
}

```



# HTML only to recieve and print the event:
 http://htmlpreview.github.io/?https://github.com/Pulimet/ScrollViewEventTest/blob/master/html/index12.html
```html
<html><head><title></title>
     <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script>
         $(window).scroll(function(event, data) {
                      $("#list").append(
                           "<li>scrollTop: " + data.scrollTop + 
                           "  scrollLeft: " + data.scrollLeft +
                           "  webViewYOrigin: " + data.webViewYOrigin +
                           "  screenHeight: " + data.screenHeight +
                           "</br></br>"
                      );
         });       
        </script>
</head>
<body style="overflow: hidden;">
     <h3>It is WebView</h3></br></br></br></br>
     <ul id="list"></ul>
</body></html>
```
    

# HTML full test:
 http://htmlpreview.github.io/?https://github.com/Pulimet/ScrollViewEventTest/blob/master/html/scroll_check5.html
```html
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script>
		$(window).scroll(function(event, data) {
			console.log(
				"LOG: scrollTop: " + data.scrollTop + 
				"  scrollLeft: " + data.scrollLeft +
				"  webViewYOrigin: " + data.webViewYOrigin +
				"  screenHeight: " + data.screenHeight 
			);
			
			$("#list").append(
			"<li>LOG: scrollTop: " + data.scrollTop + 
			"  scrollLeft: " + data.scrollLeft +
			"  webViewYOrigin: " + data.webViewYOrigin +
                        "  screenHeight: " + data.screenHeight +
                        "</br></br>"
			);
		});	

		
		$(document).ready(function() {
			$("button").click(function() {
  				$(window).trigger("scroll", [{scrollTop: 20, scrollLeft: 30, 
						webViewYOrigin : 200, screenHeight: 1200}]);
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


# Video demo on Android
[![Alt text for your video](http://img.youtube.com/vi/q_Uj-C11eww/0.jpg)](https://youtu.be/q_Uj-C11eww)


