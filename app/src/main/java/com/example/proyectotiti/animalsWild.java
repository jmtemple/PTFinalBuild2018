package com.example.proyectotiti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.cameraOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class animalsWild extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    // Passed from last screen
    private String familyNum;
    private String animalNum;
    private String visitNum;

    // Photo capability
    private ImageButton mImageButton;
    private Uri photoURI;
    private ArrayList<Uri> uris = new ArrayList<Uri>() {};
    private Map<String, String> images;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final  int REQUEST_DELETE = 2;
    private StorageReference filePath;
    private Uri path;
    private String mCurrentPhotoPath;

    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;

    // Views
    private LinearLayout mainLinearLayout;
    private EditText animalName;
    //private EditText animalMarking;
    private Switch animalCompliant;
    private EditText animalCompliantText;
    private EditText animalOther;
    //private Spinner typeSpinner;
    private Spinner spinnerMammal;
    private Spinner spinnerBird;
    private Spinner spinnerReptile;
    private Spinner spinnerClassification;
    //private Spinner spinnerWildAnimal;
    private Spinner spinnerFunction;
    private Spinner spinnerOrigin;
    private EditText animalObservations;
    private EditText animalMarking;
    private String   animalType;

    private long animalsCount;
    private boolean addNewOption;
    private boolean selected;
    private AnimalDesc animal;
    private AnimalDesc new_animal;
    private AnimalDesc post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_wild);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        animalNum = extrasBundle.getString("animalNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits");

        //Views
        animalName = (EditText)findViewById(R.id.editTextAnimal);
        //animalMarking = (EditText)findViewById(R.id.editTextMarking);
        animalCompliant = (Switch)findViewById(R.id.switch1);
        animalCompliantText = (EditText)findViewById(R.id.editTextCompliance);
        //typeSpinner = (Spinner) findViewById(R.id.spinnerType);
        spinnerBird = (Spinner) findViewById(R.id.spinnerBird);
        spinnerMammal = (Spinner) findViewById(R.id.spinnerMammal);
        spinnerReptile = (Spinner) findViewById(R.id.spinnerReptile);
        final TextView typeSpinnerTextView = (TextView) findViewById(R.id.textViewWildAnimalsQ4);
        final TextView animalOtherTextView = (TextView) findViewById(R.id.textViewWildAnimalsOther);
        animalOther = (EditText)findViewById(R.id.editTextOther);
        animalObservations = (EditText)findViewById(R.id.editTextObservations);
        animalMarking = (EditText)findViewById(R.id.editTextMarking);

        ArrayAdapter<CharSequence> wildAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerAnimalUses, android.R.layout.simple_spinner_item);
        wildAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        imageOne = (ImageView) findViewById(R.id.imageOne);
        imageTwo = (ImageView) findViewById(R.id.imageTwo);
        imageThree = (ImageView) findViewById(R.id.imageThree);

        spinnerFunction = (Spinner) findViewById(R.id.spinnerFunction);
        spinnerOrigin = (Spinner) findViewById(R.id.spinnerOrigin);

        spinnerClassification = (Spinner) findViewById(R.id.layoutSpinnerClassification);
        ArrayAdapter<CharSequence> classificationAdapter = ArrayAdapter.createFromResource(this,R.array.arraySpinnerClassification, android.R.layout.simple_spinner_item);
        classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassification.setAdapter(classificationAdapter);


        spinnerClassification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

                if (chosenOption.equals("Reptiles")){
                    spinnerReptile.setVisibility(View.VISIBLE);
                   // typeSpinnerTextView.setVisibility(View.VISIBLE);

                }else{
                    spinnerReptile.setVisibility(View.GONE);
                }
                if (chosenOption.equals("Aves")){
                    spinnerBird.setVisibility(View.VISIBLE);
                   // typeSpinnerTextView.setVisibility(View.VISIBLE);
                }else{
                    spinnerBird.setVisibility(View.GONE);
                }
                if (chosenOption.equals("Mamíferos")){
                    spinnerMammal.setVisibility(View.VISIBLE);
                   // typeSpinnerTextView.setVisibility(View.VISIBLE);
                }else{
                    spinnerMammal.setVisibility(View.GONE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSpinnerFunction();
        spinnerFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSpinnerMammal();
        spinnerMammal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("Otro")){
                    animalOther.setVisibility(View.VISIBLE);
                    animalOtherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }else{
                    animalOther.setVisibility(View.GONE);
                    animalOtherTextView.setVisibility(View.GONE);
                    addNewOption = false;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSpinnerReptile();
        spinnerReptile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("Otro")){
                    animalOther.setVisibility(View.VISIBLE);
                    animalOtherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }else{
                    animalOther.setVisibility(View.GONE);
                    animalOtherTextView.setVisibility(View.GONE);
                    addNewOption = false;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSpinnerBird();
        spinnerBird.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("Otro")){
                    animalOther.setVisibility(View.VISIBLE);
                    animalOtherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }else{
                    animalOther.setVisibility(View.GONE);
                    animalOtherTextView.setVisibility(View.GONE);
                    addNewOption = false;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSpinnerOrigin();
        spinnerOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mainLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);
        mImageButton = (ImageButton)findViewById(R.id.imageButton);
        storageReference = FirebaseStorage.getInstance().getReference();




        // When the photo button is pressed, app will switch to android camera
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        if (animalNum.equals("-1")){
            getAnimalNumber();
        }
        else {
            readFromDB();
        }
    }

    // Pulls the types of wild animals from the Firebase database
    private void setUpSpinnerMammal() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_mammals");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("wild_animals_mammals", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> mammalAdapter = new ArrayAdapter<String>(animalsWild.this, android.R.layout.simple_spinner_item, types);
                mammalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMammal.setAdapter(mammalAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpSpinnerReptile() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_reptiles");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("wild_animals_reptiles", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> reptileAdapter = new ArrayAdapter<String>(animalsWild.this, android.R.layout.simple_spinner_item, types);
                reptileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerReptile.setAdapter(reptileAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpSpinnerBird() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_bird");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("wild_animals_bird", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> birdAdapter = new ArrayAdapter<String>(animalsWild.this, android.R.layout.simple_spinner_item, types);
                birdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBird.setAdapter(birdAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpSpinnerOrigin() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animal_origin");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("wild_animal_origin", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> originAdapter = new ArrayAdapter<String>(animalsWild.this, android.R.layout.simple_spinner_item, types);
                originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOrigin.setAdapter(originAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpSpinnerFunction() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animal_function");

        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("wild_animal_function", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> functionAdapter = new ArrayAdapter<String>(animalsWild.this, android.R.layout.simple_spinner_item, types);
                functionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFunction.setAdapter(functionAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /* This function runs upon the pressing of the camera button.
    * It will set up a new photo file and put the new photo into there.*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // startActivityForResult(takePictureIntent, 0);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.proyectotiti.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir
        );

        Log.d("filepath", image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* This function runs upon completing taking a photo.
*   It will upload the file to storage and add the uri to an array.*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            galleryAddPic();

            uris.add(photoURI);

            if(uris.size() == 1) {
                path = uris.get(0);
                Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageOne);
                imageOne.setOnClickListener(thumbnailClick);
                images = null;
                mImageButton.setVisibility(View.VISIBLE);
            }

            if(uris.size() == 2) {

                path = uris.get(1);
                Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageTwo);
                imageTwo.setOnClickListener(thumbnailClick);
                images = null;
                mImageButton.setVisibility(View.VISIBLE);
            }

            if(uris.size() == 3) {
                path = uris.get(2);
                Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageThree);
                imageThree.setOnClickListener(thumbnailClick);
                images = null;
                mImageButton.setVisibility(View.GONE);
            }


        }

         if(requestCode == REQUEST_DELETE && resultCode == REQUEST_DELETE) {

             String ss =  data.getStringExtra("delete");
             path = Uri.parse(ss);
             final StorageReference store = FirebaseStorage.getInstance().getReference(path.toString());
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("¿Por qué quieres eliminar?");

            EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);

             // Set up the buttons
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             uris.remove(path);
                             images = null;

                             Map<String, String> uploads = new HashMap<String, String>();

                             for (Uri uri : uris){
                                 String uploadId = mDatabase.push().getKey();
                                 //creating the upload object to store uploaded image details
                                 uploads.put(uploadId, uri.toString());

                                 //adding an upload to firebase database
                             }

                             images = uploads;


                    // int num = Integer.parseInt(animalNum);
                     //animalNum = String.valueOf(num -1);
                     String id = "a_" + (animalNum);


                     if(addNewOption){
                         new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(),animalOther.getText().toString(), spinnerOrigin.getSelectedItem().toString(),spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "",images, animalCompliant.isChecked(), animalCompliantText.getText().toString(),animalObservations.getText().toString());

                     }
                     else{
                         if(spinnerClassification.getSelectedItem().toString().equals("Reptiles")) {
                             new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerReptile.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
                         }
                         if(spinnerClassification.getSelectedItem().toString().equals("Aves")) {
                             new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerBird.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
                         }
                         if(spinnerClassification.getSelectedItem().toString().equals("Mamíferos")) {
                             new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerMammal.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
                         }
                     }
                     mDatabase.child("visit"+visitNum).child("animals").child("wild").child(id).setValue(new_animal);

                    finish();
                    startActivity(getIntent());

                 }

             });

             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.cancel();
                 }
             });
             builder.show();
         }
    }

    private void galleryAddPic(){

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    /* This function runs once the family count has been read from the database.*/
    public void getAnimalNumber(){

        // Add value event listener to find the family number
        final ValueEventListener animalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                animalsCount = dataSnapshot.getChildrenCount();
                animalNum = String.valueOf((int)animalsCount + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("animals").child("wild").addValueEventListener(animalListener);
    }

    // Pulls data from Firebase database
    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener aListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(AnimalDesc.class);
                prepopulate(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("animals").child("wild").child("a_"+animalNum).addListenerForSingleValueEvent(aListener);
    }

    public void prepopulate(AnimalDesc post){
        animal = post;
        // Set all the editTexts to original data
        animalMarking.setText(String.valueOf(animal.marking));
        animalName.setText(animal.name);


        for (int i=1;i<spinnerFunction.getCount();i++){
            if (spinnerFunction.getItemAtPosition(i).equals(post.function)){
                spinnerFunction.setSelection(i);
            }
        }
        for (int i=1;i<spinnerOrigin.getCount();i++){
            if (spinnerOrigin.getItemAtPosition(i).equals(post.origin)){
                spinnerOrigin.setSelection(i);
            }
        }

        for (int i=0;i<spinnerClassification.getCount();i++){
            if (spinnerClassification.getItemAtPosition(i).equals(post.classification)){
                spinnerClassification.setSelection(i);
            }
        }


        if(spinnerClassification.getSelectedItem().toString().equals("Reptiles")) {
            for ( int i = 0; i < spinnerReptile.getCount(); i++) {
                if (spinnerReptile.getItemAtPosition(i).equals(post.type)) {
                    spinnerReptile.setSelection(i);
                }
            }
        }
        if(spinnerClassification.getSelectedItem().toString().equals("Aves")) {
            for ( int i = 0; i < spinnerBird.getCount(); i++) {
                if (spinnerBird.getItemAtPosition(i).equals(post.type)) {
                    spinnerBird.setSelection(i);
                }
            }
        }
        if(spinnerClassification.getSelectedItem().toString().equals("Mamíferos")) {
            for ( int i = 0; i < spinnerMammal.getCount(); i++) {
                if (spinnerMammal.getItemAtPosition(i).equals(post.type)) {
                    spinnerMammal.setSelection(i);
                }
            }
        }


        animalCompliantText.setText(animal.compliant_desc);
        animalCompliant.setChecked(animal.compliant);
        animalObservations.setText(animal.observation_desc);
        //Map<String, String> image_object = animal.images;
        images = animal.images;
        Iterator it = null;

        // Display all saved images
        if (images!=null){
            it = images.entrySet().iterator();
            //mImageButton.setVisibility(View.GONE);

            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                //ImageView image = new ImageView(animalsWild.this);
                String s = pair.getValue().toString();
                path = Uri.parse(s);
                uris.add(path);
            }

        }

        if(uris.size() == 1) {
            path = uris.get(0);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageOne);
            //mainLinearLayout.addView(image);
            imageOne.setOnClickListener(thumbnailClick);
            images = null;
            mImageButton.setVisibility(View.VISIBLE);
        }

        if(uris.size() == 2) {

            path = uris.get(0);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageOne);
            //mainLinearLayout.addView(image);
            imageOne.setOnClickListener(thumbnailClick);

            path = uris.get(1);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageTwo);
            //mainLinearLayout.addView(image);
            imageTwo.setOnClickListener(thumbnailClick);
            images = null;
            mImageButton.setVisibility(View.VISIBLE);
        }

        if(uris.size() == 3) {

            path = uris.get(0);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageOne);
            //mainLinearLayout.addView(image);
            imageOne.setOnClickListener(thumbnailClick);

            path = uris.get(1);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageTwo);
            //mainLinearLayout.addView(image);
            imageTwo.setOnClickListener(thumbnailClick);

            path = uris.get(2);
            Picasso.with(imageOne.getContext()).load(path).resize(200, 200).into(imageThree);
            //mainLinearLayout.addView(image);
            imageThree.setOnClickListener(thumbnailClick);
            images = null;
            mImageButton.setVisibility(View.GONE);
        }

    }

    // Run when the user submits the animal
    public void submitAnimal(View v){

       for(int i = 0; i < uris.size(); i++) {
           //displaying progress dialog while image is uploading
           final ProgressDialog progressDialog = new ProgressDialog(this);
           progressDialog.setTitle("Uploading");
           progressDialog.show();

           Uri uri = uris.get(i);

           //getting the storage reference

           filePath = storageReference.child("Photos").child(uri.getLastPathSegment());

           //adding the file to reference


           filePath.putFile(uri)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           //dismissing the progress dialog
                           progressDialog.dismiss();

                           //displaying success toast
                           Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                       }
                   });
       }
        Map<String, String> uploads = new HashMap<String, String>();

        for (Uri urii : uris){
            String uploadId = mDatabase.push().getKey();
            //creating the upload object to store uploaded image details
            uploads.put(uploadId, urii.toString());

            //adding an upload to firebase database
        }
        if(images !=null){
            images.putAll(uploads);
        }
        else{
            images = uploads;
        }

        String id = "a_" + animalNum;


        if(addNewOption){
            new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), animalOther.getText().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "",images, animalCompliant.isChecked(), animalCompliantText.getText().toString(),animalObservations.getText().toString());


            if(spinnerClassification.getSelectedItem().toString().equals("Reptiles")) {
                DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_reptiles").child(animalOther.getText().toString());
                nDatabase.setValue(animalOther.getText().toString());

            }
            if(spinnerClassification.getSelectedItem().toString().equals("Aves")) {
                DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_bird").child(animalOther.getText().toString());
                nDatabase.setValue(animalOther.getText().toString());

            }
            if(spinnerClassification.getSelectedItem().toString().equals("Mamíferos")) {
                DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("wild_animals_mammals").child(animalOther.getText().toString());
                nDatabase.setValue(animalOther.getText().toString());
            }

        }
        else{
            if(spinnerClassification.getSelectedItem().toString().equals("Reptiles")) {
                new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerReptile.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
            }
            if(spinnerClassification.getSelectedItem().toString().equals("Aves")) {
                new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerBird.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
            }
            if(spinnerClassification.getSelectedItem().toString().equals("Mamíferos")) {
                new_animal = new AnimalDesc(spinnerClassification.getSelectedItem().toString(), spinnerMammal.getSelectedItem().toString(), spinnerOrigin.getSelectedItem().toString(), spinnerFunction.getSelectedItem().toString(), animalMarking.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString(), animalObservations.getText().toString());
            }
        }
        mDatabase.child("visit"+visitNum).child("animals").child("wild").child(id).setValue(new_animal);
        openAnimalsHome(v);
    }

    public void openAnimalsHome(View v){
        Intent intentDetails = new Intent(animalsWild.this, animalsHome.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }


    private  View.OnClickListener thumbnailClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        try {
                switch (view.getId()){
                    case R.id.imageOne:
                        Intent i1 = new Intent(animalsWild.this, cameraOptions.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Reimage", uris.get(0).toString());
                        i1.putExtras(bundle1);
                        startActivityForResult(i1,REQUEST_DELETE);
                        break;
                    case R.id.imageTwo:
                        Intent i2 = new Intent(animalsWild.this, cameraOptions.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("Reimage", uris.get(1).toString());
                        i2.putExtras(bundle2);
                        startActivityForResult(i2,REQUEST_DELETE);
                        break;
                    case  R.id.imageThree:
                        Intent i3 = new Intent(animalsWild.this, cameraOptions.class);
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("Reimage", uris.get(2).toString());
                        i3.putExtras(bundle3);
                        startActivityForResult(i3,REQUEST_DELETE);
                        break;
                }



        }catch (Exception e){
            e.printStackTrace();
        }

        }
    };
}
