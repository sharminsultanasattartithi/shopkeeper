package producttracker.playlagom.shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import producttracker.playlagom.shopkeeper.auth.AuthSignUp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MainActivity", "=== try ====");
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.d("MainActivity", "=== finally ====");
                    Intent intent = new Intent(MainActivity.this, AuthSignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        thread.start();
    }

}
