package gossip.media.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private WebView webView; // WebView component
    private SwipeRefreshLayout mySwipeRefreshLayout; // SwipeRefreshLayout for refreshing webpage
    private AdView mAdView; // Banner ad view
    private InterstitialAd mInterstitialAd; // Interstitial ad view
    private static final String ONESIGNAL_APP_ID = "4afd64c4-7cc7-4b3b-80a4-c237c326d685";

    // Timer variables
    private Handler handler = new Handler();
    private Runnable runnable;

    // Refresh counter
    private int refreshCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new MywebClient());

        // Initialize MobileAds for displaying ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Load and display banner ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Load interstitial ad
        InterstitialAd.load(this, "ca-app-pub-9260823749316773/9611283282", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        // Start the timer when the ad is loaded
                        startTimer();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });

        // Load webpage in WebView component
        webView.loadUrl("https://sellercenter.jumia.com.ng/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set up SwipeRefreshLayout for refreshing webpage
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshCounter++; // Increment refresh counter
                        webView.reload();
                    }
                }
        );
    }

    private class MywebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startTimer() {
        // Schedule the display of interstitial ads every 2 minutes or on every third refresh
        runnable = new Runnable() {
            @Override
            public void run() {
                if (refreshCounter % 3 == 0 || (refreshCounter > 0 && refreshCounter % 3 == 2)) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }

                refreshCounter++; // Increment refresh counter

                handler.postDelayed(this, 2 * 60 * 1000); // Run every 2 minutes
            }
        };

        handler.postDelayed(runnable, 2 * 60 * 1000); // Start immediately after loading the ad
    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the timer when the activity is resumed
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop the timer when the activity is paused
        handler.removeCallbacks(runnable);
    }
}