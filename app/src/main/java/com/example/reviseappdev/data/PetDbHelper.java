package com.example.reviseappdev.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PetDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shelter.db" ;
    private static final int version =  1 ;
    public PetDbHelper(Context context){
        super(context , DATABASE_NAME , null , version);
    }
    public    String petTable = "CREATE TABLE " + PetContract.PetEntry.TableName + " ( " + PetContract.PetEntry.id + " " +
            "INTEGER PRIMARY KEY AUTOINCREMENT" + " , " + PetContract.PetEntry.PetName + " TEXT NOT NULL , " + PetContract.PetEntry.PetBreed +" TEXT" +
            " , " + PetContract.PetEntry.PetGender + "  Integer NOT NULL , " + PetContract.PetEntry.PetWeight + "  Integer NOT NULL DEFAULT 0 ) ;" ;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(petTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
