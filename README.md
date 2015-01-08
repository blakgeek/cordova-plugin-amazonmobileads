# Amazon Ads Cordova Plugin
Add support for Amazon Mobile Ads to your Cordova and Phonegap based mobile apps.

## How do I install it? ##

* If you're like me and using [CLI](http://cordova.apache.org/):
```
cordova plugin add https://github.com/blakgeek/cordova-plugin-amazonmobileads
```

or

```
phonegap local plugin add https://github.com/blakgeek/cordova-plugin-amazonmobileads
```

## WARNING: iOS Cordova Registry
****Installing this plugin directly from Cordova Registry results in Xcode using a broken `AmazonAd.framework`, this is because the current publish procedure to NPM breaks symlinks [CB-6092](https://issues.apache.org/jira/browse/CB-6092). Please install the plugin through through the github url or re-add the `AmazonAd.framework` manually.****

## How do I use it? ##

```javascript
document.addEventListener('deviceready', function() {

	window.amazonads = new AmazonMobileAds();

	// get things started by passing in you app id
	amazonads.init('<you app id>', function() {
		console.log('super dope it worked');
	}, function(err) {
		console.error(['oh crap', err]);
	});

	// show a banner at the top the screen (if no arguments is passed it will default showing at the bottom)
	amazonads.showBannerAd(true, function() {
		console.log('oh snap I got a banner at the bottom');
	}, function(err) {
		console.error(['oh crap', err]);
	});

	// show a banner at the bottom of the screen
	amazonads.showBannerAd(false, function() {
		console.log('what what see the banner at the bottom');
	}, function(err) {
		console.error(['oh crap', err]);
	});

	// hide the banner
	amazonads.hideBannerAd(function() {
		console.log('now you see me now you do not');
	}, function(err) {
		console.error(['oh crap', err]);
	});

	// show and interstitial
	amazonads.showInterstitialAd(function() {
		console.log('now that is a big ole interstitial');
	}, function(err) {
		console.error(['oh crap', err]);
	});

	// enable test mode without ads
	amazonads.enableTestMode(function() {
		console.log('testing 1 2 1 2');
	}, function(err) {
		console.error('this will never ever happen');
	});

	// enable test mode without ads
	amazonads.disableTestMode(function() {
		console.log('shit just got real');
	}, function(err) {
		console.error('this will never ever happen');
	});

	// enable test mode without ads
	amazonads.enableLogging(function() {
		console.log('messages are being logged');
	}, function(err) {
		console.error('this will never ever happen');
	});

	// enable test mode without ads
	amazonads.enableTestMode(function() {
		console.log('no mo loggin');
	}, function(err) {
		console.error('this will never ever happen');
	});
}, false);
```

For complete working example checkout [the demo project](https://github.com/blakgeek/cordova-plugin-amazonmobileads-demo)