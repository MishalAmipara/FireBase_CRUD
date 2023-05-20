package com.example.firebase_crud;

public class Producs_Data
{
    String id;
    String proName;
    String proPrice;
    String proDes;
    String proImageUrl;

    public Producs_Data() {
    }

    public Producs_Data(String id,String proName, String proPrice, String proDes, String imageurl) {
        this.id=id;
        this.proName = proName;
        this.proPrice = proPrice;
        this.proDes = proDes;
        this.proImageUrl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProPrice() {
        return proPrice;
    }

    public void setProPrice(String proPrice) {
        this.proPrice = proPrice;
    }

    public String getProDes() {
        return proDes;
    }

    public void setProDes(String proDes) {
        this.proDes = proDes;
    }

    public String getProImageUrl() {
        return proImageUrl;
    }

    public void setProImageUrl(String proImageUrl) {
        this.proImageUrl = proImageUrl;
    }
}
