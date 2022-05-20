package com.example.reviseappdev;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.reviseappdev.data.PetContract;

public class PetAdapter extends CursorAdapter {

    public PetAdapter(Context context , Cursor cursor){

        super( context , cursor , 0) ;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item , parent , false ) ;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView petname = view.findViewById(R.id.petname) ;
        TextView petbreed = view.findViewById(R.id.petbreed) ;
        int nameIndex = cursor.getColumnIndex(PetContract.PetEntry.PetName) ;
        int breedIndex = cursor.getColumnIndex(PetContract.PetEntry.PetBreed) ;
        petname.setText(cursor.getString(nameIndex));
        petbreed.setText(cursor.getString(breedIndex));
    }
}
