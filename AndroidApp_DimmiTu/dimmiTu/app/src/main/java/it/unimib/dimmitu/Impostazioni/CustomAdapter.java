package it.unimib.dimmitu.Impostazioni;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import it.unimib.dimmitu.R;


public class CustomAdapter extends BaseAdapter {

    Context context;
    int colors[];
    String[] colorNames;
    String[] hex;
    LayoutInflater inflter;
    Spinner myself;
    HashMap<String, String> colori_map;

    public CustomAdapter(Context applicationContext, int[] colors, String[] colorNames, Spinner myself, String[] hex, HashMap<String, String> colori_map) {
        this.context = applicationContext;
        this.colors = colors;
        this.colorNames = colorNames;
        inflter = (LayoutInflater.from(applicationContext));
        this.myself = myself;
        this.hex = hex;
        this.colori_map = colori_map;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.my_dropdown_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(colorNames[i]);
        icon.setImageResource(colors[i]);


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        View finalView = view;


        database.collection("Utente/" + currentUser.getEmail() + "/colors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                QueryDocumentSnapshot colori = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                String esadecimale = "";

                                switch (myself.getTag().toString()) {
                                    case "spinner_libri":
                                        esadecimale = colori.get("coloreLibri").toString();
                                        break;

                                    case "spinner_film":
                                        esadecimale = colori.get("coloreFilm").toString();
                                        break;

                                    case "spinner_videogiochi":
                                        esadecimale = colori.get("coloreVideogiochi").toString();
                                        break;
                                }

                                int save = 0;
                                for (int j = 0; j < hex.length; j++) {
                                    if (hex[j].equals(esadecimale))
                                        save = j;
                                }

                                String nome_colore = colorNames[save];

                                if(i==0) {

                                    icon.setImageResource(colors[save]);
                                    names.setText(nome_colore);

                                    if (nome_colore.equals("Corallo")) {
                                        colorNames[1] = "Giallo";
                                        colorNames[0] = "Corallo";

                                        colors[1] = R.drawable.giallo;
                                        colors[0] = R.drawable.corallo;

                                        hex[1] = "#FFDB15";
                                        hex[0] = "#FF5765";
                                    }

                                    if (nome_colore.equals("Viola")) {
                                        colorNames[2] = "Giallo";
                                        colorNames[0] = "Viola";

                                        colors[2] = R.drawable.giallo;
                                        colors[0] = R.drawable.viola;

                                        hex[2] = "#FFDB15";
                                        hex[0] = "#8A6FDF";
                                    }

                                }
                            }
                        }
                    }
                });

        return view;
    }

}