package it.unimib.dimmitu.VisualizzaInteressi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import it.unimib.dimmitu.R;

public class ModelVisualizzaInteressi {
    private static final String TAG = "errore";
    private String[] delete_image = new String[100];
    private int [] num_imm_delete = new int[100];
    private int id;
    private int position;
    FirebaseFirestore db;
    String coloreFilm;
    String coloreGioco;
    String coloreLibro;
    LinearLayout layout_images_videogames;
    LinearLayout  layout_images_film;
    LinearLayout  layout_images_book;
    FirebaseUser currentUser;
    Context context;
    CardView cardview_tv, cardview_game, cardview_books;
    private int num_elementi_da_cancellare = 0;
    HashMap<String, Integer> list_image; // contiene il link e la posizione nel layout



    public ModelVisualizzaInteressi(Context context, CardView cardview_tv, CardView cardview_game, CardView cardview_books){
        this.context = context;
        this.cardview_game = cardview_game;
        this.cardview_tv = cardview_tv;
        this.cardview_books = cardview_books;
        this.list_image = new HashMap<String, Integer>();
    }


    public void showAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("RIMUOVI INTERESSE");
        alert.setMessage("Vuoi davvero rimuovere gli interessi selezionati?");
        alert.setPositiveButton("SI'", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete(layout_images_film,"Utente/" + currentUser.getEmail() + "/tv" );
                delete(layout_images_videogames,"Utente/" + currentUser.getEmail() + "/game" );
                delete(layout_images_book, "Utente/" + currentUser.getEmail() + "/book");
                Toast.makeText(context, "Interessi eliminati!", Toast.LENGTH_SHORT).show();
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

    public void delete(LinearLayout layout, String percorso){
        int lunghezza = 0;
        int num = 0;



        Arrays.fill(delete_image, null);

        for (int i = 0; i <  layout.getChildCount() ; i++) {

            ImageView w =  (ImageView)  layout.getChildAt(i);
            if (w != null) {
                if(w.getAlpha() == 0.5F){
                    num_elementi_da_cancellare = num_elementi_da_cancellare + 1;
                    for (Map.Entry<String, Integer> entry : list_image.entrySet()) {
                        if (entry.getValue().equals(w.getId())) {
                            delete_image[lunghezza] =  (String) entry.getKey();
                            lunghezza++;
                        }
                    }
                    num_imm_delete[num] = w.getId();
                    num++;
                    w.setBackgroundResource(0);
                    w.setImageResource(0);
                    setMargins(layout.getChildAt(i), 0, 0, 0, 0);



                }


            }

        }

        if (num_elementi_da_cancellare != 0) {

            for(int i = 0; i< num_imm_delete.length; i++){
                if(layout.getChildCount() >= 0 ){
                    if(layout.getChildAt(i) != null){
                        if( layout.getChildAt(i).getId() == num_imm_delete[i]){

                            View v = layout.getChildAt(i);
                            ImageView image = new ImageView(context);

                            v.setLeft(-70);

                            layout.removeView(v);


                        }
                    }

                }
            }

            //elimino dal database le immagini

            for (String s : delete_image) {
                db = FirebaseFirestore.getInstance();
                db.collection(percorso)
                        .whereEqualTo("image", s)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                } else {
                                    Log.wtf("Problem!", "Documenti non presi ");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        }

        num_elementi_da_cancellare = 0;

    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void onTouch(LinearLayout layout){
        for (int i = 0; i <  layout.getChildCount(); i++){

            View w =  layout.getChildAt(i);
            if(w != null){
                w.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (w.getAlpha() == 0.5F) {
                            w.setAlpha(1F);
                        } else {
                            w.setAlpha(0.5F);
                        }
                        return true;
                    }
                });
            }else{
                Toast.makeText(context, "C'è qualche errore", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void addFromDbToLayout(String percorso, String image, LinearLayout layout, String tipo){
        db = FirebaseFirestore.getInstance();
        db.collection(percorso)
                .whereNotEqualTo(image, "")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ImageView image = new ImageView(layout.getContext());
                                String link = (String) document.get("image");
                                if(tipo.equals("game")){
                                    Picasso
                                            .get()
                                            .load(link)
                                            .resize(650, 450)
                                            .into(image);
                                }else{
                                    Picasso
                                            .get()
                                            .load(link)
                                            .resize(450, 550)
                                            .into(image);
                                }


                                image.setId(position);
                                id = image.getId();

                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.leftMargin =  70;
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                image.setBackgroundResource(R.drawable.margin);
                                layout.addView(image, layoutParams);

                                list_image.put(document.get("image").toString(), id);
                                position++;
                                onTouch(layout);
                            }
                        } else {
                            Log.wtf(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
                                coloreFilm =  document.getString("coloreFilm");
                                coloreGioco =  document.getString("coloreVideogiochi");
                                coloreLibro =  document.getString("coloreLibri");


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
