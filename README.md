# ScrollViewEventTest
How to send a system scroll event from ScrollView and listen for it in WebView.

# Listen for system scroll event
```javascript
jQuery(document).ready(function($){
	$(window).scroll(function(event, data) {
  		console.log("LOG: top: " + data.top + "  left: " + data.left);
	});	
});
```

# Sending event from android with:
```javascript
jQuery(document).ready(function($){
	$(window).trigger("scroll", [{top: 20, left: 30}]);
});
```

<img src="https://raw.githubusercontent.com/Pulimet/ScrollViewEventTest/master/art/webviewlogs.png">


