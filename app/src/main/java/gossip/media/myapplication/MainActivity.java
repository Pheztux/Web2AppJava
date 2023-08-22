package gossip.media.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private static final String ONESIGNAL_APP_ID = "4afd64c4-7cc7-4b3b-80a4-c237c326d685";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

        //Load Insterstial Ads

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // optIn will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App
        // Message to prompt for notification permission (See step 7)
        OneSignal.getUser().getPushSubscription().optIn();

        // Add Banner Ads to Application
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        // Use the custom WebViewClient
        webView.setWebViewClient(new WebViewClient());

        // Set up DownloadListener
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        webView.loadUrl("https://gossip247.com.ng/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //Set up SwipeRefreshLayout
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
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
    @Override
    public void onBackPressed() {
        if  (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
