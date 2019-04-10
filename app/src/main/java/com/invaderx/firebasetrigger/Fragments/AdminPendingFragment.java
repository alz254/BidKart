package com.invaderx.firebasetrigger.Fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.firebasetrigger.Adapters.ProductListAdapter;
import com.invaderx.firebasetrigger.Models.Products;
import com.invaderx.firebasetrigger.R;

import java.util.ArrayList;

public class AdminPendingFragment extends Fragment {

    private RecyclerView admin_pending_rView;
    private ArrayList<Products> productList = new ArrayList<>();
    private ProductListAdapter productListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView admin_pending_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_pending, container, false);


        //---------------Recycler View------------------------------------------
        admin_pending_rView = view.findViewById(R.id.admin_pending_rView);
        productListAdapter = new ProductListAdapter(productList, getContext());
        admin_pending_rView.setLayoutManager(new LinearLayoutManager(getContext()));
        admin_pending_rView.setAdapter(productListAdapter);
        admin_pending_error = view.findViewById(R.id.admin_pending_error);
        admin_pending_error.setVisibility(View.GONE);
        //-----------------------------------------------------------------------


        //firebase Database references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        getProductList();

        admin_pending_rView.setAdapter(productListAdapter);

        return view;
    }

    //fetches product list
    public void getProductList() {

        databaseReference.child("product")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Products products;
                        productList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                products = data.getValue(Products.class);
                                if (products.getpStatus().equals("pending")) {
                                    productList.add(new Products(products.getpId(), products.getpName(), products.getpCategory(),
                                            products.getpBid(), products.getBidderUID(), products.getProductListImgURL(), products.getSellerName(),
                                            products.getBasePrice(), products.getSellerUID(), products.getCatId(),
                                            products.getNoOfBids(), products.getSearchStr(), products.getExpTime(), products.getpDescription(),
                                            products.getpCondition(), products.getpStatus(), products.getExpDate()));
                                }

                            }
                        } else
                            showSnackbar("No products found");

                        productListAdapter.notifyDataSetChanged();
                        if (productList.size() == 0) {
                            admin_pending_error.setVisibility(View.VISIBLE);
                        } else
                            admin_pending_error.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //showSnackbar(databaseError.getMessage());

                    }
                });

    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


}
