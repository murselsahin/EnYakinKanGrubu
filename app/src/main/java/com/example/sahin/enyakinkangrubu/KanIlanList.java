package com.example.sahin.enyakinkangrubu;

import org.json.JSONObject;

/**
 * Created by sahin on 9.11.2017.
 */

public class KanIlanList {
    String Id;
    String UserId;
    String HospitalName;
    String Declaration;
    String Date ;
    String Longitude;
    String Latitude ;
    String UserName ;
    String UserDate ;
    String UserPhone;
    String GenderName;
    String Uzaklik ;
    String BloodName;
    String ImgUrl ;
    public KanIlanList(String id, String userId, String hospitalName, String declaration, String date, String longitude, String latitude, String userName, String userDate, String userPhone, String genderName, String uzaklik, String bloodName, String imgUrl) {
        Id = id;
        UserId = userId;
        HospitalName = hospitalName;
        Declaration = declaration;
        Date = date;
        Longitude = longitude;
        Latitude = latitude;
        UserName = userName;
        UserDate = userDate;
        UserPhone = userPhone;
        GenderName = genderName;
        Uzaklik = uzaklik;
        BloodName = bloodName;
        ImgUrl = imgUrl;
    }



    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public String getDeclaration() {
        return Declaration;
    }

    public void setDeclaration(String declaration) {
        Declaration = declaration;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserDate() {
        return UserDate;
    }

    public void setUserDate(String userDate) {
        UserDate = userDate;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getGenderName() {
        return GenderName;
    }

    public void setGenderName(String genderName) {
        GenderName = genderName;
    }

    public String getUzaklik() {
        return Uzaklik;
    }

    public void setUzaklik(String uzaklik) {
        Uzaklik = uzaklik;
    }

    public String getBloodName() {
        return BloodName;
    }

    public void setBloodName(String bloodName) {
        BloodName = bloodName;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}
