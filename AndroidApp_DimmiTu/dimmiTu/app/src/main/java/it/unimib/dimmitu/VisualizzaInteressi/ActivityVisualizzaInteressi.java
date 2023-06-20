package it.unimib.dimmitu.VisualizzaInteressi;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.*;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

import it.unimib.dimmitu.Home.ActivityHomePageConsigli;
import it.unimib.dimmitu.Impostazioni.ImpostazioniActivity;
import it.unimib.dimmitu.R;


public class ActivityVisualizzaInteressi extends AppCompatActivity implements View.OnClickListener {

    LinearLayout  layout_images_videogames;
    LinearLayout  layout_images_film;
    LinearLayout  layout_images_book;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ModelVisualizzaInteressi model;

    private int num_elementi_da_cancellare = 0;
    HashMap<String, Integer> list_image; // contiene il link e la posizione nel layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_image = new HashMap<String, Integer>();

        setContentView(R.layout.visualizza);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        CardView cardview_tv = findViewById(R.id.cardview_tv);
        CardView cardview_game = findViewById(R.id.cardview_game);
        CardView cardview_books = findViewById(R.id.cardview_books);

        model = new ModelVisualizzaInteressi(getApplicationContext(), cardview_tv, cardview_game, cardview_books);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.interests_navBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_navBar:
                        startActivity(new Intent(getApplicationContext(), ActivityHomePageConsigli.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.interests_navBar:
                        return true;
                    case R.id.settings_navBar:
                        startActivity(new Intent(getApplicationContext(), ImpostazioniActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        layout_images_videogames = findViewById(R.id.layout_videogames);
        model.addFromDbToLayout("Utente/" + currentUser.getEmail() + "/game", "image" , layout_images_videogames, "game");
        //Add Image to Layout programmativamente FILM
        layout_images_film = findViewById(R.id.layout_film);
        model.addFromDbToLayout("Utente/" + currentUser.getEmail() + "/tv", "image" , layout_images_film, "tv");

        layout_images_videogames = findViewById(R.id.layout_videogames);
        model.addFromDbToLayout("Utente/" + currentUser.getEmail() + "/game", "image" , layout_images_videogames, "game");

        layout_images_film = findViewById(R.id.layout_film);
        model.addFromDbToLayout("Utente/" + currentUser.getEmail() + "/tv", "image" , layout_images_film, "tv");
        layout_images_book = findViewById(R.id.layout_images_libri);
        model.addFromDbToLayout("Utente/" + currentUser.getEmail() + "/book", "image" , layout_images_book, "book");

        model.getColor("Utente/" + currentUser.getEmail() + "/colors",  "tv");
        model.getColor("Utente/" + currentUser.getEmail() + "/colors",  "game");
        model.getColor("Utente/" + currentUser.getEmail() + "/colors",  "book");



        Button button_rimuovi = findViewById(R.id.button_rimuovi);
        button_rimuovi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(v);
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_rimuovi:
                model.showAlertDialog();
                break;
        }
    }

    public void showAlertDialog(View v) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("RIMUOVI INTERESSE");
        alert.setMessage("Vuoi davvero rimuovere gli interessi selezionati?");
        alert.setPositiveButton("SI'", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                model.delete(layout_images_film,"Utente/" + currentUser.getEmail() + "/tv" );
                model.delete(layout_images_videogames,"Utente/" + currentUser.getEmail() + "/game" );
                model.delete(layout_images_book, "Utente/" + currentUser.getEmail() + "/book");
                Toast.makeText(ActivityVisualizzaInteressi.this, "Interessi eliminati!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (i = 0; i <  layout_images_videogames.getChildCount(); i++) {
                    ImageView w =  (ImageView)  layout_images_videogames.getChildAt(i);
                    if (w.getAlpha() == 0.5F) {
                        w.setAlpha(1F);
                    }
                }
                for (i = 0; i <  layout_images_film.getChildCount(); i++) {
                    ImageView w =  (ImageView)  layout_images_film.getChildAt(i);
                    if (w.getAlpha() == 0.5F) {
                        w.setAlpha(1F);
                    }
                }
                for (i = 0; i <  layout_images_book.getChildCount(); i++) {
                    ImageView w =  (ImageView)  layout_images_book.getChildAt(i);
                    if (w.getAlpha() == 0.5F) {
                        w.setAlpha(1F);
                    }
                }
            }
        });
        alert.create().show();
    }
}