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

# Sending event from android with:
```javascript
jQuery(document).ready(function($){
	$(window).trigger("scroll", [{scrollTop: 20, scrollLeft: 30}]);
});
```

# Sending event from IOS with:
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


