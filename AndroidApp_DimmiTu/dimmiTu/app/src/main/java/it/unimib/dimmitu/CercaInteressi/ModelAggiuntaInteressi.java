package it.unimib.dimmitu.CercaInteressi;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ModelAggiuntaInteressi implements SearchView.OnQueryTextListener {

    ArrayList<Item> itemList;
    SearchView searchItem;
    LinearLayout interestsLayout;
    Context context;
    ItemListAdapter adapter;
    CheckBox gameCheck, bookCheck, tvCheck;
    View view;
    ItemID gameID;
    ItemID tvID;
    ArrayList<Item> lista_reset;
    FirebaseFirestore db;

    boolean query_changed;

    public ModelAggiuntaInteressi(SearchView searchItem, LinearLayout interestsLayout, CheckBox gameCheck, CheckBox tvCheck, CheckBox bookCheck, View view){
        this.gameID = new ItemID();
        this.tvID = new ItemID();
        this.itemList = new ArrayList<Item>();
        this.itemList = new ArrayList<Item>();
        this.searchItem = searchItem;
        this.searchItem.setOnQueryTextListener(this);
        this.interestsLayout = interestsLayout;
        this.context = interestsLayout.getContext();
        this.gameCheck = gameCheck;
        this.tvCheck = tvCheck;
        this.bookCheck = bookCheck;
        this.view = view;
        this.adapter = new ItemListAdapter(context, itemList, interestsLayout);
        this.lista_reset = new ArrayList<Item>();
        this.query_changed = false;
    }

    public ModelAggiuntaInteressi(){};


    public void database(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("Utente/" + currentUser.getEmail() + "/haicliccato")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().getDocuments().isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                String query = (String) document.get("chiave");

                                searchItem.setQuery(query, false);
                            }
                        }
                    }
                });

        db = FirebaseFirestore.getInstance();
        db.collection("Utente/" + currentUser.getEmail() + "/haicliccato")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                    }
                });


        searchItem.setIconified(false);
    }

    public void fillListGamesHavindID(String url) throws InterruptedException {
        synchronized (gameID){
            while(gameID.getItem_id().isEmpty())
                gameID.wait();

            for(int i=0; i<gameID.getItem_id().size(); i++) {
                fillListGames(url + "" + gameID.getItem_id().get(i));
            }

        }
    }

    public void fillListGamesWithID(String url, String word){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getString("title").toLowerCase();
                        String itemID = obj.getString("id");

                        if(title.contains(word) && !gameID.getItem_id().contains(itemID)) {
                            gameID.getItem_id().add(itemID);
                        }
                    }

                    synchronized (gameID) {
                        gameID.notifyAll();
                    }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void fillListGames(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String imageUrlItem = response.getString("thumbnail");
                    String nameItem = response.getString("title");
                    String itemGenre = response.getString("genre");
                    String itemDeveloper = response.getString("developer");
                    String itemReleaseDate = response.getString("release_date");
                    String itemDescription = response.getString("description");
                    String itemPage = response.getString("game_url");
                    String itemPlatform = response.getString("platform");

                    JSONArray itemScreenshots_json = response.getJSONArray("screenshots");
                    List<String> screenshots = new ArrayList<String>();

                    for(int j=0; j<itemScreenshots_json.length(); j++){
                        JSONObject image_obj = itemScreenshots_json.getJSONObject(j);
                        screenshots.add(image_obj.getString("image"));

                    }
                    Item item = new Item("game", imageUrlItem, nameItem, "#FFC107", itemGenre, itemDescription, itemReleaseDate);
                    item.setDeveloper(itemDeveloper);
                    item.setGamePage(itemPage);
                    item.setScreenshots(screenshots);
                    item.setPlatform(itemPlatform);

                    if(!itemList.contains(item)) {
                        lista_reset.add(item);

                        if(gameCheck.isChecked()) {
                            itemList.add(item);

                            interestsLayout.removeView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                            interestsLayout.addView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                        }
                    }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void fillListTvHavingID(String url) throws InterruptedException {
        synchronized (tvID){
            while(tvID.getItem_id().isEmpty())
                tvID.wait();

            for(int i=0; i<tvID.getItem_id().size(); i++)
                fillListWithTV(url + "" + tvID.getItem_id().get(i));

        }
    }

    public void fillListWithTvID(String url, String word){

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + word, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list_obj = response.getJSONArray("results");

                    for (int i = 0; i < list_obj.length(); i++) {
                        JSONObject obj = list_obj.getJSONObject(i);
                        String title = obj.getString("title").toLowerCase();
                        String itemID = obj.getString("id");

                        if(title.contains(word) && !tvID.getItem_id().contains(itemID)) {
                            tvID.getItem_id().add(itemID);
                        }
                    }

                    synchronized (tvID){
                        tvID.notifyAll();
                    }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void fillListWithTV(String url){

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imageUrlItem = response.getString("image");
                    String itemName = response.getString("title");
                    String itemGenre = response.getString("genres");
                    String itemDescription = response.getString("plot");
                    String itemReleaseDate = response.getString("releaseDate");
                    String itemRating = response.getString("imDbRating");
                    String itemDurata = response.getString("runtimeStr");
                    String itemAwards = response.getString("awards");
                    String itemDirectors = response.getString("directors");
                    String itemWriters = response.getString("writers");


                    JSONArray itemActors_json = response.getJSONArray("actorList");
                    List<String> actors = new ArrayList<String>();
                    List<String> actors_images = new ArrayList<String>();
                    List<String> personaggio_recitato = new ArrayList<String>();

                    for(int j=0; j<itemActors_json.length(); j++){
                        JSONObject actors_obj = itemActors_json.getJSONObject(j);
                        actors.add(actors_obj.getString("name"));
                        actors_images.add(actors_obj.getString("image"));

                        String word = actors_obj.getString("asCharacter");

                        if(word.contains("as")){
                            for(int i=0; i<word.length(); i++){
                                if(i-1 >= 0 && i+2 < word.length()) {
                                    if (word.charAt(i) == 'a' && word.charAt(i + 1) == 's' && word.charAt(i + 2) == ' '){
                                        if(word.charAt(i-1) == '(')
                                            word = word.substring(0, i-1);
                                        else
                                            word = word.substring(0, i);
                                        break;
                                    }
                                }
                            }
                        }

                        personaggio_recitato.add(word);
                    }

                    Item item = new Item("tv", imageUrlItem, itemName, "#FF0000", itemGenre, itemDescription, itemReleaseDate);
                    item.setRating(itemRating);
                    item.setDurata(itemDurata);
                    item.setAwards(itemAwards);
                    item.setDirectors(itemDirectors);
                    item.setWriters(itemWriters);
                    item.setActors_name(actors);
                    item.setActors_images(actors_images);
                    item.setPersonaggio_recitato(personaggio_recitato);

                    if(!itemList.contains(item)) {
                        lista_reset.add(item);

                        if(tvCheck.isChecked()) {
                            itemList.add(item);
                            interestsLayout.removeView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                            interestsLayout.addView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                        }
                    }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void fillListWithBooks(String url, String type_of_list, String api_key, String word){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + type_of_list + "&api-key=" + api_key
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject results = response.getJSONObject("results");
                    JSONArray books = results.getJSONArray("books");

                    for (int i = 0; i < books.length(); i++) {
                        JSONObject obj = books.getJSONObject(i);
                        String itemTitle = obj.getString("title").toLowerCase();

                        if(itemTitle.contains(word)) {

                            String itemRank = obj.getString("rank");
                            String itemPublisher = obj.getString("publisher");
                            String itemDescription = obj.getString("description");
                            String itemAuthor = obj.getString("author");
                            String itemImage = obj.getString("book_image");
                            String itemPage = obj.getString("amazon_product_url");

                            String genere = "";

                            switch (type_of_list) {
                                case "Manga.json?":
                                    genere = "Manga";
                                    break;

                                case "Relationships.json?":
                                    genere = "Relationship";
                                    break;

                                case "Business Books.json?":
                                    genere = "Business";
                                    break;

                                case "Science.json?":
                                    genere = "Science";
                                    break;

                                case "Childrens Middle Grade.json?":
                                    genere = "Childrens";
                                    break;
                            }

                            Item item = new Item("book", itemImage, itemTitle, "#4CAF50", genere, itemDescription);
                            item.setAuthor(itemAuthor);
                            item.setRank(itemRank);
                            item.setPublisher(itemPublisher);
                            item.setGamePage(itemPage);


                            if(!itemList.contains(item)) {
                                lista_reset.add(item);

                                if(bookCheck.isChecked()) {
                                    itemList.add(item);

                                    interestsLayout.removeView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                                    interestsLayout.addView(adapter.getView(itemList.size() - 1, null, interestsLayout));
                                }
                            }
                        }
                    }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.length() != 0){
            query_changed = false;
            lista_reset.clear();
            itemList.clear();
            gameID.getItem_id().clear();
            tvID.getItem_id().clear();
            clearLayout();

            fillWithInterestsGames(query.toLowerCase());
            fillWithInterestsBooks(query.toLowerCase());
            fillWithInterestsTv(query.toLowerCase());
            checkBox();
        }

        return false;
    }

    public void clearLayout(){
        interestsLayout.removeAllViews();

        synchronized (interestsLayout) {
            interestsLayout.notifyAll();
        }
    }

    @Override
    public boolean onQueryTextChange(String s) {

        if(s.length() == 0){
            //Hide VirtualKeyboard
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            searchItem.setIconified(true);
            interestsLayout.removeAllViews();
        }
        else {
            query_changed = true;
            interestsLayout.removeAllViews();
        }

        return  false;
    }

    public void checkBox(){

        gameCheck.setOnClickListener(v -> {
            if(!tvCheck.isChecked() && !bookCheck.isChecked()) {
                gameCheck.setChecked(true);

            }
            else
                checkOthersCheckbox();
        });

        tvCheck.setOnClickListener(v -> {
            if(!bookCheck.isChecked() && !gameCheck.isChecked()) {
                tvCheck.setChecked(true);

            }
            else
                checkOthersCheckbox();
        });

        bookCheck.setOnClickListener(v -> {
            if(!tvCheck.isChecked() && !gameCheck.isChecked()) {
                bookCheck.setChecked(true);

            }
            else
                checkOthersCheckbox();
        });

    }

    public void checkOthersCheckbox(){
        String type = "";

        if(gameCheck.isChecked())
            type += "game";

        if(tvCheck.isChecked())
            type += "tv";

        if(bookCheck.isChecked())
            type += "book";


        if(searchItem.getQuery().toString().length() != 0 && !query_changed)
            filterListAccordingToCheckbox(type);

    }

    public void filterListAccordingToCheckbox(String type){

        itemList.clear();

        for(int i=0; i < lista_reset.size(); i++){
            if(type.toLowerCase().contains(lista_reset.get(i).getType().toLowerCase()))
                itemList.add(lista_reset.get(i));
        }

        interestsLayout.removeAllViews();

        for(int j=0; j<itemList.size(); j++) {
            interestsLayout.addView(adapter.getView(j, null, interestsLayout));
        }

    }

    public void fillWithInterestsGames(String word){
        String url_games = "https://www.freetogame.com/api/games"; //get game name
        String url_games_details = "https://www.freetogame.com/api/game?id="; //get game details

        Runnable runnable_game1 = new Runnable() {
            @Override
            public void run() {
                fillListGamesWithID(url_games, word);
            }
        };

        Runnable runnable_game2 = new Runnable() {
            @Override
            public void run() {
                try {
                    fillListGamesHavindID(url_games_details);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread_game1 = new Thread(runnable_game1);
        Thread thread_game2 = new Thread(runnable_game2);
        thread_game1.start();
        thread_game2.start();

    }

    public void fillWithInterestsTv(String word){
        String url_film = "https://imdb-api.com/en/API/SearchTitle//"; //get top 250 movies
        String url_film_details = "https://imdb-api.com/en/API/Title//";

        //Start Threads for fill Tv lists
        Runnable runnable_tv1 = new Runnable() {
            @Override
            public void run() {
                fillListWithTvID(url_film, word); //fill list with games
            }
        };

        Runnable runnable_tv2 = new Runnable() {
            @Override
            public void run() {
                try {
                    fillListTvHavingID(url_film_details); //fill list with games
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread_tv1 = new Thread(runnable_tv1);
        Thread thread_tv2 = new Thread(runnable_tv2);
        thread_tv1.start();
        thread_tv2.start();
    }

    public void fillWithInterestsBooks(String word){
        String url_books = "https://api.nytimes.com/svc/books/v3/lists/current/";
        String api_key = "";

        itemList.clear();
        interestsLayout.removeAllViews();

        fillListWithBooks(url_books, "Manga.json?", api_key, word);
        fillListWithBooks(url_books, "Relationships.json?", api_key, word);
        fillListWithBooks(url_books, "Business Books.json?", api_key, word);

    }


}
