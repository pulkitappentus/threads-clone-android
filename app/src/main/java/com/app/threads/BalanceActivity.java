package com.app.threads;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.volley.Request;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.threads.app.App;
import com.app.threads.common.ActivityBase;
import com.app.threads.util.CustomRequest;


public class BalanceActivity extends ActivityBase implements RewardedVideoAdListener {

    Toolbar mToolbar;

    // For Google Play
    private static final String TAG = "ifsoft.inappbilling";            // Here you can write anything you like! For example: obama.white.house.billing
    private static final String TOKEN = "ifsoft.inappbilling";          // Here you can write anything you like! For example: obama.white.house.billing

    // See documentation!   com.app.mysocia

    static final String ITEM_SKU_1 = "app.threads.com.iap1";          // Change to: yourdomain.com.iap1
    static final String ITEM_SKU_2 = "app.threads.com.iap2";          // Change to: yourdomain.com.iap2
    static final String ITEM_SKU_3 = "app.threads.com.ru.iap3";          // Change to: yourdomain.com.iap3
    static final String ITEM_SKU_4 = "android.test.purchased";          // Not used. For testing

    static final int ITEM_SKU_1_AMOUNT = 100;          // in usd cents | 1 usd = 100 cents
    static final int ITEM_SKU_2_AMOUNT = 200;          // in usd cents | 2 usd = 200 cents
    static final int ITEM_SKU_3_AMOUNT = 300;          // in usd cents | 3 usd = 300 cents

    ConstraintLayout mBuy1Button, mBuy2Button, mBuy3Button;
    Button mBuy4Button;
    TextView mLabelCredits,tvAds,tvHeading,tvCreditOne,tvCreditTwo,tvCreditThree,tvCreditOneAmount,tvCreditTwoAmount,tvCreditThreeAmount;

    ConstraintLayout mRewardedAdButton;

    private RewardedVideoAd mRewardedVideoAd;

    private Boolean loading = false;

