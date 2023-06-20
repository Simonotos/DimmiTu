package it.unimib.dimmitu.Impostazioni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import it.unimib.dimmitu.VisualizzaInteressi.ActivityVisualizzaInteressi;
import it.unimib.dimmitu.Home.ActivityHomePageConsigli;
import it.unimib.dimmitu.Login.ActivityCambioPassword;
import it.unimib.dimmitu.Login.ActivityLogin;
import it.unimib.dimmitu.R;


public class ImpostazioniActivity extends AppCompatActivity{

    private TextView email;
    private String  userEmail;
    private HashMap<String, String> colori_map = new HashMap<>();



    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Spinner spin_libri, spin_videogiochi, spin_film;

    ModelImpostazioni model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impostazioni);

        email = findViewById(R.id.textView_UserEmail);

        spin_libri = findViewById(R.id.spinner_libri);
        spin_videogiochi = findViewById(R.id.spinner_videogiochi);
        spin_film = findViewById(R.id.spinner_film);


        String useName = "";
        if (useName != null) {
            userEmail = currentUser.getEmail();
            email.setText(userEmail);
        }



                
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.settings_navBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home_navBar:
                        startActivity(new Intent(getApplicationContext(), ActivityHomePageConsigli.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.interests_navBar:
                        startActivity(new Intent(getApplicationContext(), ActivityVisualizzaInteressi.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings_navBar:
                        return true;
                }
                return false;
            }
        });

        model = new ModelImpostazioni(getApplicationContext(), spin_libri, spin_videogiochi, spin_film, currentUser, colori_map);

        Button button_salva = findViewById(R.id.button_salva);
        button_salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva();
            }
        });

        Button button_logout = findViewById(R.id.button_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));}
        });

        Button reset_password = findViewById(R.id.reset_password);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityCambioPassword.class));;
            }
        });

    }

        public void salva(){
            FirebaseFirestore db= FirebaseFirestore.getInstance();
            db.collection("Utente")
                    .document(currentUser.getEmail())
                    .collection("colors")
                    .document("colori")
                    .set(colori_map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), ActivityHomePageConsigli.class));
                        }
                    });
            Toast.makeText(ImpostazioniActivity.this, "Modifiche salvate!", Toast.LENGTH_SHORT).show();
        }


    }

