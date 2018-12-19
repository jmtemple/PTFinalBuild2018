package com.example.proyectotiti;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.DomesticAnimalDesc;
import com.example.proyectotiti.models.Family;
import com.example.proyectotiti.models.Structure;
import com.example.proyectotiti.models.StructureDesc;
import com.example.proyectotiti.models.User;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.R.color.black;
import static android.R.color.darker_gray;
import static android.R.color.holo_blue_bright;
import static android.R.color.holo_blue_dark;
import static android.R.color.holo_blue_light;
import static android.R.color.primary_text_dark;
import static android.R.color.primary_text_dark_nodisable;
import static android.R.color.secondary_text_dark_nodisable;
import static android.R.color.tertiary_text_dark;

public class visitOverview extends AppCompatActivity {

    private static final String TAG = "visitOverview";

    private String familyNum;
    private String visitNum;

    private DatabaseReference mDatabase;
    private DatabaseReference userDatabase;
    private LinearLayout mlinearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_overview);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        //Views
        mlinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        getVisitInfo();
    }

    public void getVisitInfo(){

        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot visitSnapshot) {
                final Visit post = visitSnapshot.getValue(Visit.class);

                // Add value event listener to the list of families
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {

                        for(DataSnapshot ds : userSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);

                            if(post.userID.equals(user.id)){
                                populateForm(post, user.username);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Family failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                };
                userDatabase.addListenerForSingleValueEvent(userListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.addListenerForSingleValueEvent(visitListener);
    }

    public void populateForm(Visit visit, String username){

        TextView nameView = new TextView(this);
        nameView.setText("\n" + visit.basicData.name);
        nameView.setTextSize(25);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nameView.setTextColor(getColor(R.color.colorPrimary));
        }


        TextView dateView = new TextView(this);
        dateView.setText("     Date: " + visit.date.month + " " + visit.date.day + ", " + visit.date.year);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dateView.setTextColor(getColor(R.color.darkGrey));
        }


        TextView userView = new TextView(this);
        userView.setText("     Usuario: " + username);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            userView.setTextColor(getColor(R.color.darkGrey));
        }


        TextView bdataView = new TextView(this);
        bdataView.setText("     Dirrecion: " + visit.basicData.address + "\n" + "     Communidad: " + visit.basicData.community + "\n" + "     Telefono: "+ visit.basicData.phone_number + "\n" + "     Punto GPS: " +visit.basicData.gps_coords + "\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bdataView.setTextColor(getColor(R.color.darkGrey));
        }

        mlinearLayout.addView(nameView);
        mlinearLayout.addView(dateView);
        mlinearLayout.addView(userView);
        mlinearLayout.addView(bdataView);

        if(visit.animals.committed && visit.animals != null){
            TextView animalTitle = new TextView(this);
            animalTitle.setText("Animales");
            animalTitle.setTextSize(23);
            mlinearLayout.addView(animalTitle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animalTitle.setTextColor(getColor(R.color.colorPrimary));
            }

            TextView compliantTitle = new TextView(this);
            compliantTitle.setText("     Cumple: " + visit.animals.compliant+ "\n");
            compliantTitle.setTextSize(15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                compliantTitle.setTextColor(getColor(R.color.darkGrey));
            }
            mlinearLayout.addView(compliantTitle);

            if(visit.animals.wild != null){
                TextView wildTitle = new TextView(this);
                wildTitle.setText("Silvestres");
                wildTitle.setTextSize(20);
                mlinearLayout.addView(wildTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    wildTitle.setTextColor(getColor(R.color.colorPrimary));
                }

                TextView wildText = new TextView(this);
                String animalWildText = "";
                Map<String, AnimalDesc> wildMap = new HashMap<>();
                wildMap = visit.animals.wild;
                for(Map.Entry<String, AnimalDesc> e: wildMap.entrySet()){

                    AnimalDesc ad = e.getValue();
                    if(ad.active){
                        animalWildText += " " + ad.name + "\n" + "     Clasificación: " + ad.classification + "\n" +  "     Tipo: " + ad.type + "\n" + "     Función: " + ad.function + "\n" + "     Origen: " + ad.origin + "\n" + "     Marcaje: " + ad.marking + "\n" + "     Cumple: " + ad.compliant + "\n" + "     Cumple observaciones: " + ad.compliant_desc + "\n" + "     Observaciones: " + ad.observation_desc + "\n";
                    }
                }
                wildText.setText(animalWildText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    wildText.setTextColor(getColor(R.color.darkGrey));
                }
                mlinearLayout.addView(wildText);

            }
            if(visit.animals.domestic != null){
                TextView domTitle = new TextView(this);
                domTitle.setText("Domesticos");
                domTitle.setTextSize(20);
                mlinearLayout.addView(domTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    domTitle.setTextColor(getColor(R.color.colorPrimary));
                }

                TextView domText = new TextView(this);
                String animalDomText = "";
                Map<String, DomesticAnimalDesc> domMap = new HashMap<>();
                domMap = visit.animals.domestic;
                for(Map.Entry<String, DomesticAnimalDesc> e: domMap.entrySet()){

                    DomesticAnimalDesc ad = e.getValue();
                    if(ad.active){
                        animalDomText += " " + ad.type + "\n" + "     Cuanto: " + ad.amount + "\n" + "     Cumple: " + ad.compliant + "\n" + "     Cumple observaciones: " + ad.compliant_desc + "\n";
                    }
                }
                domText.setText(animalDomText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    domText.setTextColor(getColor(R.color.darkGrey));
                }
                mlinearLayout.addView(domText);
            }


        }

        if(visit.structures.committed && visit.structures != null){
            TextView structureTitle = new TextView(this);
            structureTitle.setText("Madera del bosque");
            structureTitle.setTextSize(23);
            mlinearLayout.addView(structureTitle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                structureTitle.setTextColor(getColor(R.color.colorPrimary));
            }

            TextView compliantTitle = new TextView(this);
            compliantTitle.setText("     Cumple: " + visit.structures.compliant+ "\n");
            compliantTitle.setTextSize(15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                compliantTitle.setTextColor(getColor(R.color.darkGrey));
            }
            mlinearLayout.addView(compliantTitle);

            if(visit.structures.construction != null){
                TextView conTitle = new TextView(this);
                conTitle.setText("Construcciones");
                conTitle.setTextSize(20);
                mlinearLayout.addView(conTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    conTitle.setTextColor(getColor(R.color.colorPrimary));
                }

                TextView conText = new TextView(this);
                String structureConText = "";
                Map<String, StructureDesc> conMap = new HashMap<>();
                conMap = visit.structures.construction;
                for(Map.Entry<String, StructureDesc> e: conMap.entrySet()){

                    StructureDesc sd = e.getValue();
                    if(sd.active){
                        structureConText += " " + sd.name + "\n" + "     Metros cuadrados: " + sd.size + "\n" + "     Tipo: " + sd.type + "\n" + "     Función: " + sd.function + "\n" + "     Estado: " + sd.condition + "\n" + "     Cumple: " + sd.compliant + "\n" + "     Cumple observaciones: " + sd.compliant_desc + "\n";
                    }
                }
                conText.setText(structureConText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    conText.setTextColor(getColor(R.color.darkGrey));
                }
                mlinearLayout.addView(conText);

            }
            if(visit.structures.fence != null){
                TextView fenceTitle = new TextView(this);
                fenceTitle.setText("Cercados");
                fenceTitle.setTextSize(20);
                mlinearLayout.addView(fenceTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fenceTitle.setTextColor(getColor(R.color.colorPrimary));
                }

                TextView fenceText = new TextView(this);
                String structureFenceText = "";
                Map<String, StructureDesc> fenceMap = new HashMap<>();
                fenceMap = visit.structures.fence;
                for(Map.Entry<String, StructureDesc> e: fenceMap.entrySet()){

                    StructureDesc sd = e.getValue();
                    if(sd.active){
                        structureFenceText += " " + sd.name + "\n" + "     Metros lineales: " + sd.size + "\n" + "     Tipo: " + sd.type + "\n" + "     Función: " + sd.function + "\n" + "     Estado: " + sd.condition + "\n" + "     Cumple: " + sd.compliant + "\n" + "     Cumple observaciones: " + sd.compliant_desc + "\n";
                    }
                }
                fenceText.setText(structureFenceText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fenceText.setTextColor(getColor(R.color.darkGrey));
                }
                mlinearLayout.addView(fenceText);
            }


            if(visit.structures.cookWithWood || visit.structures.cookWithCoal){

                TextView structureText = new TextView(this);
                String text = "";
                if(visit.structures.cookWithWood){
                    text += "Cocina con leña\n";
                }
                if(visit.structures.cookWithCoal){
                    text += "Cocina con carbon\n";
                }
                text += "Frecuencia con leña: " + visit.structures.stove_freq_lena + "\n" + "Tipo de estufa con leña: " + visit.structures.stove_type_lena + "\n";
                text += "Frecuencia con carbon: " + visit.structures.stove_freq_carbon + "\n" + "Tipo de estufa con carbon: " + visit.structures.stove_type_carbon + "\n";
                structureText.setText(text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    structureText.setTextColor(getColor(R.color.darkGrey));
                }
                mlinearLayout.addView(structureText);
            }

        }

        if(visit.recycle.committed && visit.recycle != null){

            TextView recycleTitle = new TextView(this);
            recycleTitle.setText("Reciclar");
            recycleTitle.setTextSize(23);
            mlinearLayout.addView(recycleTitle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recycleTitle.setTextColor(getColor(R.color.colorPrimary));
            }

            TextView compliantTitle = new TextView(this);
            compliantTitle.setText("     Cumple: " + visit.recycle.compliant+ "\n");
            compliantTitle.setTextSize(15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                compliantTitle.setTextColor(getColor(R.color.darkGrey));
            }
            mlinearLayout.addView(compliantTitle);

            TextView recycleText = new TextView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recycleText.setTextColor(getColor(R.color.darkGrey));
            }

            if(visit.recycle.doRecycle){
                recycleText.setText("      A quién entrega reciclados: " + visit.recycle.recycle_deliver + "\n" + "      Recicla: " + visit.recycle.recycle_items + "\n");
            }
            else{
                recycleText.setText("      Cómo maneja residuos? "+visit.recycle.waste_man + "\n");
            }
            mlinearLayout.addView(recycleText);

        }

        if(visit.conservation.committed && visit.conservation != null){
            TextView conservationTitle = new TextView(this);
            conservationTitle.setText("Conservacion");
            conservationTitle.setTextSize(23);
            mlinearLayout.addView(conservationTitle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                conservationTitle.setTextColor(getColor(R.color.colorPrimary));
            }

            TextView compliantTitle = new TextView(this);
            compliantTitle.setText("     Cumple: " + visit.conservation.compliant+ "\n");
            compliantTitle.setTextSize(15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                compliantTitle.setTextColor(getColor(R.color.darkGrey));
            }
            mlinearLayout.addView(compliantTitle);

            TextView conservationText = new TextView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                conservationText.setTextColor(getColor(R.color.darkGrey));
            }

            conservationText.setText("      Hectáreas de la propiedad: " + visit.conservation.area + "\n" + "      Número de hectáreas bajo acuerdo: " + visit.conservation.agree_area + "\n");
            mlinearLayout.addView(conservationText);

        }

    }

    public void openViewVisits(View v){

        Intent intentDetails = new Intent(visitOverview.this, viewVisits.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
