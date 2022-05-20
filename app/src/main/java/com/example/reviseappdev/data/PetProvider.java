package com.example.reviseappdev.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reviseappdev.EditorActivity;
import com.example.reviseappdev.MainActivity;

public class PetProvider extends ContentProvider {
    public static final String LOG_TAG  = PetProvider.class.getSimpleName() ;
    private final static int PETS = 100 ;
    private final static int PETS_ID = 101 ;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH) ;
    static{
        sUriMatcher.addURI(PetContract.Content_Authority , PetContract.Path_Pets , PETS) ;
        sUriMatcher.addURI(PetContract.Content_Authority , PetContract.Path_Pets + "/#" , PETS_ID);
    }
    PetDbHelper petDbHelper ;
    @Override
    public boolean onCreate() {
        petDbHelper = new PetDbHelper(getContext()) ;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = petDbHelper.getReadableDatabase() ;
        Cursor cursor ;
        int match = sUriMatcher.match(uri) ;
        switch (match){
            case PETS :
                cursor = sqLiteDatabase.query(PetContract.PetEntry.TableName , projection , selection , selectionArgs , null , null , sortOrder) ;
                break ;
            case PETS_ID  :
                selection = PetContract.PetEntry.id + "=?" ;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))} ;
                Log.v(String.valueOf(ContentUris.parseId(uri)) , "cursor") ;
                cursor = sqLiteDatabase.query(PetContract.PetEntry.TableName , projection , selection , selectionArgs , null , null , sortOrder) ;

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        cursor.setNotificationUri(getContext().getContentResolver() , uri);
        return cursor ;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri) ;
        switch(match){
            case PETS :
                return insertPet(uri , values) ;

            default :
                throw new IllegalStateException("Unexpected value: " + match);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri) ;
        int rowid ;
        switch(match){
            case PETS :
                rowid =  deletePet(uri , selection , selectionArgs) ;
                break ;
            case PETS_ID :
                selection = PetContract.PetEntry.id + "=?" ;
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))} ;
                rowid =  deletePet(uri , selection , selectionArgs) ;
                break ;
            default:
                throw new IllegalArgumentException("delete is not supported for" + uri) ;
        }
        if(rowid !=0){
            getContext().getContentResolver().notifyChange(uri , null);
        }
        return rowid ;


    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri) ;
        switch(match){
            case PETS :
                return updatePet(uri , values , selection , selectionArgs) ;
            case PETS_ID:
                selection = PetContract.PetEntry.id + "= ?" ;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))} ;
                return updatePet(uri , values , selection , selectionArgs) ;
            default:
                throw new IllegalArgumentException("update is not supported for " + uri) ;
        }
    }

    private Uri insertPet(Uri uri , ContentValues contentValues){
        String petName = contentValues.getAsString(PetContract.PetEntry.PetName) ;
        if(petName =="null"){
            throw new IllegalArgumentException("pet require a name ") ;
        }
        Integer  gender = contentValues.getAsInteger(PetContract.PetEntry.PetGender) ;
        if(gender == null || !PetContract.PetEntry.isValid(gender)){
            throw  new IllegalArgumentException("pet require a valid gender") ;

        }
        Integer weight = contentValues.getAsInteger(PetContract.PetEntry.PetWeight) ;
        if(weight != null && weight <0){
            throw new IllegalArgumentException("pet require a valid weight ") ;
        }
        SQLiteDatabase sqLiteDatabase = petDbHelper.getWritableDatabase() ;
        long id = sqLiteDatabase.insert(PetContract.PetEntry.TableName , null , contentValues) ;
        getContext().getContentResolver().notifyChange(uri , null);
        if(id == -1 ){
            return null ;
        }

        else {
            return ContentUris.withAppendedId(uri, id);
        }
    }

    // update method
    private int updatePet(Uri uri , ContentValues contentValues , String selection , String[] selectionArg ){
        if(contentValues.containsKey(PetContract.PetEntry.PetName)){
            String petName = contentValues.getAsString(PetContract.PetEntry.PetName) ;
            if(petName ==null){
                throw new IllegalArgumentException("pet require a name ") ;
            }
        }
        if(contentValues.containsKey(PetContract.PetEntry.PetGender)){
            Integer  gender = contentValues.getAsInteger(PetContract.PetEntry.PetGender) ;
            if(gender == null || !PetContract.PetEntry.isValid(gender)){
                throw  new IllegalArgumentException("pet require a valid gender") ;

            }
        }
        if(contentValues.containsKey(PetContract.PetEntry.PetWeight)){
            Integer weight = contentValues.getAsInteger(PetContract.PetEntry.PetWeight) ;
            if(weight != null && weight <0){
                throw new IllegalArgumentException("pet require a valid weight ") ;
            }
        }
        if(contentValues.size() ==0){
            return 0 ;
        }

        SQLiteDatabase sqLiteDatabase = petDbHelper.getWritableDatabase() ;

        int totalRow =  sqLiteDatabase.update(PetContract.PetEntry.TableName , contentValues, selection , selectionArg) ;
        if(totalRow !=0){
            getContext().getContentResolver().notifyChange(uri , null);
        }
        return totalRow ;

    }

    private int deletePet(Uri uri , String selection , String[] selectionArg){
        SQLiteDatabase sqLiteDatabase = petDbHelper.getWritableDatabase() ;
        getContext().getContentResolver().notifyChange(uri , null);
        return sqLiteDatabase.delete(PetContract.PetEntry.TableName , selection , selectionArg) ;
    }
}
