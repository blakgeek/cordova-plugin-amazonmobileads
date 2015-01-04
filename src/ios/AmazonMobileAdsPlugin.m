#import "AmazonMobileAdsPlugin.h"

// TODO: add cordova callbacks
// TODO: add amazon delegate interface
// TODO: make options a property instead of creating it on every call to showBanner

@interface AmazonMobileAdsPlugin()

-(void) resizeFrames;

@end

@implementation AmazonMobileAdsPlugin

-(void) pluginInitialize {
    
    self.amazonAdView = [AmazonAdView amazonAdViewWithAdSize: AmazonAdSize_320x50];
    self.amazonAdView.delegate = self;
    self.interstitial = [AmazonAdInterstitial amazonAdInterstitial];
    self.interstitial.delegate = self;

    [self.webView.superview addSubview:self.amazonAdView];
    self.amazonAdView.hidden = YES;
    self.bannerAtTop = NO;
    self.testMode = NO;

    CGSize containerSize = self.webView.superview.frame.size;
    
    // precalculate all sizes here
    CGFloat max = MAX(containerSize.width, containerSize.height);
    CGFloat min = MIN(containerSize.width, containerSize.height);
    
    // TODO: get min and max measurements and use them to setup the frames below
    // TODO: add properties for landscape and portrait rectangles
    
    self.bannerFrameTopLandscape = CGRectMake(0, 0, max, 50);
    self.bannerFrameBottomLandscape = CGRectMake(0, min - 50, max, 50);
    self.webViewFrameTopLandscape = CGRectMake(0, 50, max, min - 50);
    self.webViewFrameBottomLandscape = CGRectMake(0, 0, max, min - 50);
    
    self.bannerFrameTopPortrait = CGRectMake(0, 0, min, 50);
    self.bannerFrameBottomPortrait = CGRectMake(0, max - 50, min, 50);
    self.webViewFrameTopPortrait = CGRectMake(0, 50, min, max - 50);
    self.webViewFrameBottomPortrait = CGRectMake(0, 0, min, max - 50);

    [self resizeFrames];
}

-(void) resizeFrames {

    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    
    if(UIInterfaceOrientationIsPortrait(orientation)) {
        // portrait
        if(self.bannerAtTop) {
            self.bannerFrame = self.bannerFrameTopPortrait;
            self.webViewFrame = self.webViewFrameTopPortrait;
        } else {
            self.bannerFrame = self.bannerFrameBottomPortrait;
            self.webViewFrame = self.webViewFrameBottomPortrait;
        }
    } else {
        
        // landscape
        if(self.bannerAtTop) {
            self.bannerFrame = self.bannerFrameTopLandscape;
            self.webViewFrame = self.webViewFrameTopLandscape;
        } else {
            self.bannerFrame = self.bannerFrameBottomLandscape;
            self.webViewFrame = self.webViewFrameBottomLandscape;
        }
    }
}

-(void) setAppKey:(CDVInvokedUrlCommand *)command {
    
    NSString *appKey = [command argumentAtIndex:0];
    [[AmazonAdRegistration sharedRegistration] setAppKey: appKey];
}

- (void) showInterstitialAd: (CDVInvokedUrlCommand*)command {

    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = self.testMode;
    [self.interstitial load:options];
}

-(void) showBannerAd:(CDVInvokedUrlCommand *)command {
    
    self.bannerAtTop = [[command argumentAtIndex:0 withDefault: @"NO"] boolValue];
    [self resizeFrames];
	self.webView.frame = self.webViewFrame;
	self.amazonAdView.frame = self.bannerFrame;
    self.amazonAdView.hidden = NO;
    
    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = self.testMode;
    [self.amazonAdView loadAd:options];
    
}

-(void) hideBannerAd:(CDVInvokedUrlCommand *)command {
    
    self.webView.frame = self.webView.superview.frame;
    self.amazonAdView.hidden = YES;
}

-(void) enableTestMode:(CDVInvokedUrlCommand *)command {
    self.testMode = [[command argumentAtIndex:0 withDefault: @"YES"] boolValue];
}

-(void) enableLogging:(CDVInvokedUrlCommand *)command {
    
    BOOL isEnabled = [[command argumentAtIndex:0 withDefault: @"YES"] boolValue];
    [[AmazonAdRegistration sharedRegistration] setLogging: isEnabled];
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
- (void)interstitialDidLoad:(AmazonAdInterstitial *)interstitial
{
    NSLog(@"Interstial loaded.");
    [self.interstitial presentFromViewController:self.viewController];
}

- (void)interstitialDidFailToLoad:(AmazonAdInterstitial *)interstitial withError:(AmazonAdError *)error
{
    NSLog(@"Interstitial failed to load.");
}

- (void)interstitialWillPresent:(AmazonAdInterstitial *)interstitial
{
    NSLog(@"Interstitial will be presented.");
}

- (void)interstitialDidPresent:(AmazonAdInterstitial *)interstitial
{
    NSLog(@"Interstitial has been presented.");
}

- (void)interstitialWillDismiss:(AmazonAdInterstitial *)interstitial
{
    NSLog(@"Interstitial will be dismissed.");
}

- (void)interstitialDidDismiss:(AmazonAdInterstitial *)interstitial
{
    NSLog(@"Interstitial has been dismissed.");
}

@end