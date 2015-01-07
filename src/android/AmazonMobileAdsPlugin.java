package com.blakgeek.cordova.plugin.amazonmobileads;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.amazon.device.ads.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

// TODO: add separate methods for loading and displaying interstitial
// TODO: add callback javascript callbacks
// TODO: add support for controlling the ad sizes

public class AmazonMobileAdsPlugin extends CordovaPlugin {

    private static final String LOGTAG = "AmazonMobileAdsPlugin";
    private boolean bannerAtTop = false;
    private AdLayout bannerAdView = null;
    private InterstitialAd interstitialAd = null;
    private CallbackEnabledInterstitialAdListener interstitialAdListener;
    private CallbackEnabledAdListener bannerAdListener;

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        // create banner view
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerAdView = new AdLayout(AmazonMobileAdsPlugin.this.cordova.getActivity(), AdSize.SIZE_320x50);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = bannerAtTop ? Gravity.TOP : Gravity.BOTTOM;
                // explicitly set the height so that it doesn't flash when reloading
                // multiply by the density to ensure that doesn't appear too small on the device
                float density = AmazonMobileAdsPlugin.this.cordova.getActivity().getResources().getDisplayMetrics().density;
                params.height = Math.round(AdSize.SIZE_320x50.getHeight() * density);
                bannerAdView.setLayoutParams(params);
                bannerAdView.setVisibility(View.VISIBLE);
                bannerAdListener = new CallbackEnabledAdListener();
                bannerAdView.setListener(bannerAdListener);
                ViewGroup parent = (ViewGroup) AmazonMobileAdsPlugin.this.webView.getParent();
                parent.addView(bannerAdView, bannerAtTop ? 0 : parent.indexOfChild(webView) + 1);
            }
        });
        // create interstitial
        interstitialAd = new InterstitialAd(cordova.getActivity());
        interstitialAdListener = new CallbackEnabledInterstitialAdListener(interstitialAd);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            Log.i(LOGTAG, action);

            if (action.equals("setAppKey")) {

                AdRegistration.setAppKey(args.getString(0));
                callbackContext.success();
            } else if (action.equals("enableTestMode")) {

                AdRegistration.enableTesting(args.getBoolean(0));
                callbackContext.success();
            } else if (action.equals("enableLogging")) {

                AdRegistration.enableLogging(args.getBoolean(0));
                callbackContext.success();
            } else if (action.equals("showBannerAd")) {

                showBannerAd(args, callbackContext);
            } else if (action.equals("hideBannerAd")) {

                hideBannerAd(callbackContext);
            } else if (action.equals("showInterstitialAd")) {

                showInterstitialAd(callbackContext);
            } else {

                callbackContext.error("Unknown Action");
                return false;
            }
            return true;
        } catch (JSONException e) {

            Log.e("AmazonMobileAdsPlugin", e.getMessage());
            callbackContext.error("AmazonMobileAdsPlugin: " + e.getMessage());
            return false;
        }
    }

    private void showInterstitialAd(CallbackContext callbackContext) {
        interstitialAdListener.setCallbackContext(callbackContext);
        if(!interstitialAd.loadAd()) {
            callbackContext.error("Unable to load interstitial ad");
        }
    }

    private void hideBannerAd(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bannerAdView.getVisibility() == View.VISIBLE) {
                    bannerAdView.setVisibility(View.GONE);
                }
                callbackContext.success();
            }
        });
    }

    protected void showBannerAd(JSONArray args, CallbackContext callbackContext) throws JSONException {

        final boolean showAtTop = args.getBoolean(0);
        bannerAdListener.setCallbackContext(callbackContext);
        if(bannerAdView.loadAd()) {

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (showAtTop != bannerAtTop) {
                        bannerAtTop = showAtTop;
                        ViewGroup parent = (ViewGroup) webView.getParent();
                        parent.removeView(bannerAdView);
                        parent.addView(bannerAdView, bannerAtTop ? 0 : parent.indexOfChild(webView) + 1);
                    }

                    if (bannerAdView.getVisibility() != View.VISIBLE) {
                        bannerAdView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            callbackContext.error("Unable to banner ad");
        }
    }

    private class CallbackEnabledInterstitialAdListener extends DefaultAdListener {

        CallbackContext callbackContext;
        InterstitialAd interstitialAd;

        public CallbackEnabledInterstitialAdListener(InterstitialAd interstitialAd) {

            this.interstitialAd = interstitialAd;
            this.interstitialAd.setListener(this);
        }

        public void setCallbackContext(CallbackContext callbackContext) {
            if (this.callbackContext != null && !this.callbackContext.isFinished()) {
                callbackContext.error("Too slow.  Your request got stomped on by a newer one");
            }
            this.callbackContext = callbackContext;
        }

        @Override
        public void onAdLoaded(Ad ad, AdProperties adProperties) {

            interstitialAd.showAd();
            callbackContext.success();
        }

        @Override
        public void onAdFailedToLoad(Ad ad, AdError error) {

            callbackContext.error(error.getMessage());
        }

    }

    private class CallbackEnabledAdListener extends DefaultAdListener {

        CallbackContext callbackContext;

        public void setCallbackContext(CallbackContext callbackContext) {
            if (this.callbackContext != null && !this.callbackContext.isFinished()) {
                callbackContext.error("Too slow.  Your request got stomped on by a newer one");
            }
            this.callbackContext = callbackContext;
        }

        @Override
        public void onAdLoaded(Ad ad, AdProperties adProperties) {

            callbackContext.success();
        }

        @Override
        public void onAdFailedToLoad(Ad ad, AdError error) {

            callbackContext.error(error.getMessage());
        }

    }
}
