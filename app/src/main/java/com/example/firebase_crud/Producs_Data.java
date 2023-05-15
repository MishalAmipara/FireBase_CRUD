package com.example.firebase_crud;

public class Producs_Data
{
    String proName;
    String proPrice;
    String proDes;
    String proImage;

    public Producs_Data(String proName, String proPrice, String proDes, String proImage, String imageurl) {
        this.proName = proName;
        this.proPrice = proPrice;
        this.proDes = proDes;
        this.proImage = proImage;
    }

    public Producs_Data() {
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

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }
}
