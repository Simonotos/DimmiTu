package it.unimib.dimmitu.CercaInteressi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.dimmitu.Home.ActivityHomePageConsigli;
import it.unimib.dimmitu.VisualizzaInteressi.ActivityVisualizzaInteressi;
import it.unimib.dimmitu.Impostazioni.ImpostazioniActivity;
import it.unimib.dimmitu.R;


public class HomePageActivity extends AppCompatActivity{

    SearchView searchView;
    LinearLayout interestsLayout;
    CheckBox tvCheck, gameCheck, bookCheck;
    Button logOut;
    ModelAggiuntaInteressi model;

    public HomePageActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_interests_home);

        //Fill List when Application Start + Manage Filters
        searchView = (SearchView) findViewById(R.id.searchInterests);
        interestsLayout = (LinearLayout) findViewById(R.id.layoutInterests);
        tvCheck = (CheckBox) findViewById(R.id.tvCheckID);
        gameCheck = (CheckBox) findViewById(R.id.gamesCheckID);
        bookCheck = (CheckBox) findViewById(R.id.booksCheckID);

        //All checkBox enabled
        gameCheck.setChecked(true);
        tvCheck.setChecked(true);
        bookCheck.setChecked(true);


        model = new ModelAggiuntaInteressi(searchView, interestsLayout, gameCheck, tvCheck, bookCheck, searchView.getRootView());
        model.database();


        //Manage close searchview event
        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(searchCloseButtonId);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                interestsLayout.removeAllViews();
            }
        });



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
    }

}