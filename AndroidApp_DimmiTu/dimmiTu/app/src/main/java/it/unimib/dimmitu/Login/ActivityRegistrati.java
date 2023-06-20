package it.unimib.dimmitu.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import it.unimib.dimmitu.R;

public class ActivityRegistrati extends AppCompatActivity {

    Button registrazione;
    FirebaseAuth mAuth;
    EditText  inputEmail, inputPassword, inputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrati);


        mAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPassword = findViewById(R.id.editTextTextPassword);
        inputConfirmPassword = findViewById(R.id.editTextTextPassword2);

        registrazione= findViewById(R.id.creaAccount);
        registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();

            }
        });
    }

    public void createUser() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

         if (TextUtils.isEmpty(email) || !email.contains("@")) {

            inputEmail.setError("Email non può essere vuota");
            inputEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            inputPassword.setError("La password deve contenere uno dei seguenti caratteri speciali !?/- ");
            inputPassword.requestFocus();
        }else if (TextUtils.isEmpty(confirmPassword) || !confirmPassword.equals(password)){
            inputPassword.setError("Le password devono coincidere");
            inputPassword.requestFocus();
        } else {



            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityRegistrati.this, "Registrazione avvenuta con successo!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivityRegistrati.this, ActivitySceltaInteressi.class));
                    } else {
                        Toast.makeText(ActivityRegistrati.this, "Registrazione Errore" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            });

        }
    }


}
