package com.example.sahin.enyakinkangrubu;

/**
 * Created by sahin on 17.12.2017.
 */

public class BagisciList {
    String UserId;
    String Name;
    String BloodName;
    String Chatizin;
    String Aramaizin;
    String Smsizin;
    String Longitude;
    String Latitude;
    String UserPhone;
    String GenderName;
    String Uzaklik;
    String Url;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public BagisciList(String userId, String name, String bloodName, String chatizin, String aramaizin, String smsizin, String longitude, String latitude, String userPhone, String genderName, String uzaklik, String url) {
        UserId = userId;
        Name = name;
        BloodName = bloodName;
        Chatizin = chatizin;
        Aramaizin = aramaizin;
        Smsizin = smsizin;
        Longitude = longitude;

        Latitude = latitude;
        UserPhone = userPhone;
        GenderName = genderName;
        Uzaklik = uzaklik;
        Url=url;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBloodName() {
        return BloodName;
    }

    public void setBloodName(String bloodName) {
        BloodName = bloodName;
    }

    public String getChatizin() {
        return Chatizin;
    }

    public void setChatizin(String chatizin) {
        Chatizin = chatizin;
    }

    public String getAramaizin() {
        return Aramaizin;
    }

    public void setAramaizin(String aramaizin) {
        Aramaizin = aramaizin;
    }

    public String getSmsizin() {
        return Smsizin;
    }

    public void setSmsizin(String smsizin) {
        Smsizin = smsizin;
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
}
