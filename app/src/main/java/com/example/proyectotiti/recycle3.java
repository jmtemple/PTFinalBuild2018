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

import com.example.proyectotiti.models.Recycle;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class recycle3 extends AppCompatActivity {

    private static final String TAG = "recycle3";

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
    private Spinner wasteSpinner;
    private EditText waste_man;
    private TextView textView;

    private Recycle recycle;
    private Class nextField;

    private Boolean addNewOption;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle3);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        // Views
        wasteSpinner = (Spinner) findViewById(R.id.spinnerResidue);
        textView = (TextView) findViewById(R.id.otherResidue);
        waste_man = (EditText) findViewById(R.id.editText2);

        setUpWasteSpinner();

        wasteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("Otro")){
                    waste_man.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                } else{
                    waste_man.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    addNewOption = false;

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        readFromDB();
    }

    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);

                if(post != null){
                    if(post.recycle.waste_man != null){
                        prepopulate(post.recycle);

                    }
                    if(post.conservation.committed){
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(visitListener);

    }


    private void setUpWasteSpinner() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("reciclar_waste");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("reciclar_waste", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> wasteAdapter = new ArrayAdapter<String>(recycle3.this, android.R.layout.simple_spinner_item, types);
                wasteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wasteSpinner.setAdapter(wasteAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void prepopulate(Recycle post){
        recycle = post;
        waste_man.setText(recycle.waste_man);

    }

    public void submitRecycle(View v){
        if(addNewOption) {
            mDatabase.child("recycle").child("waste_man").setValue(waste_man.getText().toString());
            DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("reciclar_waste").child(waste_man.getText().toString());
            nDatabase.setValue(waste_man.getText().toString());


        }else{

            mDatabase.child("recycle").child("waste_man").setValue(wasteSpinner.getSelectedItem().toString());
        }

        openNextField(v);
    }

    public void openRecycle1(View v){

        Intent intentDetails = new Intent(recycle3.this, recycle1.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openNextField(View v){
        Intent intentDetails = new Intent(recycle3.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
