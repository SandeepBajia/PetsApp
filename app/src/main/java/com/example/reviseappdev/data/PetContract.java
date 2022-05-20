package com.example.reviseappdev.data;

import android.net.Uri;
import android.provider.BaseColumns;

public   final class PetContract {
    private PetContract(){}
    public static final String Content_Authority = "com.example.reviseappdev" ;
    public final static Uri Base_Content_Uri = Uri.parse("content://" + Content_Authority) ;
    public final static String Path_Pets = "Pets" ;

    public final static class   PetEntry implements BaseColumns {
        public   final static String  TableName  = "Pets" ;
        public final  static  String id = BaseColumns._ID ;
        public final static String PetName = "name" ;
        public final static String PetBreed = "breed" ;
        public final static String PetGender = "gender" ;
        public final static String PetWeight = "weight" ;
        public final static int  Unknown  = 0 ;
        public final static int  Male =     1  ;
        public final static int  Female  = 2 ;

        public final static Uri Content_URI = Uri.withAppendedPath(Base_Content_Uri , Path_Pets) ;

        public static  boolean isValid(int gender){
            if(gender==Unknown || gender==Male || gender==Female){
                return true ;
            }
            return false ;
        }

    }
}
