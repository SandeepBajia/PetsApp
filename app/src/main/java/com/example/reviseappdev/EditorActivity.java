package com.example.reviseappdev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviseappdev.data.PetContract;
import com.example.reviseappdev.data.PetDbHelper ;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    Uri uri ;
    private boolean mPetHasChanged = false ;

    private View.OnTouchListener mtouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true ;
            return false;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent() ;
        uri = intent.getData() ;


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mtouchListener);
        mBreedEditText.setOnTouchListener(mtouchListener);
        mGenderSpinner.setOnTouchListener(mtouchListener);
        mWeightEditText.setOnTouchListener(mtouchListener);



        if(uri == null){
            setTitle("add a  pet");
            invalidateOptionsMenu();


        }
        else {
            setTitle("edit the pet");
            getLoaderManager().initLoader(0 , null , this) ;
        }
        setupSpinner();


    }


    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.Male; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.Female; // Female
                    } else {
                        mGender = PetContract.PetEntry.Unknown; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savePet(){
        String names = mNameEditText.getText().toString().trim() ;
        String breed = mBreedEditText.getText().toString().trim() ;
        int gender = mGender ;
        String weightString = mWeightEditText.getText().toString().trim() ;
        if(uri == null && TextUtils.isEmpty(names) && TextUtils.isEmpty(breed) && gender == PetContract.PetEntry.Unknown){
            return ;
        }
        int weight =0 ;
        if(!TextUtils.isEmpty(weightString)){
            weight = Integer.parseInt(weightString) ;
        }


        ContentValues values = new ContentValues() ;
        values.put(PetContract.PetEntry.PetName , names);
        values.put(PetContract.PetEntry.PetBreed , breed);
        values.put(PetContract.PetEntry.PetGender , gender);
        values.put(PetContract.PetEntry.PetWeight , weight);
        if(uri == null ) {
            Uri insertUri = getContentResolver().insert(PetContract.PetEntry.Content_URI, values);


            if (insertUri == null) {
                Toast.makeText(this, R.string.pet_add_fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.pet_add, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowid = getContentResolver().update(uri , values , null , null ) ;
            if(rowid == 0){
                Toast.makeText(this , "error in updating " , Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(this , "pet updated " , Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet() ;
                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                 delete();
                 finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {PetContract.PetEntry.id , PetContract.PetEntry.PetName , PetContract.PetEntry.PetBreed , PetContract.PetEntry.PetGender
         , PetContract.PetEntry.PetWeight} ;

        return new CursorLoader(this , uri , projection , null , null , null) ;

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if(data == null && data.getCount()< 1 ){
            return ;
        }
        if(data.moveToFirst()){
            int nameindex = data.getColumnIndex(PetContract.PetEntry.PetName) ;
            int breedindex = data.getColumnIndex(PetContract.PetEntry.PetBreed) ;
            int genderindex = data.getColumnIndex(PetContract.PetEntry.PetGender) ;
            int weightindex = data.getColumnIndex(PetContract.PetEntry.PetWeight) ;
            String name = data.getString(nameindex) ;
            String breed = data.getString(breedindex) ;
            String weight = Integer.toString(data.getInt(weightindex)) ;
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(weight);
            int gender = data.getInt(genderindex) ;
            switch(gender){
                case PetContract.PetEntry.Unknown :
                    mGenderSpinner.setSelection(0);
                    break ;
                case PetContract.PetEntry.Male :
                    mGenderSpinner.setSelection(1);
                    break ;
                case PetContract.PetEntry.Female :
                    mGenderSpinner.setSelection(2);
                    break ;
                default:
                    mGenderSpinner.setSelection(0);
                    break ;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");

    }

    private void unSavedChangedDialog(DialogInterface.OnClickListener dialoglistener){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this) ;
        alertDialog.setMessage(R.string.unsaved_changes_dialog_msg) ;
        alertDialog.setPositiveButton(R.string.discard , dialoglistener) ;
        alertDialog.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        }) ;
        AlertDialog dialog = alertDialog.create() ;
        dialog.show();
    }

    private void delete(){
        int row = getContentResolver().delete(uri , null , null) ;
        if(row != 0){
            Toast.makeText(this , "pet deleted" , Toast.LENGTH_SHORT ).show();
        }
        else {
            Toast.makeText(this , "error in deleting pet" , Toast.LENGTH_SHORT).show();
        }
    }
}