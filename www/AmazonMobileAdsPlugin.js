var exec = require('cordova/exec');

function AmazonMobileAds() {
    
    exec(dispatchEvent, null, "RevMobPlugin", "init");

    this.init = this.setAppKey = function (key, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'setAppKey', [key]);
    };

    this.showBannerAd = function (showAtTop, claimSpace, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'showBannerAd', [showAtTop === true, claimSpace !== false]);
    };

    this.hideBannerAd = function (releaseSpace, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'hideBannerAd', [releaseSpace !== false]);
    };

    this.claimBannerAdSpace = function (atTop) {
        exec(null, null, 'AmazonMobileAdsPlugin', 'claimBannerAdSpace', [atTop === true]);
    };

    this.releaseBannerAdSpace = function (atTop) {
        exec(null, null, 'AmazonMobileAdsPlugin', 'releaseBannerAdSpace', []);
    };

    this.showInterstitialAd = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'showInterstitialAd', []);
    };

    this.enableLogging = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableLogging', [true]);
    };

    this.disableLogging = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableLogging', [false]);
    };

    this.enableTestMode = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableTestMode', [true]);
    };

    this.disableTestMode = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'AmazonMobileAdsPlugin', 'enableTestMode', [false]);
    };
    
    function dispatchEvent(e) {

        var event = new Event(e.type);
        if(e.data) {
            for (var prop in e.data) {

                event[prop] = e.data[prop];
            }
        }
        window.dispatchEvent(event);
    }
}

module.exports = AmazonMobileAds;
// identify the plugin as being smoothie compatible
module.exports.$mixable = true;
