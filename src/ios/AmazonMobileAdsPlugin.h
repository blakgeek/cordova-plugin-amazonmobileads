#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <AmazonAd/AmazonAdView.h>
#import <AmazonAd/AmazonAdOptions.h>
#import <AmazonAd/AmazonAdRegistration.h>
#import <AmazonAd/AmazonAdInterstitial.h>
#import <AmazonAd/AmazonAdError.h>

@interface AmazonMobileAdsPlugin : CDVPlugin <AmazonAdViewDelegate, AmazonAdInterstitialDelegate>

- (void) setAppKey: (CDVInvokedUrlCommand*) command;
- (void) enableTestMode:(CDVInvokedUrlCommand*)command;
- (void) enableLogging:(CDVInvokedUrlCommand*)command;
- (void) showBannerAd: (CDVInvokedUrlCommand*)command;
- (void) hideBannerAd: (CDVInvokedUrlCommand*)command;
- (void) showInterstitialAd: (CDVInvokedUrlCommand*)command;

@property (nonatomic) NSString* interstitialAdCallbackId;
@property (nonatomic) NSString* bannerAdCallbackId;
@property (nonatomic) CGRect bannerFrameTopLandscape;
@property (nonatomic) CGRect bannerFrameBottomLandscape;
@property (nonatomic) CGRect webViewFrameTopLandscape;
@property (nonatomic) CGRect webViewFrameBottomLandscape;
@property (nonatomic) CGRect bannerFrameTopPortrait;
@property (nonatomic) CGRect bannerFrameBottomPortrait;
@property (nonatomic) CGRect webViewFrameTopPortrait;
@property (nonatomic) CGRect webViewFrameBottomPortrait;
@property (nonatomic) CGRect webViewFrame;
@property (nonatomic) CGRect bannerFrame;
@property (nonatomic, getter=isBannerAtTop) BOOL bannerAtTop;
@property (nonatomic, strong) AmazonAdView *amazonAdView;
@property (nonatomic, strong) AmazonAdInterstitial *interstitial;
@property (nonatomic) BOOL testMode;
@end