package com.example.sahin.enyakinkangrubu;

/**
 * Created by sahin on 12.12.2017.
 */

public class Mesajlar {
    public String gonderenUserId;
    public String Message;
    public String Date;
    public String aliciUserId;

    public String getMesajId() {
        return MesajId;
    }

    public void setMesajId(String mesajId) {
        MesajId = mesajId;
    }

    public String MesajId;

    public String getGonderenUserId() {
        return gonderenUserId;
    }

    public void setGonderenUserId(String gonderenUserId) {
        this.gonderenUserId = gonderenUserId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }



    public String getAliciUserId() {
        return aliciUserId;
    }

    public void setAliciUserId(String aliciUserId) {
        this.aliciUserId = aliciUserId;
    }
}
