package com.example.reviseappdev;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import com.example.reviseappdev.data.PetContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int  Pet_Loader =  0 ;

    PetAdapter mPetAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView =(ListView) findViewById(R.id.list_view) ;
        View emptyview = findViewById(R.id.empty_view) ;
        listView.setEmptyView(emptyview);
        mPetAdapter = new PetAdapter(this , null) ;
        listView.setAdapter(mPetAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this , EditorActivity.class) ;
                Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.Content_URI , id) ;
                Log.v(Long.toString(id) , "hello") ;
                intent.setData(uri) ;
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(Pet_Loader , null , this) ;


    }



    private void insertdummydata(){
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(PetContract.PetEntry.PetName , "Toto");
        contentValues.put(PetContract.PetEntry.PetBreed , "Terrier");
        contentValues.put(PetContract.PetEntry.PetGender , PetContract.PetEntry.Male);
        contentValues.put(PetContract.PetEntry.PetWeight , 7);
        Uri uri  = getContentResolver().insert(PetContract.PetEntry.Content_URI , contentValues);
        if(uri  == null ){
            Toast.makeText(this , R.string.pet_add_fail , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this , R.string.pet_add , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:

                insertdummydata();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = { PetContract.PetEntry.id , PetContract.PetEntry.PetName , PetContract.PetEntry.PetBreed} ;
        return new CursorLoader(this , PetContract.PetEntry.Content_URI , projection , null , null , null) ;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mPetAdapter.swapCursor(data) ;
    }


    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mPetAdapter.swapCursor(null) ;

    }

    private void deleteAllPet(){
        int row = getContentResolver().delete(PetContract.PetEntry.Content_URI , null , null) ;
        if(row !=0 ){
            Toast.makeText(this , "deleted all pet " , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this , "error in deleting all pet " , Toast.LENGTH_SHORT).show();
        }
    }
}