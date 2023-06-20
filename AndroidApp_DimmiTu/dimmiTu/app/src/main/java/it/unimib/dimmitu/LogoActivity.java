package it.unimib.dimmitu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import it.unimib.dimmitu.Login.ActivityLogin;

public class LogoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        Thread background = new Thread() {
            public void run() {
                try {
                    VideoView videoView = (VideoView) findViewById(R.id.videoView);
                    videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.scritta_finale);
                    videoView.start();

                    sleep(5*1000);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(), ActivityLogin.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();

    }


}
