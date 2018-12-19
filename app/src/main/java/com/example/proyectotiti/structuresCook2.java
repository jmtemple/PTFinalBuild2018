package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.Structure;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class structuresCook2 extends AppCompatActivity {

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;
    private String selected;

    // Views
   // private EditText stove_freq;
    //private EditText stove_type;

    private EditText otherStoveType;
    private Spinner spinnerFreq;
    private Spinner spinnerStoveType;
    private EditText otherStoveType2;
    private Spinner spinnerFreq2;
    private Spinner spinnerStoveType2;

    private Structure structure;
    private Class nextField;
    private boolean addNewOption = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structures_cook2);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        selected = extrasBundle.getString("data");
        // Views
        spinnerStoveType = (Spinner) findViewById(R.id.spinnerStoveType);
        final TextView spinnerStoveTypeTextView = (TextView) findViewById(R.id.textViewCookingQ5);
        final TextView otherTextView = (TextView) findViewById(R.id.textViewStoveType);
        otherStoveType = (EditText) findViewById(R.id.editTextStoveType);

        spinnerStoveType2 = (Spinner) findViewById(R.id.spinnerStoveType2);
        final TextView spinnerStoveType2TextView = (TextView) findViewById(R.id.textViewCookingQ52);
        final TextView otherTextView2 = (TextView) findViewById(R.id.textViewStoveType2);
        otherStoveType2 = (EditText) findViewById(R.id.editTextStoveType2);

        spinnerFreq = (Spinner) findViewById(R.id.spinnerFreq);

        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerFrequency, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq.setAdapter(freqAdapter);

        spinnerFreq2 = (Spinner) findViewById(R.id.spinnerFreq2);

        ArrayAdapter<CharSequence> freqAdapter2 = ArrayAdapter.createFromResource(this, R.array.spinnerFrequency, android.R.layout.simple_spinner_item);
        freqAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq2.setAdapter(freqAdapter2);


        spinnerFreq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerFreq2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpTypeSpinner();


        // Set up onitemlistener to check if the user choses "other"
        spinnerStoveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);

                if (chosenOption.equals("Otro")) {
                    otherStoveType.setVisibility(View.VISIBLE);
                    otherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }else{
                    otherStoveType.setVisibility(View.GONE);
                    otherTextView.setVisibility(View.GONE);
                    addNewOption = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setUpTypeSpinner2();

        spinnerStoveType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);

                if (chosenOption.equals("Otro")) {
                    otherStoveType2.setVisibility(View.VISIBLE);
                    otherTextView2.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }else{
                    otherStoveType2.setVisibility(View.GONE);
                    otherTextView2.setVisibility(View.GONE);
                    addNewOption = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit" + visitNum);

        if(selected.equals("both")){
            spinnerStoveTypeTextView.setVisibility(View.VISIBLE);
            spinnerFreq.setVisibility(View.VISIBLE);
            spinnerStoveType.setVisibility(View.VISIBLE);
            spinnerStoveType2TextView.setVisibility(View.VISIBLE);
            spinnerFreq2.setVisibility(View.VISIBLE);
            spinnerStoveType2.setVisibility(View.VISIBLE);
        }else if(selected.equals("Wood")){
            spinnerStoveTypeTextView.setVisibility(View.VISIBLE);
            spinnerFreq.setVisibility(View.VISIBLE);
            spinnerStoveType.setVisibility(View.VISIBLE);
        }else if(selected.equals("Coal")){
            spinnerStoveType2TextView.setVisibility(View.VISIBLE);
            spinnerFreq2.setVisibility(View.VISIBLE);
            spinnerStoveType2.setVisibility(View.VISIBLE);
        }

        readFromDB();
    }

        // Pulls the types of wild animals from the Firebase database

        private void setUpTypeSpinner () {
            DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("stove_type");
            tDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    final List<String> types = new ArrayList<String>();

                    for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                        String typeName = typeSnapshot.getValue(String.class);
                        Log.d("stove_type", typeName);
                        types.add(typeName);
                    }

                    ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(structuresCook2.this, android.R.layout.simple_spinner_item, types);
                    typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStoveType.setAdapter(typesAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    private void setUpTypeSpinner2() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("stove_type");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("stove_type", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> typesAdapter2 = new ArrayAdapter<String>(structuresCook2.this, android.R.layout.simple_spinner_item, types);
                typesAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStoveType2.setAdapter(typesAdapter2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void readFromDB(){

        // Add value event listener to the list of families
        ValueEventListener bdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("DEBUG", String.valueOf(dataSnapshot));
                Visit post = dataSnapshot.getValue(Visit.class);
                if(post != null){

                   if(post.structures.stove_freq_lena != null && post.structures.stove_type_lena != null && post.structures.stove_freq_carbon != null && post.structures.stove_type_carbon != null){
                     prepopulate(post.structures);
                    }
                    
                    if(post.recycle.committed){
                        nextField = recycle1.class;
                    }
                    else if(post.conservation.committed){
                        nextField = conservation.class;
                    }
                    else{
                        nextField = visitOverview.class;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(bdListener);

    }

    public void prepopulate(Structure post){
        structure = post;
        boolean inSpinner = true;
        for (int i=0;i<spinnerFreq.getCount();i++){
          if (spinnerFreq.getItemAtPosition(i).equals(post.stove_freq_lena)){
               spinnerFreq.setSelection(i);
            }
        }

        for (int i=0;i<spinnerStoveType.getCount();i++){
            if (spinnerStoveType.getItemAtPosition(i).equals(post.stove_type_lena)){
               spinnerStoveType.setSelection(i);
               inSpinner = false;
            }
        }
        if(inSpinner){
            spinnerStoveType.setSelection(3);
            otherStoveType.setText(post.stove_type_lena);
        }

        inSpinner = true;

        for (int i=0;i<spinnerFreq2.getCount();i++){
            if (spinnerFreq2.getItemAtPosition(i).equals(post.stove_freq_carbon)){
                spinnerFreq2.setSelection(i);
            }
        }

        for (int i=0;i<spinnerStoveType2.getCount();i++){
            if (spinnerStoveType2.getItemAtPosition(i).equals(post.stove_type_carbon)){
                spinnerStoveType2.setSelection(i);
                inSpinner = false;
            }
        }
        if(inSpinner){
            spinnerStoveType2.setSelection(3);
            otherStoveType2.setText(post.stove_type_carbon);
        }
    }

    public void submitStructure(View v){

        if(addNewOption){
            if(selected.equals("both")) {
                if (spinnerStoveType.getSelectedItem().toString().equals("Otro") && spinnerStoveType2.getSelectedItem().toString().equals("Otro")) {
                    mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_lena").setValue(otherStoveType.getText().toString());
                    mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_carbon").setValue(otherStoveType2.getText().toString());

                } else if (spinnerStoveType.getSelectedItem().toString().equals("Otro")) {
                    mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_lena").setValue(otherStoveType.getText().toString());
                    mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_carbon").setValue(spinnerStoveType2.getSelectedItem().toString());

                } else{
                    mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_lena").setValue(spinnerStoveType.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
                    mDatabase.child("structures").child("stove_type_carbon").setValue(otherStoveType2.getText().toString());
                }

            }else if(selected.equals("Wood")){
            mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
            mDatabase.child("structures").child("stove_type_lena").setValue(otherStoveType.getText().toString());
            mDatabase.child("structures").child("stove_freq_carbon").setValue(null);
            mDatabase.child("structures").child("stove_type_carbon").setValue(null);

            }else if(selected.equals("Coal")){
            mDatabase.child("structures").child("stove_freq_lena").setValue(null);
            mDatabase.child("structures").child("stove_type_lena").setValue(null);
            mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
            mDatabase.child("structures").child("stove_type_carbon").setValue(otherStoveType2.getText().toString());
            }

            // Add the new wild animal option to the database
            //DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("stove_types").child(otherStoveType.getText().toString());
            //nDatabase.setValue(otherStoveType.getText().toString());
        } else{
            if(selected.equals("both")){
                mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_type_lena").setValue(spinnerStoveType.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_type_carbon").setValue(spinnerStoveType2.getSelectedItem().toString());

          }else if(selected.equals("Wood")){
                mDatabase.child("structures").child("stove_freq_lena").setValue(spinnerFreq.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_type_lena").setValue(spinnerStoveType.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_freq_carbon").setValue(null);
                mDatabase.child("structures").child("stove_type_carbon").setValue(null);

            }else if(selected.equals("Coal")){
                mDatabase.child("structures").child("stove_freq_lena").setValue(null);
                mDatabase.child("structures").child("stove_type_lena").setValue(null);
                mDatabase.child("structures").child("stove_freq_carbon").setValue(spinnerFreq2.getSelectedItem().toString());
                mDatabase.child("structures").child("stove_type_carbon").setValue(spinnerStoveType2.getSelectedItem().toString());
            }


        }

        openNextField(v);
    }

    public void openMadera4(View v){
        Intent intentDetails = new Intent(structuresCook2.this, structuresCook.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void openNextField(View v){
        Intent intentDetails = new Intent(structuresCook2.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }
}
