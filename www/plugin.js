function AmazonMobileAds() {

	this.init = this.setAppKey = function(key, successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'setAppKey', [key]);
	};

	this.showBannerAd = function(showAtTop, claimSpace, successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'showBannerAd', [showAtTop === true, claimSpace !== false]);
	};

	this.hideBannerAd = function(releaseSpace, successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'hideBannerAd', [releaseSpace !== false]);
	};

	this.claimBannerAdSpace = function(atTop) {
		cordova.exec(null, null, 'AmazonMobileAdsPlugin', 'claimBannerAdSpace', [atTop === true]);
	};

	this.releaseBannerAdSpace = function(atTop) {
		cordova.exec(null, null, 'AmazonMobileAdsPlugin', 'releaseBannerAdSpace', []);
	};
               
   	this.showInterstitialAd = function(successCallback, failureCallback) {
 		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'showInterstitialAd', []);
   	};

	this.enableLogging = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableLogging', [true]);
	};

	this.disableLogging = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableLogging', [false]);
	};

	this.enableTestMode = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableTestMode', [true]);
	};

	this.disableTestMode = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableTestMode', [false]);
	};
}

if(typeof module !== undefined && module.exports) {

	module.exports = AmazonMobileAds;
	// identify the plugin as being smoothie compatible
	module.exports.$mixable = true;
}
