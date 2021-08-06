package rk.android.app.privacydashboard.activities.donation;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivityDonationBinding;
import rk.android.app.privacydashboard.util.Utils;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class DonationActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Context context;

    ActivityDonationBinding binding;

    BillingClient billingClient;

    List<String> skuList = Arrays.asList("purchase_1", "purchase_2", "purchase_3", "purchase_4",
            "purchase_5", "purchase_6", "purchase_7", "purchase_8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = DonationActivity.this;

        setupToolbar();
        initBilling();
        initOnClickListeners();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.donation_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED,null);
            finish();
        });

        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.scrollView.canScrollVertically(Constants.SCROLL_DIRECTION_UP)){
                toolbar.setElevation(Constants.TOOLBAR_SCROLL_ELEVATION);
            }else{
                toolbar.setElevation(Constants.TOOLBAR_DEFAULT_ELEVATION);
            }
        });
    }

    private void initBilling(){

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    initBillingList();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    private void initBillingList(){

        if (billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();

            billingClient.querySkuDetailsAsync(params, (billingResult, list) -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    if (list!=null){
                        binding.donationSupport.setPrice(list.get(0).getPrice());
                        binding.donationTea.setPrice(list.get(1).getPrice());
                        binding.donationLatte.setPrice(list.get(2).getPrice());
                        binding.donationMovie.setPrice(list.get(3).getPrice());
                        binding.donationDinner.setPrice(list.get(4).getPrice());
                        binding.donationAdFree.setPrice(list.get(5).getPrice());
                        binding.donationCreateApps.setPrice(list.get(6).getPrice());
                        binding.donationSuper.setPrice(list.get(7).getPrice());
                    }

                }else {
                    Toast.makeText(DonationActivity.this,"Cannot query product",Toast.LENGTH_SHORT).show();
                }

            });

        }else {
            Toast.makeText(DonationActivity.this,"Billing client is not ready",Toast.LENGTH_SHORT).show();

        }

    }

    private void initOnClickListeners(){

        binding.donationFree.setOnClickListener(view -> Utils.openLink(context,"https://play.google.com/store/apps/details?id=rk.android.app.privacydashboard"));
        binding.donationSupport.setOnClickListener(view -> checkPurchase(skuList.get(0)));
        binding.donationTea.setOnClickListener(view -> checkPurchase(skuList.get(1)));
        binding.donationLatte.setOnClickListener(view -> checkPurchase(skuList.get(2)));
        binding.donationMovie.setOnClickListener(view -> checkPurchase(skuList.get(3)));
        binding.donationDinner.setOnClickListener(view -> checkPurchase(skuList.get(4)));
        binding.donationAdFree.setOnClickListener(view -> checkPurchase(skuList.get(5)));
        binding.donationCreateApps.setOnClickListener(view -> checkPurchase(skuList.get(6)));
        binding.donationSuper.setOnClickListener(view -> checkPurchase(skuList.get(7)));

    }

    private void checkPurchase(String id){
        if (billingClient.isReady()) {
            initiatePurchase(id);
        }else {
            billingClient = BillingClient.newBuilder(DonationActivity.this).enablePendingPurchases()
                    .setListener(DonationActivity.this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(id);
                    } else {
                        Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {

                }
            });
        }
    }

    private void initiatePurchase(final String PRODUCT_ID) {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (skuDetailsList != null && skuDetailsList.size() > 0) {
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                            billingClient.launchBillingFlow(DonationActivity.this, flowParams);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Purchase Item "+PRODUCT_ID+" not Found",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                " Error "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            acknowledgePurchase(list.get(0).getPurchaseToken());
        }  else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(context,"Already Donated! Thank you :)",Toast.LENGTH_SHORT).show();
        }

        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void acknowledgePurchase(String token){

        AcknowledgePurchaseParams acknowledgePurchase = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(token)
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchase, billingResult -> Toast.makeText(context,"Donation Complete! Thank you :)",Toast.LENGTH_SHORT).show());

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }

}
