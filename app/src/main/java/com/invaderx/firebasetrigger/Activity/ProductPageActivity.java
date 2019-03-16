package com.invaderx.firebasetrigger.Activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.Models.UserProfile;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ProductPageActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private String proId;
    private AppBarLayout appBarLayout;
    private NestedScrollView nestedScrollView;
    private ImageView pro_image, pro_seller_image;
    private TextView pro_title, pro_currentbid, pro_expDay,
            pro_expHrs, pro_expMin, pro_expSec, pro_category,
            pro_condition, pro_bidsNo, pro_base_price, pro_description, pro_seller, pro_user_bid;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button place_bid;
    private ArrayList<String> time = new ArrayList<>();
    private LottieAnimationView pro_error_anim, pro_loading;
    private LinearLayout pro_error_frame;
    private PopupWindow popWindow;
    private CoordinatorLayout product_container;
    private FirebaseUser firebaseUser;
    private Products products;
    private int walletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "AppBarTransition");
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        proId = getIntent().getStringExtra("pid");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //enables back button------------------------------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);


        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //gets current user-----------------------------------------------
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //------- Getting views-------------------------
        pro_image = findViewById(R.id.pro_image);
        pro_title = findViewById(R.id.pro_title);
        pro_currentbid = findViewById(R.id.pro_currentbid);
        pro_expDay = findViewById(R.id.pro_expDay);
        pro_expHrs = findViewById(R.id.pro_expHrs);
        pro_expMin = findViewById(R.id.pro_expMin);
        pro_expSec = findViewById(R.id.pro_expSec);
        pro_category = findViewById(R.id.pro_category);
        pro_condition = findViewById(R.id.pro_condition);
        pro_bidsNo = findViewById(R.id.pro_bidsCount);
        pro_base_price = findViewById(R.id.pro_base_price);
        pro_description = findViewById(R.id.pro_description);
        pro_seller_image = findViewById(R.id.pro_seller_image);
        pro_seller = findViewById(R.id.pro_seller);
        place_bid = findViewById(R.id.place_bid);
        pro_error_frame = findViewById(R.id.pro_error_frame);
        pro_error_anim = findViewById(R.id.pro_error);
        pro_loading = findViewById(R.id.pro_loading);
        product_container = findViewById(R.id.product_container);
        pro_user_bid = findViewById(R.id.pro_user_bid);
        nestedScrollView = findViewById(R.id.scroll);
        appBarLayout = findViewById(R.id.app_bar_layout);
        //-------------------------------------------------------------------
        pro_error_frame.setVisibility(View.GONE);
        pro_error_anim.cancelAnimation();
        appBarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);
        place_bid.setVisibility(View.INVISIBLE);
        pro_loading.setVisibility(View.VISIBLE);

        //app bar layout----------------------------------------------------------------------------
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i == 0) {
                nestedScrollView.setBackgroundResource(R.drawable.plane_white);

            } else {
                nestedScrollView.setBackgroundResource(R.drawable.slideup_background);
            }
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, i, i1, i2, i3) -> {


            if (i1 < i3) {
                place_bid.setVisibility(View.VISIBLE);
                place_bid.setAlpha(1f);
            }

            if (i1 > i3) {
                place_bid.setVisibility(View.INVISIBLE);
            }

        });
        //getting product page
        getProducts();
        //getting wallet amount
        getWallet();
        product_container.setAlpha(1f);


    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }

    }


    //sets back button click on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                if (parentIntent == null) {
                    finish();
                    return true;
                } else {
                    parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(parentIntent);
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    //gets product details
    public void getProducts() {

        databaseReference.child("product").orderByChild("pId").equalTo(proId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pro_loading.setVisibility(View.INVISIBLE);
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                products = data.getValue(Products.class);
                            }
                            Glide.with(getApplicationContext()).
                                    load(products.getProductListImgURL())
                                    .into(pro_image);
                            pro_title.setText(products.getpName());

                            if (products.getpBid().containsKey(firebaseUser.getUid())) {
                                pro_user_bid.setText("Your Bid: ₹" + products.getpBid().get(firebaseUser.getUid()));
                                place_bid.setText("Update your bid");
                            } else {
                                pro_user_bid.setText("Your Bid: Not Placed");
                                place_bid.setText("Place your bid");
                            }

                            pro_currentbid.setText("₹" + Collections.max(products.getpBid().values()));
                            pro_category.setText("Category: " + products.getpCategory());
                            pro_condition.setText("Condition: " + products.getpCondition());
                            pro_base_price.setText("Base Price: ₹" + products.getBasePrice());
                            pro_bidsNo.setText("Total Bids: " + products.getNoOfBids());
                            pro_description.setText(products.getpDescription());
                            pro_seller.setText(products.getSellerName());

                            expiryTime(products.getExpTime());

                            pro_expDay.setText(time.get(0) + " Days :");
                            pro_expHrs.setText(time.get(1) + " Hrs :");
                            pro_expMin.setText(time.get(2) + " Min :");
                            pro_expSec.setText(time.get(3) + " Sec");
                            collapsingToolbarLayout.setTitle(products.getpName());

                            place_bid.setVisibility(View.VISIBLE);
                            appBarLayout.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.VISIBLE);

                            if (products.getSellerUID().trim().equals(firebaseUser.getUid())) {
                                place_bid.setVisibility(View.GONE);
                                pro_user_bid.setText("Product belongs to you");
                            } else {
                                place_bid.setVisibility(View.VISIBLE);
                                place_bid.setOnClickListener(v -> {
                                    product_container.setAlpha(0.4f);
                                    bidPopup();
                                });
                            }

                        } else {
                            pro_error_frame.setVisibility(View.VISIBLE);
                            pro_error_anim.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProductPageActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    //calculates product count down time
    public void expiryTime(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        String d = String.valueOf(days);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        String s = String.valueOf(seconds);

        if (d.length() == 1)
            d = "0" + d;
        if (h.length() == 1)
            h = "0" + h;
        if (m.length() == 1)
            m = "0" + m;
        if (s.length() == 1)
            s = "0" + s;

        time.clear();
        time.add(d);
        time.add(h);
        time.add(m);
        time.add(s);

    }

    //pops up bid gateway
    public void bidPopup() {

        LinearLayout bid_success_frame, before_bid, wallet_layout;
        ImageView bid_pro_image;
        TextView bid_pro_name, bid_pro_seller, wallet_amount, bid_pro_currentBid;
        EditText bid_edit_text;
        Button final_payment_button;
        LottieAnimationView bid_success_anim;
        int maxBid = Collections.max(products.getpBid().values());

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(ProductPageActivity.this.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.payment_popup, null, false);

        //getting views---------------------------------------------------------------------------
        bid_success_frame = inflatedView.findViewById(R.id.bid_success_frame);
        before_bid = inflatedView.findViewById(R.id.before_bid);
        wallet_layout = inflatedView.findViewById(R.id.wallet_layout);
        bid_pro_image = inflatedView.findViewById(R.id.bid_pro_image);
        bid_pro_name = inflatedView.findViewById(R.id.bid_pro_name);
        bid_pro_seller = inflatedView.findViewById(R.id.bid_pro_seller);
        wallet_amount = inflatedView.findViewById(R.id.wallet_amount);
        bid_edit_text = inflatedView.findViewById(R.id.bid_edit_text);
        final_payment_button = inflatedView.findViewById(R.id.final_payment_button);
        bid_pro_currentBid = inflatedView.findViewById(R.id.bid_pro_currentBid);
        bid_success_anim = inflatedView.findViewById(R.id.bid_success_anim);
        //----------------------------------------------------------------------------------------

        //hiding animations for success bid
        bid_success_anim.cancelAnimation();
        bid_success_frame.setVisibility(View.GONE);
        before_bid.setVisibility(View.VISIBLE);

        //open wallet layout to add money
        wallet_layout.setOnClickListener(v -> {
            //open wallet layout
        });


        //inflating views with data-------------------------------------------
        Glide.with(getApplicationContext()).
                load(products.getProductListImgURL())
                .into(bid_pro_image);
        bid_pro_name.setText(products.getpName());
        bid_pro_seller.setText("by " + products.getSellerName());
        wallet_amount.setText("Wallet Amount \n₹" + walletAmount);
        bid_pro_currentBid.setText("₹" + Collections.max(products.getpBid().values()));
        //-----------------------------------------------------------------------

        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        //mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width, height - 60, true);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(inflatedView, Gravity.BOTTOM, 0, 100);
        //setting background to full view after dismiss of popup window
        popWindow.setOnDismissListener(() -> product_container.setAlpha(1f));

        //click listener for final payment button
        final_payment_button.setOnClickListener(v -> {
            //error here
            if ((bid_edit_text.getText().toString()).isEmpty()) {
                bid_edit_text.setError("Enter Amount");
                bid_edit_text.requestFocus();

            } else if (Integer.parseInt(bid_edit_text.getText().toString()) <= maxBid) {
                bid_edit_text.setError("Amount must be greater than the current bid");
                bid_edit_text.requestFocus();

            }
            //Toast.makeText(getApplicationContext(), "Amount must be greater than the current bid", Toast.LENGTH_SHORT).show();
            else {
                int x = Integer.parseInt(bid_edit_text.getText().toString());

                if (walletAmount < x / 4)
                    Toast.makeText(getApplicationContext(), "You don't have the sufficient amount in your wallet", Toast.LENGTH_SHORT).show();
                else
                    placeBid(x, bid_success_frame, before_bid, bid_success_anim);
            }
        });


    }

    //gets user wallet amount
    public void getWallet() {

        databaseReference.child("UserProfile").orderByChild("uid").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                userProfile = data.getValue(UserProfile.class);
                            }
                            walletAmount = userProfile.getWallet();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //updates/places bid
    public void placeBid(int bidAmount, LinearLayout linearLayout, LinearLayout h, LottieAnimationView animationView) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.putAll(products.getpBid());
        hashMap.put(firebaseUser.getUid(), bidAmount);
        //sets bidder uid and amount
        databaseReference.child("product").child(products.getpId()).child("pBid").setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    //sets bidder uid for notification
                    databaseReference.child("product").child(products.getpId()).child("bidderUID").setValue(firebaseUser.getUid())
                            .addOnSuccessListener(aoid -> {
                                //increases no of bids
                                databaseReference.child("product").child(products.getpId()).child("noOfBids").setValue(hashMap.size())
                                        .addOnSuccessListener(aVoid1 -> {
                                            linearLayout.setVisibility(View.VISIBLE);
                                            h.setVisibility(View.GONE);
                                            animationView.playAnimation();
                                            animationView.addAnimatorListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    finish();
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            });
                                        });
                            });

                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProductPageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show()
                );

    }


}