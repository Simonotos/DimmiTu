package it.unimib.dimmitu.Login;


import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import it.unimib.dimmitu.CercaInteressi.HomePageActivity;
import it.unimib.dimmitu.R;

public class ActivitySceltaInteressi extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button inizia;
    FirebaseFirestore db;
    Map<String, String> colori = new HashMap<>();
    FirebaseAuth mAuth;


    String[] colorNames={"Giallo","Corallo","Viola"};
    int colors[] = {R.drawable.giallo, R.drawable.corallo, R.drawable.viola};
    CustomAdapterScelta customAdapter;
    CustomAdapterScelta customAdapter2;
    CustomAdapterScelta customAdapter3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scelta_interessi);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        Spinner spin = findViewById(R.id.spinner);
        Spinner spin2 = findViewById(R.id.spinner2);
        Spinner spin3 = findViewById(R.id.spinner3);

        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        spin2.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        spin3.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        customAdapter = new CustomAdapterScelta(getApplicationContext(), colors, colorNames);
        customAdapter2 = new CustomAdapterScelta(getApplicationContext(), colors, colorNames);
        customAdapter3 = new CustomAdapterScelta(getApplicationContext(), colors, colorNames);


        spin.setAdapter(customAdapter);
        spin2.setAdapter(customAdapter2);
        spin3.setAdapter(customAdapter3);


        inizia =findViewById(R.id.inizia);
        inizia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                db.collection("Utente")
                        .document(currentUser.getEmail())
                        .collection("colors")
                        .document("colori")
                        .set(colori)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(ActivitySceltaInteressi.this, HomePageActivity.class));
                            }
                        });

            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getApplicationContext(), colorNames[position], Toast.LENGTH_LONG).show();
        switch (arg0.getId()){
            case R.id.spinner:

                if(colorNames[position].equals("Giallo")){
                    colori.put("coloreVideogiochi", "#FFDB15");
                }else if (colorNames[position].equals("Corallo")){
                    colori.put("coloreVideogiochi", "#FF5765");
                }else{
                    colori.put("coloreVideogiochi", "#8A6FDF");
                }


                break;
            case R.id.spinner2:

                if(colorNames[position].equals("Giallo")){
                    colori.put("coloreFilm", "#FFDB15");
                }else if (colorNames[position].equals("Corallo")){
                    colori.put("coloreFilm", "#FF5765");
                }else{
                    colori.put("coloreFilm", "#8A6FDF");
                }

                break;
            case R.id.spinner3:

                if(colorNames[position].equals("Giallo")){
                    colori.put("coloreLibri", "#FFDB15");
                }else if (colorNames[position].equals("Corallo")){
                    colori.put("coloreLibri", "#FF5765");
                }else{
                    colori.put("coloreLibri", "#8A6FDF");
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}





