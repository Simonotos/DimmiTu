package it.unimib.dimmitu.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.unimib.dimmitu.VisualizzaInteressi.ActivityVisualizzaInteressi;
import it.unimib.dimmitu.CercaInteressi.HomePageActivity;
import it.unimib.dimmitu.Impostazioni.ImpostazioniActivity;
import it.unimib.dimmitu.R;

public class ActivityHomePageConsigli extends AppCompatActivity {
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Context context;
    FirebaseFirestore db;
    private static final String TAG = "errore";
    private ArrayList<String> list_genres = new ArrayList<String>();
    ConstraintLayout layout;
    LinearLayout layout_film;
    LinearLayout layout_book;
    LinearLayout layout_games;
    boolean sceltaTv = false;
    boolean sceltaBook = false;
    boolean sceltaGame = false;
    CardView cardview_tv, cardview_game, cardview_books;
    TextView hai_guardato;
    HashMap<String, ImageView> lista_immagini = new HashMap<String, ImageView>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_consigli);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        layout = findViewById(R.id.layout_home);
        layout_book = findViewById(R.id.layout_images_libri);
        layout_games = findViewById(R.id.layout_videogames_home);
        layout_film = findViewById(R.id.layout_images_film);

        cardview_tv = findViewById(R.id.cardView_tv);
         cardview_books = findViewById(R.id.cardView_book);
         cardview_game = findViewById(R.id.cardView3);

       hai_guardato = findViewById(R.id.hai_guardato);



        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.home_navBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_navBar:
                        startActivity(new Intent(getApplicationContext(), ActivityHomePageConsigli.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.interests_navBar:
                        startActivity(new Intent(getApplicationContext(), ActivityVisualizzaInteressi.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings_navBar:
                        startActivity(new Intent(getApplicationContext(), ImpostazioniActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        ImageButton button = findViewById(R.id.searchInterests);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        int interesse_random_classe = new Random().nextInt(3);

        switch (interesse_random_classe){
            case 0:{
                sceltaTv = true;
                countInterest("Utente/"+ currentUser.getEmail() +"/tv", "image");
            }break;
            case 1:{
                sceltaBook = true;
                countInterest("Utente/"+ currentUser.getEmail() +"/book", "image");
            }break;
            case 2:{
                sceltaGame = true;
                countInterest("Utente/"+ currentUser.getEmail() +"/game", "image");
            }break;
        }

        getColor("Utente/" + currentUser.getEmail() + "/colors",  "tv");
        getColor("Utente/" + currentUser.getEmail() + "/colors",  "game");
        getColor("Utente/" + currentUser.getEmail() + "/colors",  "book");

    }
    public  void onTouch(LinearLayout layout){

        for (int i = 0; i <  layout.getChildCount(); i++){
            View w =  layout.getChildAt(i);
            if(w != null){
                w.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (Map.Entry<String, ImageView> entry : lista_immagini.entrySet()) {
                            if (entry.getValue().equals(w)) {


                                HashMap<String,String> keyMap = new HashMap<String, String>();
                                keyMap.put("chiave", entry.getKey());

                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                db = FirebaseFirestore.getInstance();
                                db.collection("Utente")
                                        .document(currentUser.getEmail())
                                        .collection("haicliccato")
                                        .document("chiave")
                                        .set(keyMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.wtf("ciao", "Hai inserito correttamente la chiave");
                                            }
                                        });

                                startActivity(new Intent(ActivityHomePageConsigli.this, HomePageActivity.class));
                            }
                        }
                    }
                });
            }
        }
    }
    public void countInterest(String percorso, String image){

        db = FirebaseFirestore.getInstance();
        db.collection(percorso)
                .whereNotEqualTo(image, "")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int interesse_random = 0;
                            int count = 0;
                            count = task.getResult().size();
                            if(count != 0){
                                interesse_random =(int) new Random().nextInt(count);

                                for(int i = 0; i<task.getResult().size(); i++){
                                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(i);
                                    if(i == interesse_random){

                                        hai_guardato.setText("PERCHE' TI E' PIACIUTO: " + document.get("name"));
                                        String genre = (String) document.get("genres");

                                        if (genre != null){
                                            if(document.get("actors_image") != null){

                                                int l1 = 0;

                                                for(int l = 0; l < genre.length(); l++){

                                                    char c = genre.charAt(l);
                                                    if (c == ','){

                                                        String genre_adjust = genre.substring(l1, l);
                                                        l1 = l + 1;
                                                        list_genres.add(genre_adjust.toLowerCase());

                                                    }
                                                }
                                            }else{

                                                list_genres.add(genre.toLowerCase());
                                            }

                                        }


                                        if(sceltaBook){

                                            for(int b = 0; b < list_genres.size(); b++){


                                                switch (list_genres.get(b)) {


                                                    case "combined print and e-book fiction":
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Combined Print and E-Book Fiction", "");
                                                        createInterestGames(layout_games, "https://www.freetogame.com/api/games?category=social&sort-by=release-date");
                                                        createInterestTv(layout_film, "https://imdb-api.com/API/AdvancedSearch//?genres=drama");
                                                        break;
                                                    //...

                                                }
                                            }

                                        } else if (sceltaGame){
                                            Log.wtf("azz", "hai scelto un videogioco");
                                            for(int k = 0; k < list_genres.size(); k++){
                                                switch (list_genres.get(k)){
                                                    case "mmorpg":
                                                        createInterestGames(layout_games, "https://www.freetogame.com/api/games?category=mmorpg&sort-by=release-date");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Crime and Punishment", "");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Expeditions Disasters and Adventures", "");
                                                        createInterestTv(layout_film, "https://imdb-api.com/API/AdvancedSearch//?genres=action");
                                                        break;
                                                    case "shooter":
                                                        createInterestGames(layout_games, "https://www.freetogame.com/api/games?category=shooter&sort-by=release-date");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Expeditions Disasters and Adventures", "");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Espionage", "");
                                                        createInterestTv(layout_film, "https://imdb-api.com/API/AdvancedSearch//?genres=action");
                                                        break;
                                                    //...
                                                }
                                            }


                                        }else{

                                            for(int f = 0; f < list_genres.size(); f++){


                                                switch (list_genres.get(f)) {
                                                    case "action":
                                                        createInterestTv(layout_film, "https://imdb-api.com/API/AdvancedSearch//?genres=action");
                                                        createInterestGames(layout_games, "https://www.freetogame.com/api/games?category=mmorpg&sort-by=release-date");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Combined Print Nonfiction", "");
                                                        break;
                                                    case "adventure":
                                                        createInterestTv(layout_film, "https://imdb-api.com/API/AdvancedSearch//?genres=adventure");
                                                        createInterestGames(layout_games, "https://www.freetogame.com/api/games?category=mmorpg&category=shooter&sort-by=release-date");
                                                        createInterestBook(layout_book, "https://api.nytimes.com/svc/books/v3/lists/current/", "Series Books", "");
                                                        break;

                                                        //...

                                                }

                                            }

                                        }



                                    }
                                }

                            }






                        } else {
                            Log.wtf(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }
    public void createInterestGames(LinearLayout layout, String url){

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        ImageView image = new ImageView(layout.getContext());

                        String link = (String) obj.getString("thumbnail");


                        Picasso
                                .get()
                                .load(link)
                                .resize(650, 450)
                                .into(image);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.leftMargin =  70;
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        image.setBackgroundResource(R.drawable.margin);

                        String itemName = obj.getString("title");
                        lista_immagini.put(itemName, image);


                        layout.addView(image, layoutParams);

                        if(i == 5){
                            i = response.length();
                        }

                    }

                    onTouch(layout);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }
    public void createInterestTv(LinearLayout layout, String url){

        Toast.makeText(this, "Sto Caricando", Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {


                    JSONArray list_obj = response.getJSONArray("results");

                    for (int i = 0; i < list_obj.length(); i++) {

                        JSONObject obj = list_obj.getJSONObject(i);
                        ImageView image = new ImageView(layout.getContext());

                        String link = (String) obj.getString("image");


                        Picasso
                                .get()
                                .load(link)
                                .resize(450, 550)
                                .into(image);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.leftMargin =  70;
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        image.setBackgroundResource(R.drawable.margin);

                        String itemName = obj.getString("title");
                        lista_immagini.put(itemName, image);

                        layout.addView(image, layoutParams);

                        if(i == 5){
                            i = list_obj.length();
                        }


                    }

                    onTouch(layout);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }
    public void createInterestBook(LinearLayout layout, String url, String type_of_list, String api_key){

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + type_of_list + ".json?/&api-key=" + api_key, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject results = response.getJSONObject("results");
                    JSONArray books = results.getJSONArray("books");

                    for (int i = 0; i < books.length(); i++) {
                        JSONObject obj = books.getJSONObject(i);
                        ImageView image = new ImageView(layout.getContext());
                        String link = (String) obj.getString("book_image");
                        Picasso
                                .get()
                                .load(link)
                                .resize(450, 550)
                                .into(image);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.leftMargin =  70;
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        image.setBackgroundResource(R.drawable.margin);

                        String itemName = obj.getString("title");
                        lista_immagini.put(itemName, image);

                        layout.addView(image, layoutParams);


                        if(i == 5){
                            i = books.length();
                        }

                    }

                    onTouch(layout);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("errore", "c'è qualche problema");
            }
        }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    public void getColor(String percorso, String tipo){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(percorso)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String coloreFilm =  document.getString("coloreFilm");
                                String coloreGioco =  document.getString("coloreVideogiochi");
                                String coloreLibro =  document.getString("coloreLibri");


                                switch(tipo){
                                    case "tv":
                                        cardview_tv.setCardBackgroundColor(Color.parseColor(coloreFilm));
                                        break;

                                    case "game":
                                        cardview_game.setCardBackgroundColor(Color.parseColor(coloreGioco));
                                        break;

                                    case "book":
                                        cardview_books.setCardBackgroundColor(Color.parseColor(coloreLibro));
                                        break;
                                }
                            }
                        } else {
                            Log.wtf("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}