var exec = require('cordova/exec');

function AmazonMobileAds() {

    var PLUGIN_NAME = "AmazonMobileAdsPlugin";

    exec(dispatchEvent, null, PLUGIN_NAME, "init");

    this.init = this.setAppKey = function (key, successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'setAppKey', [key]);
    };

    this.showBannerAd = function (showAtTop, claimSpace, successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'showBannerAd', [showAtTop === true, claimSpace !== false]);
    };

    this.hideBannerAd = function (releaseSpace, successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'hideBannerAd', [releaseSpace !== false]);
    };

    this.claimBannerAdSpace = function (atTop) {
        exec(null, null, PLUGIN_NAME, 'claimBannerAdSpace', [atTop === true]);
    };

    this.releaseBannerAdSpace = function (atTop) {
        exec(null, null, PLUGIN_NAME, 'releaseBannerAdSpace', []);
    };

    this.showInterstitialAd = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'showInterstitialAd', []);
    };

    this.enableLogging = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'enableLogging', [true]);
    };

    this.disableLogging = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'enableLogging', [false]);
    };

    this.enableTestMode = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'enableTestMode', [true]);
    };

    this.disableTestMode = function (successCallback, failureCallback) {
        exec(successCallback, failureCallback, PLUGIN_NAME, 'enableTestMode', [false]);
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
