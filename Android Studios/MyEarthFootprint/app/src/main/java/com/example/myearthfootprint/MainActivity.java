package com.example.myearthfootprint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button   btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Login flow
        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,
                        "Please enter both username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ApiManager.authenticate(user, pass, new ApiManager.JsonCallback() {
                @Override
                public void onSuccess(JSONObject res) {
                    String msg = res.optString("message");
                    if (!"Authenticated".equals(msg)) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this,
                                        "Login failed: " + msg,
                                        Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    int userID = res.optInt("userID", -1);
                    if (userID < 1) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this,
                                        "Invalid user ID returned",
                                        Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    // Navigate straight to HomeActivity
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.putExtra("userID", userID);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this,
                                    "Error: " + error,
                                    Toast.LENGTH_LONG).show()
                    );
                }
            });
        });

        // Go to Register screen
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );
    }
}