    private BillingClient mBillingClient;
    private final Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_balance);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {
            loading = false;
        }

        if (loading) {

            showpDialog();
        }

        mLabelCredits = (TextView) findViewById(R.id.labelCredits);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        tvCreditOneAmount = (TextView) findViewById(R.id.tvCreditOneAmount);
        tvCreditTwoAmount = (TextView) findViewById(R.id.tvCreditTwoAmount);
        tvCreditThreeAmount = (TextView) findViewById(R.id.tvCreditThreeAmount);
        mBuy1Button = (ConstraintLayout) findViewById(R.id.iap1_google_btn);
        mBuy2Button = (ConstraintLayout) findViewById(R.id.iap2_google_btn);
        mBuy3Button = (ConstraintLayout) findViewById(R.id.iap3_google_btn);
        mBuy4Button = (Button) findViewById(R.id.iap4_google_btn);      // For test Google Pay Button

        mRewardedAdButton = (ConstraintLayout) findViewById(R.id.rewarded_ad_btn);
        tvAds = (TextView) findViewById(R.id.tvAds);

        tvHeading.setText("Buy Credits");

        if (!GOOGLE_PAY_TEST_BUTTON) {
            mBuy4Button.setVisibility(View.GONE);
        }

        mBuy1Button.setOnClickListener(v -> {
            launchBilling(ITEM_SKU_1);
            tvCreditOne.setTextColor(getResources().getColor(R.color.white));
            tvCreditOneAmount.setTextColor(getResources().getColor(R.color.white));
            mBuy1Button.setBackground(getResources().getDrawable(R.drawable.bg_gredient));
        });

        mBuy2Button.setOnClickListener(v -> {
            tvCreditTwo.setTextColor(getResources().getColor(R.color.white));
            tvCreditTwoAmount.setTextColor(getResources().getColor(R.color.white));
            mBuy2Button.setBackground(getResources().getDrawable(R.drawable.bg_gredient));
            launchBilling(ITEM_SKU_2);

        });

        mBuy3Button.setOnClickListener(v -> {
            tvCreditThree.setTextColor(getResources().getColor(R.color.white));
            tvCreditThreeAmount.setTextColor(getResources().getColor(R.color.white));
            mBuy3Button.setBackground(getResources().getDrawable(R.drawable.bg_gredient));
            launchBilling(ITEM_SKU_3);
        });

        mBuy4Button.setOnClickListener(v -> {
            tvCreditOne.setTextColor(getResources().getColor(R.color.white));
            tvCreditOneAmount.setTextColor(getResources().getColor(R.color.white));
            mBuy1Button.setBackground(getResources().getDrawable(R.drawable.bg_gredient));
            launchBilling(ITEM_SKU_4);
        });

        mRewardedAdButton.setVisibility(View.GONE);
        mRewardedAdButton.setEnabled(false);

        if (App.getInstance().getAllowRewardedAds() == 1) {

            mRewardedAdButton.setVisibility(View.VISIBLE);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(this);

            loadRewardedVideoAd();
        }

        mRewardedAdButton.setOnClickListener(v -> {

            if (mRewardedVideoAd.isLoaded()) {

                mRewardedVideoAd.show();
            }
        });

        update();

        setupBilling();
    }

    private void setupBilling() {

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, purchases) -> {

            if (purchases != null) {

                for (Purchase purchase : purchases) {

                    consume(purchase.getPurchaseToken(), purchase.getDeveloperPayload(), purchase.getSku());
                }
            }
        }).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    // The BillingClient is ready. You can query purchases here.

                    querySkuDetails();

                    Log.e("onBillingSetupFinished", "WORKS");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                Log.e("onBillingSetupFinished", "NOT WORKS");
            }
        });
    }

    private void consume(String purchaseToken, String payload, String sku) {

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();

        ConsumeResponseListener listener = (billingResult, outToken) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                // Handle the success of the consume operation.
                // For example, increase the number of coins inside the user's basket.

                switch (sku) {

                    case ITEM_SKU_1:

                        App.getInstance().setBalance(App.getInstance().getBalance() + 30);
                        payment(30, ITEM_SKU_1_AMOUNT, PT_GOOGLE_PURCHASE,true);

                        break;

                    case ITEM_SKU_2:

                        App.getInstance().setBalance(App.getInstance().getBalance() + 70);
                        payment(70, ITEM_SKU_2_AMOUNT, PT_GOOGLE_PURCHASE,true);

                        break;

                    case ITEM_SKU_3:

                        App.getInstance().setBalance(App.getInstance().getBalance() + 120);
                        payment(120, ITEM_SKU_3_AMOUNT, PT_GOOGLE_PURCHASE,true);

                        break;

                    case ITEM_SKU_4:

                        Log.e("Payment", "Call");

                        App.getInstance().setBalance(App.getInstance().getBalance() + 100);
                        payment(100, 0, PT_GOOGLE_PURCHASE,true);

                        break;

                    default:

                        break;
                }
            }
        };

        mBillingClient.consumeAsync(consumeParams, listener);
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(ITEM_SKU_1);
        skuList.add(ITEM_SKU_2);
        skuList.add(ITEM_SKU_3);

        if (GOOGLE_PAY_TEST_BUTTON) {

            skuList.add(ITEM_SKU_4);
        }

        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), (billingResult, skuDetailsList) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                if (skuDetailsList != null) {

                    for (SkuDetails skuDetails : skuDetailsList) {

                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                    }
                }
            }
        });
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(mSkuDetailsMap.get(skuId)).build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }

    public void payment(final int cost, final int amount, final int paymentType, final Boolean showSuccess) {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PAYMENTS_NEW, null,
                response -> {

                    try {

                        if (!response.getBoolean("error")) {

                            if (response.has("balance")) {

                                App.getInstance().setBalance(response.getInt("balance"));
                            }

                            if (showSuccess) {

                                success();
                            }
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                    } finally {

                        loading = false;

                        hidepDialog();

                        update();
                    }
                }, error -> {

                    loading = false;

                    hidepDialog();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("credits", Integer.toString(cost));
                params.put("paymentType", Integer.toString(paymentType));
                params.put("amount", Integer.toString(amount));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void update() {

        mLabelCredits.setText(getString(R.string.label_credits) + " (" + App.getInstance().getBalance() + ")");
    }

    public void success() {

        Toast.makeText(BalanceActivity.this, getString(R.string.msg_success_purchase), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    @Override
    protected void onStart() {

        super.onStart();

        if (!mBillingClient.isReady()) {

            setupBilling();
        }
    }

    @Override
    protected void onPause() {

        mRewardedVideoAd.pause(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (mRewardedVideoAd != null) {

            mRewardedVideoAd.destroy(this);
        }

        super.onDestroy();

        hidepDialog();

        mBillingClient.endConnection();
    }

    @Override protected void onResume() {

        mRewardedVideoAd.resume(this);

        super.onResume();

        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_deactivate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void loadRewardedVideoAd() {
        tvAds.setText(getString(R.string.msg_loading));

        mRewardedVideoAd.loadAd(getString(R.string.rewarded_ad_unit_id), new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {

        // Reward the user.

        Log.d("Rewarded Video", "onRewarded");

        Toast.makeText(BalanceActivity.this, getString(R.string.msg_success_rewarded), Toast.LENGTH_SHORT).show();

        mRewardedAdButton.setEnabled(false);

        App.getInstance().setBalance(App.getInstance().getBalance() + reward.getAmount());
        payment(reward.getAmount(), 0, PT_ADMOB_REWARDED_ADS,false);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

        Log.d("Rewarded Video", "onRewardedVideoAdLeftApplication");

        // user click by link in video ad
    }

    @Override
    public void onRewardedVideoAdClosed() {

        Log.d("Rewarded Video", "onRewardedVideoAdClosed");

        mRewardedAdButton.setEnabled(false);

        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {

        Log.d("Rewarded Video", "onRewardedVideoAdFailedToLoad");

        mRewardedAdButton.setEnabled(false);

        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

        Log.d("Rewarded Video", "onRewardedVideoAdLoaded");

        tvAds.setText(getString(R.string.action_view_rewarded_video_ad));
        mRewardedAdButton.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {

        Log.d("Rewarded Video", "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {

        Log.d("Rewarded Video", "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted() {

        Log.d("Rewarded Video", "onRewardedVideoCompleted");
    }
}
