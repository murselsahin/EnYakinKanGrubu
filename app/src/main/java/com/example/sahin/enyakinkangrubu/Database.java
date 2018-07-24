package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahin on 31.10.2017.
 */

public class Database extends SQLiteOpenHelper {
    private static final String DbName = "KANGRUBU";
    private static final int DATABASE_VERSION = 1;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DbName, null, DATABASE_VERSION);
    }

    public void izinEkle(int UserId,int IlanHaber)
    {
        SQLiteDatabase db_yaz = this.getWritableDatabase();
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sql="select * from IZIN where UserId="+UserId+"";
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0)
        {
            String sql2="insert into IZIN(UserId,IlanHaber) values("+UserId+","+IlanHaber+")";
            db_yaz.execSQL(sql2);
        }else
        {
            String sql2="update IZIN set IlanHaber="+IlanHaber+" where UserId="+UserId+"";
            db_yaz.execSQL(sql2);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE REMEMBER(UserId INTEGER ); ");
        sqLiteDatabase.execSQL("CREATE TABLE COORDINATES(Coordinates TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE MESAJLAR (Id INTEGER PRIMARY KEY AUTOINCREMENT,UserId INTEGER,gonderenUserId INTEGER,aliciUserId INTEGER,Message TEXT,Date datetime,MesajId INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE SILINENMESAJLAR (Id INTEGER PRIMARY KEY AUTOINCREMENT,MesajId INTEGER , UserId INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE GURUPLAMA (UserId INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE IZIN (UserId INTEGER,IlanHaber INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE BLOODGROUPS(Id INTEGER,Name TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE USERINFO(UserId INTEGER,GoogleId TEXT,Name TEXT,SurName TEXT,Date datetime,Phone TEXT,BloodName TEXT,GenderName TEXT,Mail TEXT,ImgUrl TEXT,IlanHaber INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE ILANLAR (UserName TEXT, UserPhone TEXT, KendiUserId INTEGER, IlanUserId INTEGER, HospitalName TEXT, BloodName TEXT, Declaration TEXT, Date datetime, Longitude TEXT, Latitude TEXT, Uzaklik TEXT, IlanId INTEGER);");

    }
    public String sonTarihGetir(int UserId)
    {
        String sql="select Date from MESAJLAR where UserId="+UserId+" AND  aliciUserId="+UserId+" order by Date DESC limit 1;";
        SQLiteDatabase db_oku = this.getReadableDatabase();
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0)
            return "-1";
        cursor.moveToNext();
        return  cursor.getString(0);
    }

    public void MesajlaraEkle(String UserId,String gonderenUserId , String Message , String Date , String MesajId , String aliciUserId)
    {
        SQLiteDatabase db_ekle = this.getWritableDatabase();
        db_ekle.execSQL("insert into MESAJLAR(UserId,gonderenUserId,Message,Date,MesajId,aliciUserId) values("+UserId+" , "+gonderenUserId+" , '"+Message+"' , '"+Date+"' , "+MesajId+" , "+aliciUserId+")");

    }

    public void BloodGroupDoldur(List<Integer> listId , List<String> listName)
    {
        SQLiteDatabase db_yaz =this.getWritableDatabase();
        db_yaz.execSQL("delete from BLOODGROUPS");
        for(int i=0;i<listId.size();i++)
        {
            db_yaz.execSQL("insert into BLOODGROUPS(Id,Name) values("+listId.get(i)+",'"+listName.get(i)+"')");
        }
    }
    public int mesajlarBosMu(int UserId)
    {
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sql="select * from MESAJLAR where UserId="+UserId+"";
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0)
            return 1;
        return 0;

    }

    public List<Integer> odalari_getir(String UserId)
    {
        List<Integer> sonuc_UserId = new ArrayList<>();

        List<Integer> list_gonderenUserId=new ArrayList<>();
        List<Integer> list_aliciUserId  = new ArrayList<>();



        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select gonderenUserId from MESAJLAR where UserId="+UserId+" group by gonderenUserId HAVING gonderenUserId!="+UserId+"";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext())
            list_gonderenUserId.add(cursor.getInt(0));

        sql = "select aliciUserId from MESAJLAR where UserId="+UserId+" group by aliciUserId HAVING aliciUserId!="+UserId+"";
        Cursor cursor1 = db.rawQuery(sql,null);
        while (cursor1.moveToNext())
            list_aliciUserId.add(cursor1.getInt(0));


        db.execSQL("DELETE FROM GURUPLAMA");

        for(int i=0;i<list_aliciUserId.size();i++)
            db.execSQL("INSERT INTO GURUPLAMA(UserId) values("+list_aliciUserId.get(i)+")");

        for(int k=0;k<list_gonderenUserId.size();k++)
            db.execSQL("INSERT INTO GURUPLAMA(UserId) values("+list_gonderenUserId.get(k)+")");

        sql="select UserId from GURUPLAMA group by UserId";
        Cursor cursor2 = db.rawQuery(sql,null);
        while (cursor2.moveToNext())
            sonuc_UserId.add(cursor2.getInt(0));

        return sonuc_UserId;
    }


    public String engellenenMesajIdleriGetir(String UserId)
    {
        String engelenenMesajIdler="";
        SQLiteDatabase db_oku_mesajlar = this.getReadableDatabase();
        String sql = "select MesajId from MESAJLAR where UserId="+UserId+"";
        Cursor cursor = db_oku_mesajlar.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            engelenenMesajIdler+=cursor.getString(0)+";";
        }
        SQLiteDatabase db_oku_silinen_mesajlar = this.getReadableDatabase();
        String sql1 = "select MesajId from SILINENMESAJLAR where UserId="+UserId+"";
        Cursor cursor1 =db_oku_silinen_mesajlar.rawQuery(sql1,null);
        while (cursor1.moveToNext())
        {
            engelenenMesajIdler+=cursor1.getString(0)+";";
        }
        return engelenenMesajIdler;


    }

    public void coordinatekle(String coordinates)
    {
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sql="select * from COORDINATES";
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0) {
            SQLiteDatabase db_ekle = this.getWritableDatabase();
            db_ekle.execSQL("INSERT INTO COORDINATES(Coordinates) values('"+coordinates+"')");
        }else {
            SQLiteDatabase db_guncelle = this.getWritableDatabase();
            db_guncelle.execSQL("UPDATE COORDINATES SET Coordinates='"+coordinates+"'");
        }
    }
    public boolean coordinat_bosmu()
    {
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sql="select * from COORDINATES";
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0)
            return true;
        return false;
    }
    public String coordinat_getir()
    {
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sql="select * from COORDINATES";
        Cursor cursor = db_oku.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            return cursor.getString(0);
        }
        return "";
    }


    public void rememberEkle(int userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("insert into REMEMBER(UserId) values("+userId+");");
    }
    public int izinGetir(int userId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="select IlanHaber from IZIN where UserId="+userId+"";

        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext())
            return cursor.getInt(0);
        return -1;
    }

    public boolean remember_Varmi()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="select * from REMEMBER";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount()==0)
            return false;
        return true;
    }
    public int userid_getir()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="select * from REMEMBER";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount()==0)
            return -1;
            while(cursor.moveToNext())
                return cursor.getInt(0);
        return -1;
    }

    public void remember_userId_sil()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from REMEMBER");
    }

    public void UserInfoDoldur(int UserId,String GoogleId,String Name,String Surname,String Date,String Phone,String BloodName,String GenderName,String Mail,String ImgUrl,int ilanHaber)
    {
        SQLiteDatabase db_oku = this.getReadableDatabase();
        String sqloku="select * from USERINFO where UserId="+UserId+"";
        Cursor cursor=db_oku.rawQuery(sqloku,null);
        String sql="";
        if(cursor.getCount()==0)
            sql="insert into USERINFO(UserId,GoogleId,Name,SurName,Date,Phone,BloodName,GenderName,Mail,ImgUrl,IlanHaber) values("+UserId+",'"+GoogleId+"','"+Name+"','"+Surname+"','"+Date+"','"+Phone+"','"+BloodName+"','"+GenderName+"','"+Mail+"','"+ImgUrl+"',"+ilanHaber+");";
        else
            sql="update USERINFO set GoogleId='"+GoogleId+"',Name='"+Name+"',SurName='"+Surname+"',Date='"+Date+"',Phone='"+Phone+"',BloodName='"+BloodName+"',GenderName='"+GenderName+"',Mail='"+Mail+"',ImgUrl='"+ImgUrl+"',IlanHaber="+ilanHaber+" where UserId="+UserId+" ";

        SQLiteDatabase db_yaz= this.getWritableDatabase();
        db_yaz.execSQL(sql);
    }





    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS REMEMBER");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS COORDINATES");

        onCreate(sqLiteDatabase);

    }
}
