package com.example.myearthfootprint;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private TextView tvWelcome;
    private int      userID;

    private static final double THRESHOLD_GHG   = 50.0;
    private static final double THRESHOLD_WATER = 1000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Clear in-memory impact for a fresh start
        ImpactManager.clearItems();

        // Grab userID
        userID = getIntent().getIntExtra("userID", -1);

        // Toolbar
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Welcome text
        tvWelcome = findViewById(R.id.tvWelcome);
        ApiManager.getUsers(new ApiManager.JsonArrayCallback() {
            @Override public void onSuccess(JSONArray users) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject u = users.optJSONObject(i);
                    if (u != null && u.optInt("userID") == userID) {
                        final String fn = u.optString("User_FName", "User");
                        runOnUiThread(() -> tvWelcome.setText("Welcome, " + fn));
                        break;
                    }
                }
            }
            @Override public void onError(String err) {
                Log.e(TAG, "getUsers failed: " + err);
                runOnUiThread(() -> Toast.makeText(
                        HomeActivity.this,
                        "Error loading user info: " + err,
                        Toast.LENGTH_LONG
                ).show());
            }
        });

        //Fun Environmental Facts
        LinearLayout llFacts = findViewById(R.id.llEnvironmentalFacts);
        String[] facts = {
                "Fun Fact: A mature tree can absorb nearly 22 kg of CO₂ per year.",
                "Fun Fact: Recycling one aluminum can saves enough energy to power a TV for 3 hours.",
                "Fun Fact: Turning off unused lights can cut your home’s energy use by up to 10%.",
                "Fun Fact: Composting kitchen scraps can reduce household waste by about 30%."
        };
        int idx = new Random().nextInt(facts.length);
        TextView tv = new TextView(this);
        tv.setText(facts[idx]);
        tv.setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        llFacts.addView(tv);

        // Category cards
        findViewById(R.id.cardFood)       .setOnClickListener(v -> openList("Food"));
        findViewById(R.id.cardElectronics).setOnClickListener(v -> openList("Electronics"));
        findViewById(R.id.cardStationary) .setOnClickListener(v -> openList("Stationary"));
        findViewById(R.id.cardToiletries) .setOnClickListener(v -> openList("Toiletries"));
        findViewById(R.id.cardClothing)   .setOnClickListener(v -> openList("Clothing"));

        // Fetch and check last-24h impact
        ApiManager.getImpactSummary(userID, new ApiManager.JsonCallback() {
            @Override public void onSuccess(JSONObject sum) {
                double tg = sum.optDouble("totalGhg", 0);
                double tw = sum.optDouble("totalWater", 0);
                Log.d(TAG, "Impact summary: GHG=" + tg + " Water=" + tw);

                // removed Success dialog – now in CurrentImpactActivity
                if (tg > THRESHOLD_GHG) {
                    showThresholdAlert("GHG Threshold Exceeded",
                            String.format("Your GHG (%.2f kg) exceeds %.1f kg.", tg, THRESHOLD_GHG));
                }
                if (tw > THRESHOLD_WATER) {
                    showThresholdAlert("Water Threshold Exceeded",
                            String.format("Your Water (%.2f L) exceeds %.1f L.", tw, THRESHOLD_WATER));
                }
            }
            @Override public void onError(String e) {
                // handle error
            }
        });
    }

    private void openList(String category) {
        Intent i = new Intent(this, ProductListActivity.class);
        i.putExtra("category", category);
        i.putExtra("userID", userID);
        startActivity(i);
    }

    private void showThresholdAlert(String title, String message) {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='white'>" + title + "</font>"))
                .setMessage(Html.fromHtml("<font color='white'>" + message + "</font>"))
                .setPositiveButton("OK", null)
                .create();
        dlg.show();
        if (dlg.getWindow() != null) {
            dlg.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.error_red))
            );
        }
        Button ok = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        if (ok != null) ok.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
