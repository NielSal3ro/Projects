package com.example.myearthfootprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Toolbar w/ back arrow
        Toolbar toolbar = findViewById(R.id.toolbar_reg);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText edtF = findViewById(R.id.edtFirstName);
        EditText edtL = findViewById(R.id.edtLastName);
        EditText edtU = findViewById(R.id.edtUsernameReg);
        EditText edtP = findViewById(R.id.edtPasswordReg);
        Button btnR  = findViewById(R.id.btnRegister);

        btnR.setOnClickListener(v -> {
            String fn = edtF.getText().toString().trim();
            String ln = edtL.getText().toString().trim();
            String u  = edtU.getText().toString().trim();
            String p  = edtP.getText().toString().trim();

            if (fn.isEmpty()||ln.isEmpty()||u.isEmpty()||p.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiManager.registerUser(fn, ln, u, p, new ApiManager.JsonCallback(){
                @Override
                public void onSuccess(JSONObject res) {
                    Toast.makeText(RegisterActivity.this,
                            "Registered! Please log in.", Toast.LENGTH_SHORT).show();
                    // go back to login
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                }
                @Override
                public void onError(String err) {
                    Toast.makeText(RegisterActivity.this,
                            "Error: "+err, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
