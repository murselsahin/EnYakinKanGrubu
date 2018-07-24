package com.example.sahin.enyakinkangrubu;

/**
 * Created by sahin on 15.12.2017.
 */

public class OdaList {
    public String NameSurname;
    public String ImgUrl;

    public OdaList(String NameSurname,String ImgUrl)
    {
        this.NameSurname=NameSurname;
        this.ImgUrl=ImgUrl;
    }

    public String getNameSurname() {
        return NameSurname;
    }

    public void setNameSurname(String nameSurname) {
        NameSurname = nameSurname;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}
