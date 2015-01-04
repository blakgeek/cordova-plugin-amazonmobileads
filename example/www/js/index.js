document.addEventListener('deviceready', function() {

    window.ama = new AmazonMobileAds;
    ama.enableLogging();
    // ama.setAppKey('<your app key here>');
    ama.enableTestMode(true);
    ama.setAppKey('817f9a6b6c92485a8e9774d991d28b06');

    window.topBannerBtn = document.getElementById('topBanner');
    window.bottomBannerBtn = document.getElementById('bottomBanner');
    window.hideBannerBtn = document.getElementById('hideBanner');
    window.enableLoggingBtn = document.getElementById('enableLogging');
    window.disableLoggingBtn = document.getElementById('disableLogging');
    window.showInterstitialBtn = document.getElementById('showInterstitial');


    topBannerBtn.addEventListener('click', function() {
        ama.showBannerAd(true);
    }, false);

    bottomBannerBtn.addEventListener('click', function() {
        ama.showBannerAd();
    }, false);

    hideBannerBtn.addEventListener('click', function() {
        ama.hideBannerAd();
    }, false);

    enableLoggingBtn.addEventListener('click', function() {
        ama.enableLogging();
    }, false);

    disableLoggingBtn.addEventListener('click', function() {
        ama.enableLogging(false);
    }, false);

    showInterstitialBtn.addEventListener('click', function() {
        ama.showInterstitialAd();
    }, false);

}, false);