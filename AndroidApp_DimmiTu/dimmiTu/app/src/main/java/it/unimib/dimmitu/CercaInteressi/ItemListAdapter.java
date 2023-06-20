package it.unimib.dimmitu.CercaInteressi;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import it.unimib.dimmitu.R;


public class ItemListAdapter extends ArrayAdapter<Item>{

    private ArrayList<Item> itemList, lista_filtrata;
    LinearLayout interestsLayout;
    private List<Integer> clicked;
    private HorizontalScrollView horizontal1, horizontal2, horizontal3;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String coloreFilm, coloreGioco, coloreLibro;

    boolean onDatabase;

    public ItemListAdapter(Context context, ArrayList<Item> itemList, LinearLayout interestsLayout){
        super(context, 0, itemList);
        this.itemList = itemList;
        lista_filtrata = new ArrayList<Item>();
        this.interestsLayout = interestsLayout;
        this.clicked = new ArrayList<Integer>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void setItemList(ArrayList<Item> itemList){
        this.itemList.clear();
        this.itemList.addAll(itemList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position == getCount() - 1){
            Toast toast = Toast.makeText(getContext(), "ho finito di caricare", Toast.LENGTH_SHORT);
            toast.show();
        }


        View currentItemView = convertView;

        if (currentItemView == null)
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);

        Item itemAtPosition = getItem(position);

        ImageView itemImage = currentItemView.findViewById(R.id.itemImage);

            Picasso
                    .get()
                    .load(itemAtPosition.getItemImage())
                    .resize(500

                            , 400)
                    .into(itemImage);

        Button detailsBTN = currentItemView.findViewById(R.id.dettagliBTN);

        TextView itemName = currentItemView.findViewById(R.id.itemName);
        itemName.setText(itemAtPosition.getItemName());

        Button saveInterest = currentItemView.findViewById(R.id.aggiuntaInteresseBTN);

        //check if enable button
        itemOnDatabase(currentUser.getEmail(), itemAtPosition.getType(), itemAtPosition.getItemName(), saveInterest);

        TextView itemGenre = currentItemView.findViewById(R.id.itemGenre);
        itemGenre.setText(itemAtPosition.getGenre());

        detailsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemAtPosition.getType().equalsIgnoreCase("game"))
                    openGameDetails(position, itemAtPosition, detailsBTN);
                else if(itemAtPosition.getType().equalsIgnoreCase("tv"))
                    openTvDetails(position, itemAtPosition, detailsBTN);
                else
                    openBookDetails(position, itemAtPosition, detailsBTN);
            }
        });

        //Save Data to Database
        saveInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAtPosition.saveItemToDatabase(currentUser.getEmail());
                saveInterest.setVisibility(View.GONE);
            }
        });


        //Change cardView color
        getColor(mAuth.getCurrentUser().getEmail(), currentItemView, itemAtPosition);

        return currentItemView;

    }

    public void getColor(String email, View v, Item item){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Utente/" + email + "/colors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                coloreFilm =  document.getString("coloreFilm");
                                coloreGioco =  document.getString("coloreVideogiochi");
                                coloreLibro =  document.getString("coloreLibri");

                                CardView card = v.findViewById(R.id.card);
                                switch(item.getType()){
                                    case "game":
                                        card.setCardBackgroundColor(Color.parseColor(coloreGioco));
                                        LinearLayout layout = v.findViewById(R.id.layoutDetailsGames);
                                        layout.setBackgroundColor(Color.parseColor(coloreGioco));
                                        break;

                                    case "tv":
                                        card.setCardBackgroundColor(Color.parseColor(coloreFilm));
                                        LinearLayout layout2 = v.findViewById(R.id.layoutDetailsTv);
                                        layout2.setBackgroundColor(Color.parseColor(coloreFilm));
                                        break;

                                    case "book":
                                        card.setCardBackgroundColor(Color.parseColor(coloreLibro));
                                        LinearLayout layout3= v.findViewById(R.id.layoutDetailsBooks);
                                        layout3.setBackgroundColor(Color.parseColor(coloreLibro));
                                        break;
                                }
                            }
                        } else {
                            Log.wtf("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void itemOnDatabase(String email, String type, String itemName, Button btn){

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("Utente/" + email + "/" + type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if (document.get("name").toString().equals(itemName))
                            btn.setVisibility(View.GONE);
                    }
                }
                else
                    Log.wtf("test", "Error getting documents: ", task.getException());
            }
        });

    }

    public void openGameDetails(int index, Item itemAtPosition, Button button){

        View v = interestsLayout.getChildAt(index);
        LinearLayout layout = v.findViewById(R.id.layoutDetailsGames);

        if(!clicked.contains(index)) {
            clicked.add(index);

            button.setText("Chiudi");
            layout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layout.requestLayout();

            //Add Image to LinearLayout
            LinearLayout layout_images = v.findViewById(R.id.layout_images);

            for (int i = 0; i < itemAtPosition.getScreenshoots().size(); i++) {
                ImageView image = new ImageView(layout_images.getContext());
                Picasso
                        .get()
                        .load(itemAtPosition.getScreenshoots().get(i))
                        .resize(800, 500)
                        .into(image);

                layout_images.addView(image);
            }

            //Add further details
            TextView textDeveloper = v.findViewById(R.id.textDeveloper);
            textDeveloper.setText(itemAtPosition.getDeveloper());

            TextView textDate = v.findViewById(R.id.textDateGame);
            textDate.setText(itemAtPosition.getRelease_date());

            TextView textPlot = v.findViewById(R.id.textPlotGame);
            textPlot.setText(itemAtPosition.getPlot());

            TextView textPage = v.findViewById(R.id.textPage);
            textPage.setText(itemAtPosition.getGamePage());

            TextView textPlatforms = v.findViewById(R.id.textPlatforms);
            textPlatforms.setText(itemAtPosition.getPlatform());

        }
        else{
            button.setText("Dettagli");
            layout.getLayoutParams().height = 0;
            layout.requestLayout();

            for(int i=0; i<clicked.size(); i++) {
                if (clicked.get(i) == index)
                    clicked.remove(i);
            }
        }
    }

    public void openTvDetails(int index, Item itemAtPosition, Button button){

        View v = interestsLayout.getChildAt(index);
        LinearLayout layout = v.findViewById(R.id.layoutDetailsTv);

        if(!clicked.contains(index)) {
            clicked.add(index);

            button.setText("Chiudi");
            layout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layout.requestLayout();

            //Add Image to RelativeLayout
            LinearLayout layout_images = v.findViewById(R.id.layout_images_actors);

            for (int i = 0; i < itemAtPosition.getActors_images().size(); i++) {

                ImageView image = new ImageView(layout_images.getContext());

                Picasso
                        .get()
                        .load(itemAtPosition.getActors_images().get(i))
                        .resize(500, 700)
                        .into(image);

                layout_images.addView(image);
            }

            //Add TextView to RelativeLayout
            RelativeLayout layout_attori = v.findViewById(R.id.layout_attori);
            int margin_left = 0;

            for (int i = 0; i < itemAtPosition.getActors_name().size(); i++) {
                TextView name = new TextView(layout_attori.getContext());

                if(i!=0){
                    margin_left += 500;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(margin_left, 0, 0, 0);
                    name.setLayoutParams(params);
                }

                name.setText(itemAtPosition.getActors_name().get(i));
                layout_attori.addView(name);
            }

            //Add TextView to RelativeLayout
            RelativeLayout layout_personaggi = v.findViewById(R.id.layout_personaggi);
            int margin_left2 = 0;

            for (int i = 0; i < itemAtPosition.getPersonaggio_recitato().size(); i++) {
                TextView name = new TextView(layout_personaggi.getContext());

                if(i!=0){
                    margin_left2 += 500;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(margin_left2, 0, 0, 0);
                    name.setLayoutParams(params);
                }

                name.setText(itemAtPosition.getPersonaggio_recitato().get(i));
                name.setMaxWidth(400);
                layout_personaggi.addView(name);
            }

            //Per fixare problema finale nello scroll
            TextView name = new TextView(layout_attori.getContext());
            name.setTextColor(Color.WHITE);
            margin_left += 500;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin_left, 0, 0, 0);
            name.setLayoutParams(params);
            layout_attori.addView(name);

            //Per fixare problema finale nello scroll
            TextView name2 = new TextView(layout_personaggi.getContext());
            name2.setTextColor(Color.WHITE);
            margin_left2 += 500;
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params2.setMargins(margin_left2, 0, 0, 0);
            name2.setLayoutParams(params2);
            layout_personaggi.addView(name2);


            //Add further details
            TextView textDate = v.findViewById(R.id.textDateTv);
            textDate.setText(itemAtPosition.getRelease_date());

            TextView textPlot = v.findViewById(R.id.textPlotTv);
            textPlot.setText(itemAtPosition.getPlot());

            TextView textRating = v.findViewById(R.id.textRating);
            textRating.setText(itemAtPosition.getRating());

            TextView textDuration = v.findViewById(R.id.textDuration);
            textDuration.setText(itemAtPosition.getDurata());

            TextView textAwards = v.findViewById(R.id.textAwards);
            textAwards.setText(itemAtPosition.getAwards());

            TextView textDirectors = v.findViewById(R.id.textDirectors);
            textDirectors.setText(itemAtPosition.getDirectors());

            TextView textWriters = v.findViewById(R.id.textWriters);
            textWriters.setText(itemAtPosition.getWriters());
        }
        else{
            button.setText("Dettagli");
            layout.getLayoutParams().height = 0;
            layout.requestLayout();

            for(int i=0; i<clicked.size(); i++) {
                if (clicked.get(i) == index)
                    clicked.remove(i);
            }
        }

        horizontal1 = v.findViewById(R.id.horizontal1);
        horizontal2 = v.findViewById(R.id.horizontal2);
        horizontal3 = v.findViewById(R.id.horizontal3);

        horizontal1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                horizontal2.scrollTo(horizontal1.getScrollX(), 0);
                horizontal3.scrollTo(horizontal1.getScrollX(), 0);
            }
        });

    }

    public void openBookDetails(int index, Item itemAtPosition, Button button){
        View v = interestsLayout.getChildAt(index);
        LinearLayout layout = v.findViewById(R.id.layoutDetailsBooks);

        if(!clicked.contains(index)) {
            clicked.add(index);

            button.setText("Chiudi");
            layout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layout.requestLayout();

            //Add further details
            TextView textRank = v.findViewById(R.id.textRank);
            textRank.setText(itemAtPosition.getRank());

            TextView textPublisher = v.findViewById(R.id.textPublisher);
            textPublisher.setText(itemAtPosition.getPublisher());

            TextView textPlot = v.findViewById(R.id.textPlot);
            textPlot.setText(itemAtPosition.getPlot());

            TextView textAuthor = v.findViewById(R.id.textAuthor);
            textAuthor.setText(itemAtPosition.getAuthor());

            TextView textPage = v.findViewById(R.id.textPageBook);
            textPage.setText(itemAtPosition.getGamePage());

        }
        else{
            button.setText("Dettagli");
            layout.getLayoutParams().height = 0;
            layout.requestLayout();

            for(int i=0; i<clicked.size(); i++) {
                if (clicked.get(i) == index)
                    clicked.remove(i);
            }
        }
    }
}
