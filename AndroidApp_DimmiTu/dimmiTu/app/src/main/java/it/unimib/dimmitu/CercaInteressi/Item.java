package it.unimib.dimmitu.CercaInteressi;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item{
    private String type;
    private String itemImage;
    private String itemName;
    private String colorBack;
    private String genre;
    private String release_date;
    private String description;

    //Specify for Games
    private String developer;
    private String platform;
    private String gamePage;
    private List<String> screenshots;

    //Specify for Tv
    private String rating;
    private List<String> actors_name;
    private List<String> actors_images;
    private List<String> personaggio_recitato;
    private String durata;
    private String awards;
    private String directors;
    private String writers;

    //Specify for Books
    private String rank;
    private String publisher;
    private String author;

    public Item(){}

    public Item(String type, String itemImage, String itemName, String colorBack, String genre, String description, String release_date) {
        this.type = type;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.colorBack = colorBack;
        this.genre = genre;
        this.description = description;
        this.release_date = release_date;
    }

    public Item(String type, String itemImage, String itemName){
        this.type = type;
        this.itemImage = itemImage;
        this.itemName = itemName;
    }

    //for Manga
    public Item(String type, String itemImage, String itemName, String colorBack, String genre, String description) {
        this.type = type;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.colorBack = colorBack;
        this.genre = genre;
        this.description = description;
    }

    public String getItemImage() {return itemImage;}

    public String getItemName() {return itemName;}

    public String getColorBack(){return this.colorBack;}

    public String getGenre(){return genre;}

    public String getType(){return this.type;}

    public void setDeveloper(String developer){this.developer = developer;}
    public String getDeveloper(){return this.developer;}

    public String getRelease_date(){return this.release_date;}

    public void setGamePage(String gamePage){this.gamePage = gamePage;}
    public String getGamePage(){return this.gamePage;}

    public String getPlot(){return this.description;}

    public void setScreenshots(List<String> screenshots) {this.screenshots = screenshots;}
    public List<String> getScreenshoots(){return this.screenshots;}

    public void setRating(String rating) {this.rating = rating;}
    public String getRating() {return rating;}

    public List<String> getActors_name() {return actors_name;}
    public void setActors_name(List<String> actors_name) {this.actors_name = actors_name;}

    public List<String> getActors_images() {return actors_images;}
    public void setActors_images(List<String> actors_images) {this.actors_images = actors_images;}

    public List<String> getPersonaggio_recitato() {return personaggio_recitato;}
    public void setPersonaggio_recitato(List<String> personaggio_recitato) {this.personaggio_recitato = personaggio_recitato;}

    public String getDurata() {return durata;}
    public void setDurata(String durata) {this.durata = durata;}

    public String getAwards() {return awards;}
    public void setAwards(String awards) {this.awards = awards;}

    public String getDirectors() {return directors;}
    public void setDirectors(String directors) {this.directors = directors;}

    public String getWriters() {return writers;}

    public void setWriters(String writers) {this.writers = writers;}

    public void setPlatform(String platform){this.platform = platform;}
    public String getPlatform(){return this.platform;}

    public String getRank() {return rank;}
    public void setRank(String rank) {this.rank = rank;}

    public String getPublisher() {return publisher;}
    public void setPublisher(String publisher) {this.publisher = publisher;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}


    public void saveItemToDatabase(String email) {
        //Database
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        Map<String, Object> item = new HashMap<>();

        item.put("name", itemName);
        item.put("image", itemImage);
        item.put("genres", genre);
        item.put("colorBack", colorBack);

        if(type != "book")
            item.put("release_date", release_date);

        item.put("description", description);

        if (type.equalsIgnoreCase("game")) {
            item.put("developer", developer);
            item.put("platform", platform);
            item.put("gamePage", gamePage);
            item.put("screenshots", screenshots);
        } else if(type.equalsIgnoreCase("tv")) {
            item.put("rating", rating);
            item.put("actors_name", actors_name);
            item.put("actors_image", actors_images);
            item.put("personaggio_recitato", personaggio_recitato);
            item.put("durata", durata);
            item.put("awards", awards);
            item.put("directors", directors);
            item.put("writers", writers);
        }
        else{
            item.put("rank", rank);
            item.put("publisher", publisher);
            item.put("author", author);
            item.put("gamePage", gamePage);
        }

        database.collection("Utente").document(email).collection(type).document(itemName)
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.wtf("bene", "item aggiunto correttamente al database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf("errore", "Error writing document", e);
                    }
                });

    }
}
