package producttracker.playlagom.shopkeeper.auth;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import producttracker.playlagom.shopkeeper.MyShopActivity;
import producttracker.playlagom.shopkeeper.R;

public class AuthLogin extends AppCompatActivity implements View.OnClickListener{

    public static boolean online = false;
    private static final String TAG = "LoginActivity";
    Button btnSignIn;
    EditText etLoginEmail, etLoginPassword;
    TextView tvSignUp;

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private int counter = 1;

    public static RelativeLayout RelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_auth_login);

        RelativeLayout =  findViewById(R.id.relativeLayout1);

        // check internet connection
        if (!isInternetOn()) {
            showSnackbar();
            Toast.makeText(this, "Please ON your internet", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "----onCreate: " + isInternetOn());

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MyShopActivity.class));
            finish();
        }

        // WHEN user not logged in THEN check if user's name is provided or not
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.shopkeeper));

        progressDialog = new ProgressDialog(this);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnSignIn.setOnClickListener((View.OnClickListener) this);
        tvSignUp.setOnClickListener((View.OnClickListener) this);
    }

    // Paste this on activity from where you need to check internet status
    public boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSignIn) {
            if(!isInternetOn()){
                showSnackbar();
            }else{
                loginUser();
            }
        }
        if (v == tvSignUp) {
            startActivity(new Intent(AuthLogin.this, AuthSignUp.class));
            finish();
        }
    }

    public void showSnackbar() {
        final Snackbar snackbar = Snackbar
                .make(RelativeLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar1 = Snackbar.make(RelativeLayout,"Retry Login Using Your Connection Again",Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void loginUser() {
        // register user
        final String email = etLoginEmail.getText().toString().trim();
        final String password = etLoginPassword.getText().toString().trim();

        // START: form validation
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Please enter a valid Email");
            etLoginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show();
            return;
        } // END: form validation

        progressDialog.setMessage("Login...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Login successful
                    // Notify friends online status
                    online = true;

                    // move to next page
                    finish();

                    // STORE auth values at db
                    databaseReference
                            .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                            .child("email").setValue(email);
                    databaseReference
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .child("password").setValue(password);

                    startActivity(new Intent(AuthLogin.this, MyShopActivity.class));
                    Toast.makeText(AuthLogin.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    if (counter > 3) {
                        finish();
                        Toast.makeText(AuthLogin.this, "Could not login. Try again later", Toast.LENGTH_LONG).show();
                    }
                    if (counter == 3) {
                        Toast.makeText(AuthLogin.this, "Invalid email or password.  Last chance", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AuthLogin.this, "Invalid email or password.  " + (3 - counter) + " chance left", Toast.LENGTH_LONG).show();
                    }
                    counter++;
                }
            }
        });
    }
}
