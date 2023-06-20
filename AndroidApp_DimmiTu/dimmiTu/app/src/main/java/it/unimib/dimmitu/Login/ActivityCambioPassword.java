package it.unimib.dimmitu.Login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import it.unimib.dimmitu.R;

public class ActivityCambioPassword extends AppCompatActivity {

EditText email;
Button resetPasswordButton;
ProgressBar progressBar;

FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambio_password);
        email = findViewById(R.id.inserisciMail2);
        resetPasswordButton = findViewById(R.id.resetPassword);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email2 = email.getText().toString().trim();

        if (email2.isEmpty()){
            email.setError("La mail è richiesta!");
            email.requestFocus();
            return;
       }

        if(!Patterns.EMAIL_ADDRESS.matcher(email2).matches()){
            email.setError("Per piacere inserisci una mail");
            email.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText( ActivityCambioPassword.this, "Controlla la tua mail!" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText( ActivityCambioPassword.this, "Riprova! Qualcosa è andato storto." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
