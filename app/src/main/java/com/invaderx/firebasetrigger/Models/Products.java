package com.invaderx.firebasetrigger.Models;

public class Products {
    private String pId;
    private String pName;
    private String pCategory;
    private int pBid;
    private String bidderUID;
    private String productListImgURL;
    private String sellerName;
    private String basePrice;
    private String sellerUID;
    private String catId;
    private int noOfBids;
    private String searchStr;
    private long expTime;
    private String pDescription;


    public Products(String pId, String pName, String pCategory, int pBid, String bidderUID, String productListImgURL,
                    String sellerName, String basePrice, String sellerUID,
                    String catId, int noOfBids, String searchStr, long expTime, String pDescription) {
        this.pId = pId;
        this.pName = pName;
        this.pCategory = pCategory;
        this.pBid = pBid;
        this.bidderUID = bidderUID;
        this.productListImgURL = productListImgURL;
        this.sellerName = sellerName;
        this.basePrice = basePrice;
        this.sellerUID = sellerUID;
        this.catId = catId;
        this.noOfBids = noOfBids;
        this.searchStr = searchStr;
        this.expTime = expTime;
        this.pDescription = pDescription;
    }

    public Products() {

    }

    public String getpId() {
        return pId;
    }

    public String getpName() {
        return pName;
    }

    public String getpCategory() {
        return pCategory;
    }

    public int getpBid() {
        return pBid;
    }

    public String getBidderUID() {
        return bidderUID;
    }

    public String getProductListImgURL() {
        return productListImgURL;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public String getSellerUID() {
        return sellerUID;
    }

    public String getCatId() {
        return catId;
    }

    public int getNoOfBids() {
        return noOfBids;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public long getExpTime() {
        return expTime;
    }

    public String getpDescription() {
        return pDescription;
    }
}
