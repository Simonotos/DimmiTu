package it.unimib.dimmitu.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.dimmitu.Home.ActivityHomePageConsigli;
import it.unimib.dimmitu.Login.ActivityCambioPassword;
import it.unimib.dimmitu.Login.ActivityRegistrati;
import it.unimib.dimmitu.Login.ActivitySceltaInteressi;
import it.unimib.dimmitu.R;

public class ActivityLogin extends AppCompatActivity {
    Button button;
    Button accedi;
    EditText emailInsert, passwordInsert;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    FirebaseAuth mAuth;
    TextView passwordDimenticata;
    private CheckBox ricordami;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        button = findViewById(R.id.button3);



        emailInsert = findViewById(R.id.editTextTextUsername);
        passwordInsert = findViewById(R.id.editTextTextPassword3);



        createRequest();

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //login e remember me per memorizzare account
        ricordami = (CheckBox)findViewById(R.id.ricordami);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailInsert.setText(loginPreferences.getString("email", ""));
            passwordInsert.setText(loginPreferences.getString("password", ""));
            ricordami.setChecked(true);
        }


        accedi = findViewById(R.id.accedi);
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == accedi) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(emailInsert.getWindowToken(), 0);

                    email = emailInsert.getText().toString();
                    password = passwordInsert.getText().toString();

                    if (ricordami.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("email", email);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }
                    loginUser();
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrati();
            }
        });

        passwordDimenticata = (TextView) findViewById(R.id.passwordDimenticata);
        passwordDimenticata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCambioPassword();
            }
        });

    }

    //login con email e password
    public void loginUser(){
        String email = emailInsert.getText().toString();
        String password = passwordInsert.getText().toString();


        if (TextUtils.isEmpty(email)) {
            emailInsert.setError("L'email non può essere vuota");
            emailInsert.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordInsert.setError("La password non può essere vuota");
            passwordInsert.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText( ActivityLogin.this, "Login avvenuto con successo!" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivityLogin.this, ActivityHomePageConsigli.class));
                    }
                    else{
                        Toast.makeText(ActivityLogin.this, "Questo utente non esiste" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    //login con google
    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (Throwable e) {

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        startActivity(new Intent(ActivityLogin.this, ActivitySceltaInteressi.class));
                    }
                });
    }




    public void openRegistrati() {
        Intent intent = new Intent(this, ActivityRegistrati.class);
        startActivity(intent);
    }

    public void openCambioPassword() {
        Intent intent = new Intent(this, ActivityCambioPassword.class);
        startActivity(intent);
    }





}