package com.blakgeek.cordova.plugin.amazonmobileads;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.amazon.device.ads.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

// TODO: add separate methods for loading and displaying interstitial
// TODO: add callback javascript callbacks
// TODO: add support for controlling the ad sizes

public class AmazonMobileAdsPlugin extends CordovaPlugin {

    private static final String LOGTAG = "[AmazonMobileAdsPlugin]";
    private boolean bannerAtTop = true;
    private AdLayout bannerAdView = null;
    private InterstitialAd interstitialAd = null;
    private CallbackEnabledInterstitialAdListener interstitialAdListener;
    private CallbackEnabledAdListener bannerAdListener;
    private ViewGroup blender;
    private ViewGroup webViewContainer;
    private View webView;

    @Override
    protected void pluginInitialize() {

        // look for the smoothie parent view
        if(super.webView instanceof View) {
            webView = (View) super.webView;
        } else {
            webView = (View) super.webView.getView().getParent();
        }
        webViewContainer = (ViewGroup) webView.getParent();
        bannerAdView = new AdLayout(AmazonMobileAdsPlugin.this.cordova.getActivity(), AdSize.SIZE_320x50);
        bannerAdListener = new CallbackEnabledAdListener();
        bannerAdView.setListener(bannerAdListener);

        // create banner view
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                plugInBlender();
                bannerAdView.setVisibility(View.INVISIBLE);
                blender.addView(bannerAdView);
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

                hideBannerAd(args, callbackContext);
            } else if (action.equals("showInterstitialAd")) {

                showInterstitialAd(callbackContext);
            } else if ("claimBannerAdSpace".equals(action)) {

                // TODO: implement code to claim space

            } else if ("releaseBannerAdSpace".equals(action)) {

                // TODO: implement code to release space
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
        if (!interstitialAd.loadAd()) {
            callbackContext.error("Unable to load interstitial ad");
        }
    }

    private void hideBannerAd(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final boolean releaseAdSpace = args.getBoolean(0);

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (releaseAdSpace) {
                    blender.setVisibility(View.GONE);
                }
                bannerAdView.setVisibility(View.INVISIBLE);
                callbackContext.success();
            }
        });
    }

    protected void showBannerAd(JSONArray args, CallbackContext callbackContext) throws JSONException {

        final boolean showAtTop = args.getBoolean(0);
        // TODO: figure out how to claim and not claim space
        final boolean claimAdSpace = args.getBoolean(1);

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showAtTop != bannerAtTop) {
                    bannerAtTop = showAtTop;
                    webViewContainer.removeView(blender);
                    webViewContainer.addView(blender, bannerAtTop ? 0 : webViewContainer.getChildCount());

                }

                blender.setVisibility(View.VISIBLE);
            }
        });

        bannerAdListener.setCallbackContext(callbackContext);
        if (!bannerAdView.loadAd()) {
            callbackContext.error("Unable to load banner ad");
        }
    }

    private void plugInBlender() {
        blender = (ViewGroup) webViewContainer.findViewWithTag("SMOOTHIE_BLENDER");
        if (blender == null) {

            // make the webView stretchy
            webView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            blender = new FrameLayout(cordova.getActivity());
            blender.setTag("SMOOTHIE_BLENDER");
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            float density = cordova.getActivity().getResources().getDisplayMetrics().density;
            params.height = Math.round(50 * density);
            blender.setLayoutParams(params);
            blender.setVisibility(View.GONE);

            webViewContainer.addView(blender, bannerAtTop ? 0 : webViewContainer.getChildCount());
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
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    bannerAdView.setVisibility(View.VISIBLE);
                    bannerAdView.bringToFront();
                }
            });
            callbackContext.success();
        }

        @Override
        public void onAdFailedToLoad(Ad ad, AdError error) {

            callbackContext.error(error.getMessage());
        }

    }
}
