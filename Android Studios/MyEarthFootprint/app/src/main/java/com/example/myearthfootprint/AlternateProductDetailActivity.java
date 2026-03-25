package com.example.myearthfootprint;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

public class AlternateProductDetailActivity extends AppCompatActivity {
    private int currentUserID;
    private int alternateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_product_detail);

        // 1) Retrieve intent extras
        currentUserID  = getIntent().getIntExtra("userID", -1);
        alternateID    = getIntent().getIntExtra("alternateID", -1);
        String name    = getIntent().getStringExtra("productName");

        // 2) Toolbar setup
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(name);
        }

        // 3) Bind views
        TextView    tvTitle     = findViewById(R.id.tvProductTitle);
        ImageView   imgDetail   = findViewById(R.id.imgProductDetail);
        TextView    tvBio       = findViewById(R.id.tvBiodegradable);
        TextView    tvG         = findViewById(R.id.tvGhG);
        TextView    tvW         = findViewById(R.id.tvWater);
        TextView    tvH         = findViewById(R.id.tvHumanHours);
        TextView    tvM         = findViewById(R.id.tvMachineHours);
        Button      btnAddImp   = findViewById(R.id.btnAddImpact);

        // 4) Set the product title
        tvTitle.setText(name);

        // 5) Fetch and display this alternate’s details (including its image)
        ApiManager.getAlternateProducts(new ApiManager.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray arr) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    if (o != null && o.optInt("Alternate_Product_ID") == alternateID) {
                        // pull out all fields
                        String    imgPath   = o.optString("Alternate_Product_Image");
                        String    bio       = o.optString("Alternate_Biodegradable");
                        double    ghg       = o.optDouble("Alternate_GreenHouseGas", 0);
                        double    water     = o.optDouble("Alternate_WaterUse",      0);
                        String    humanH    = o.optString("Alternate_HumanHours");
                        String    machineH  = o.optString("Alternate_MachineHours");

                        // resolve drawable resource ID (if any)
                        final int imageRes = getResources()
                                .getIdentifier(imgPath, "drawable", getPackageName());

                        runOnUiThread(() -> {
                            // set image if found
                            if (imageRes != 0) {
                                imgDetail.setImageResource(imageRes);
                            }

                            tvBio.setText("Biodegradable: " + bio);
                            tvG  .setText("GHG Emissions: "   + String.format("%.2f kg", ghg));
                            tvW  .setText("Water Use: "       + String.format("%.2f L", water));
                            tvH  .setText("Human Hours: "     + humanH);
                            tvM  .setText("Machine Hours: "   + machineH);
                        });

                        // wire up Add-Impact button
                        btnAddImp.setOnClickListener(v -> {
                            ImpactManager.addItem(name, ghg, water);
                            ApiManager.addImpact(currentUserID, ghg, water);
                            Toast.makeText(
                                    AlternateProductDetailActivity.this,
                                    "Added to impact",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                        break;
                    }
                }
            }

            @Override
            public void onError(String err) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            AlternateProductDetailActivity.this,
                            "Error loading alternate details",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
