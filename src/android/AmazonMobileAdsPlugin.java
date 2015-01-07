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
                ViewGroup parent = (ViewGroup) AmazonMobileAdsPlugin.this.webView.getParent();
                parent.addView(bannerAdView, bannerAtTop ? 0 : parent.indexOfChild(webView));
            }
        });
        // create interstitial
        interstitialAd = new InterstitialAd(cordova.getActivity());
        interstitialAd.setListener(new DefaultAdListener() {
            @Override
            public void onAdFailedToLoad(Ad ad, AdError error) {
                // call another ad network and make us some cash
            }

            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                AmazonMobileAdsPlugin.this.interstitialAd.showAd();
            }

            @Override
            public void onAdDismissed(Ad ad) {
                // callback to javascript so the game can continue
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            Log.i(LOGTAG, action);
            if (action.equals("setAppKey")) {
                AdRegistration.setAppKey(args.getString(0));
            } else if (action.equals("enableTestMode")) {
                AdRegistration.enableTesting(args.getBoolean(0));
            } else if (action.equals("enableLogging")) {
                AdRegistration.enableLogging(args.getBoolean(0));
            } else if (action.equals("showBannerAd")) {
                onShowBannerAd(args.getBoolean(0));
            } else if (action.equals("hideBannerAd")) {
                onHideBannerAd();
            } else if (action.equals("showInterstitialAd")) {
                interstitialAd.loadAd();
            }
            callbackContext.success("");
            return true;
        } catch (JSONException e) {
            Log.e("AmazonMobileAdsPlugin", e.getMessage());
            callbackContext.error("AmazonMobileAdsPlugin: " + e.getMessage());
            return false;
        }
    }

    private void onHideBannerAd() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(bannerAdView.getVisibility() == View.VISIBLE) {
                    bannerAdView.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void onShowBannerAd(final boolean showAtTop) throws JSONException {

        bannerAdView.loadAd();

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(showAtTop != bannerAtTop) {
                    bannerAtTop = showAtTop;
                    ViewGroup parent = (ViewGroup) webView.getParent();
                    parent.removeView(bannerAdView);
                    parent.addView(bannerAdView, bannerAtTop ? 0 : 1);
                }

                if(bannerAdView.getVisibility() != View.VISIBLE) {
                    bannerAdView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
