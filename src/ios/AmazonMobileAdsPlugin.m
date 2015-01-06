#import "AmazonMobileAdsPlugin.h"

// TODO: add cordova callbacks
// TODO: add amazon delegate interface
// TODO: make options a property instead of creating it on every call to showBanner
// TODO: add support for displaying varied banners sizes based on device and orientation
// TODO: fix top banner display when the status bar is visible


@interface AmazonMobileAdsPlugin ()

- (void)updateViewFrames;

@end

@implementation AmazonMobileAdsPlugin

- (void)pluginInitialize {
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter]
            addObserver:self
               selector:@selector(deviceOrientationChange:)
                   name:UIDeviceOrientationDidChangeNotification
                 object:nil];

    
    CGSize bannerSize = AmazonAdSize_320x50;

//    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
//        bannerSize = AmazonAdSize_728x90;
//    } else {
//        bannerSize = AmazonAdSize_320x50;
//    }

//    extern const CGSize AmazonAdSize_320x50;
//    extern const CGSize AmazonAdSize_300x250;
//    extern const CGSize AmazonAdSize_728x90;
//    extern const CGSize AmazonAdSize_1024x50;

    self.amazonAdView = [AmazonAdView amazonAdViewWithAdSize:bannerSize];
    self.amazonAdView.delegate = self;
    self.interstitial = [AmazonAdInterstitial amazonAdInterstitial];
    self.interstitial.delegate = self;

    [self.webView.superview addSubview:self.amazonAdView];
    self.amazonAdView.hidden = YES;
    self.bannerAtTop = NO;
    self.testMode = NO;
    self.webView.superview.backgroundColor = [UIColor blackColor];

    // precalculate all frame sizes and positions
    CGSize containerSize = self.webView.superview.frame.size;
    CGFloat max = MAX(containerSize.width, containerSize.height);
    CGFloat min = MIN(containerSize.width, containerSize.height);
    float bannerWidth, bannerHeight;

    bannerWidth = bannerSize.width;
    bannerHeight = bannerSize.height;


    self.bannerFrameTopLandscape = CGRectMake(max / 2 - bannerWidth / 2, 0, bannerWidth, bannerHeight);
    self.bannerFrameBottomLandscape = CGRectMake(max / 2 - bannerWidth / 2, min - bannerHeight, bannerWidth, bannerHeight);
    self.webViewFrameTopLandscape = CGRectMake(0, bannerHeight, max, min - bannerHeight);
    self.webViewFrameBottomLandscape = CGRectMake(0, 0, max, min - bannerHeight);

    self.bannerFrameTopPortrait = CGRectMake(min / 2 - bannerWidth / 2, 0, bannerWidth, bannerHeight);
    self.bannerFrameBottomPortrait = CGRectMake(min / 2 - bannerWidth / 2, max - bannerHeight, bannerWidth, bannerHeight);
    self.webViewFrameTopPortrait = CGRectMake(0, bannerHeight, min, max - bannerHeight);
    self.webViewFrameBottomPortrait = CGRectMake(0, 0, min, max - bannerHeight);

    [self updateViewFrames];
}

- (void)deviceOrientationChange:(NSNotification *)notification {

    [self updateViewFrames];

    if (self.amazonAdView.isHidden == NO) {
        self.webView.frame = self.webViewFrame;
        self.amazonAdView.frame = self.bannerFrame;
    }
}

- (void)updateViewFrames {

    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;

    if (UIInterfaceOrientationIsPortrait(orientation)) {
        // portrait
        if (self.bannerAtTop) {
            self.bannerFrame = self.bannerFrameTopPortrait;
            self.webViewFrame = self.webViewFrameTopPortrait;
        } else {
            self.bannerFrame = self.bannerFrameBottomPortrait;
            self.webViewFrame = self.webViewFrameBottomPortrait;
        }
    } else {

        // landscape
        if (self.bannerAtTop) {
            self.bannerFrame = self.bannerFrameTopLandscape;
            self.webViewFrame = self.webViewFrameTopLandscape;
        } else {
            self.bannerFrame = self.bannerFrameBottomLandscape;
            self.webViewFrame = self.webViewFrameBottomLandscape;
        }
    }
}

- (void)setAppKey:(CDVInvokedUrlCommand *)command {

    NSString *appKey = [command argumentAtIndex:0];
    [[AmazonAdRegistration sharedRegistration] setAppKey:appKey];
}

- (void)showInterstitialAd:(CDVInvokedUrlCommand *)command {

    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = self.testMode;
    [self.interstitial load:options];
}

- (void)showBannerAd:(CDVInvokedUrlCommand *)command {

    self.bannerAtTop = [[command argumentAtIndex:0 withDefault:@"NO"] boolValue];
    [self updateViewFrames];
    self.webView.frame = self.webViewFrame;
    self.amazonAdView.frame = self.bannerFrame;
    self.amazonAdView.hidden = NO;

    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = self.testMode;
    [self.amazonAdView loadAd:options];

}

- (void)hideBannerAd:(CDVInvokedUrlCommand *)command {

    self.webView.frame = self.webView.superview.frame;
    self.amazonAdView.hidden = YES;
}

- (void)enableTestMode:(CDVInvokedUrlCommand *)command {
    self.testMode = [[command argumentAtIndex:0 withDefault:@"YES"] boolValue];
}

- (void)enableLogging:(CDVInvokedUrlCommand *)command {

    BOOL isEnabled = [[command argumentAtIndex:0 withDefault:@"YES"] boolValue];
    [[AmazonAdRegistration sharedRegistration] setLogging:isEnabled];
}

#pragma mark AmazonAdViewDelegate

// @required
- (UIViewController *)viewControllerForPresentingModalView {
    return self.viewController;
}

// @optional
- (void)adViewWillExpand:(AmazonAdView *)view {
    NSLog(@"Will present modal view for an ad. Its time to pause other activities.");
}

// @optional
- (void)adViewDidCollapse:(AmazonAdView *)view {
    NSLog(@"Modal view has been dismissed, its time to resume the paused activities.");
}

// @optional
- (void)adViewDidLoad:(AmazonAdView *)view {
    NSLog(@"Successfully loaded an ad");
}

// @optional
- (void)adViewDidFailToLoad:(AmazonAdView *)view withError:(AmazonAdError *)error {
    NSLog(@"Ad Failed to load. Error code %d: %@", error.errorCode, error.errorDescription);
}


#pragma mark - AmazonAdInterstitialDelegate

- (void)interstitialDidLoad:(AmazonAdInterstitial *)interstitial {
    NSLog(@"Interstial loaded.");
    [self.interstitial presentFromViewController:self.viewController];
}

- (void)interstitialDidFailToLoad:(AmazonAdInterstitial *)interstitial withError:(AmazonAdError *)error {
    NSLog(@"Interstitial failed to load.");
}

- (void)interstitialWillPresent:(AmazonAdInterstitial *)interstitial {
    NSLog(@"Interstitial will be presented.");
}

- (void)interstitialDidPresent:(AmazonAdInterstitial *)interstitial {
    NSLog(@"Interstitial has been presented.");
}

- (void)interstitialWillDismiss:(AmazonAdInterstitial *)interstitial {
    NSLog(@"Interstitial will be dismissed.");
}

- (void)interstitialDidDismiss:(AmazonAdInterstitial *)interstitial {
    NSLog(@"Interstitial has been dismissed.");
}

@end