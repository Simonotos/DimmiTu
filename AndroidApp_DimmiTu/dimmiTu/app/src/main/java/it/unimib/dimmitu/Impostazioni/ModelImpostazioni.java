package it.unimib.dimmitu.Impostazioni;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.unimib.dimmitu.Home.ActivityHomePageConsigli;
import it.unimib.dimmitu.Login.ActivityCambioPassword;
import it.unimib.dimmitu.Login.ActivityLogin;
import it.unimib.dimmitu.R;

public class ModelImpostazioni implements AdapterView.OnItemSelectedListener {

    private HashMap<String, String> colori_map;
    private Context context;

    FirebaseUser currentUser;

    String[] colorNames={"Giallo","Corallo","Viola"};
    String[] hex = {"#FFDB15", "#FF5765", "#8A6FDF"};
    int colors[] = {R.drawable.giallo, R.drawable.corallo, R.drawable.viola};

    String[] colorNames2={"Giallo","Corallo","Viola"};
    String[] hex2 = {"#FFDB15", "#FF5765", "#8A6FDF"};
    int colors2[] = {R.drawable.giallo, R.drawable.corallo, R.drawable.viola};

    String[] colorNames3={"Giallo","Corallo","Viola"};
    String[] hex3 = {"#FFDB15", "#FF5765", "#8A6FDF"};
    int colors3[] = {R.drawable.giallo, R.drawable.corallo, R.drawable.viola};

    CustomAdapter customAdapter;
    CustomAdapter customAdapter2;
    CustomAdapter customAdapter3;

    public ModelImpostazioni(Context context, Spinner spin_libri, Spinner spin_videogiochi, Spinner spin_film, FirebaseUser currentUser, HashMap<String, String> colori_map){
        this.context = context;
        spin_libri.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        spin_videogiochi.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        spin_film.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        this.colori_map = colori_map;

        customAdapter = new CustomAdapter(context, colors, colorNames, spin_libri, hex, colori_map);
        customAdapter2 = new CustomAdapter(context, colors2, colorNames2, spin_videogiochi, hex2, colori_map);
        customAdapter3 = new CustomAdapter(context, colors3, colorNames3, spin_film, hex3, colori_map);
        this.currentUser = currentUser;


        spin_libri.setAdapter(customAdapter);
        spin_videogiochi.setAdapter(customAdapter2);
        spin_film.setAdapter(customAdapter3);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

            switch (arg0.getId()) {
                    case R.id.spinner_videogiochi:

                        if (colorNames2[0].equals("Giallo")) {
                            colori_map.put("coloreVideogiochi", "#FFDB15");
                        } else if (colorNames2[0].equals("Corallo")) {
                            colori_map.put("coloreVideogiochi", "#FF5765");
                        } else {
                            colori_map.put("coloreVideogiochi", "#8A6FDF");
                        }
                        break;
                    case R.id.spinner_film:
                        if (colorNames3[0].equals("Giallo")) {
                            colori_map.put("coloreFilm", "#FFDB15");
                        } else if (colorNames3[0].equals("Corallo")) {
                            colori_map.put("coloreFilm", "#FF5765");
                        } else {
                            colori_map.put("coloreFilm", "#8A6FDF");
                        }

                        break;
                    case R.id.spinner_libri:

                        if (colorNames[0].equals("Giallo")) {
                            colori_map.put("coloreLibri", "#FFDB15");
                        } else if (colorNames[0].equals("Corallo")) {
                            colori_map.put("coloreLibri", "#FF5765");
                        } else {
                            colori_map.put("coloreLibri", "#8A6FDF");
                        }
                        break;
                }
    }
}
