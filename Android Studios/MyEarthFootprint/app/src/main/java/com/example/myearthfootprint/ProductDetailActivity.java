package com.example.myearthfootprint;

import android.app.AlertDialog;
import android.content.Intent;
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

public class ProductDetailActivity extends AppCompatActivity {
    private int currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 1) Retrieve intent extras
        currentUserID = getIntent().getIntExtra("userID", -1);
        int productID = getIntent().getIntExtra("productID", -1);
        String name   = getIntent().getStringExtra("productName");
        String imgStr = getIntent().getStringExtra("productImage");

        // 2) Toolbar setup
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(name);
        }

        // 3) Bind views
        TextView tvTitle        = findViewById(R.id.tvProductTitle);
        ImageView img           = findViewById(R.id.imgProductDetail);
        TextView tvBio          = findViewById(R.id.tvBiodegradable);
        TextView tvG            = findViewById(R.id.tvGhG);
        TextView tvW            = findViewById(R.id.tvWater);
        TextView tvH            = findViewById(R.id.tvHumanHours);
        TextView tvM            = findViewById(R.id.tvMachineHours);
        Button  btnAdd          = findViewById(R.id.btnAddImpact);
        Button  btnViewAlt      = findViewById(R.id.btnViewAlternates);
        Button  btnBioInfo      = findViewById(R.id.btnBiodegradableInfo);
        Button  btnGhgInfo      = findViewById(R.id.btnGhGInfo);
        Button  btnWaterInfo    = findViewById(R.id.btnWaterInfo);

        // 4) Set product title
        tvTitle.setText(name);

        // 5) Load image resource
        int resId = getResources().getIdentifier(imgStr, "drawable", getPackageName());
        if (resId != 0) img.setImageResource(resId);

        // 6) Fetch and display product details
        ApiManager.getProductDetails(productID, new ApiManager.JsonCallback() {
            @Override public void onSuccess(JSONObject r) {
                double ghg     = r.optDouble("Product_GreenHouseGas", 0);
                double water   = r.optDouble("Product_WaterUse",      0);
                String bioText = r.optString("Product_Biodegradable");
                String humanText   = r.optString("Product_HumanHours");
                String machineText = r.optString("Product_MachineHours");

                // detailed fields
                String bioDetail     = r.optString("Product_Biodegradable_Detailed");
                String ghgDetail     = r.optString("Product_GreenHouseGas_Detailed");
                String waterDetail   = r.optString("Product_WaterUse_Detailed");
                String humanDetail   = r.optString("Product_HumanHours_Detailed");
                String machineDetail = r.optString("Product_MachineHours_Detailed");

                runOnUiThread(() -> {
                    // set summary fields
                    tvBio.setText("Biodegradable: " + bioText);
                    tvG  .setText("GHG Emissions: " + String.format("%.2f kg", ghg));
                    tvW  .setText("Water Use: "     + String.format("%.2f L", water));
                    tvH  .setText("Human Hours: "   + humanText);
                    tvM  .setText("Machine Hours: " + machineText);

                    // wire "More Info" dialogs
                    btnBioInfo.setOnClickListener(v ->
                            showInfoDialog("Biodegradable Details", bioDetail)
                    );
                    btnGhgInfo.setOnClickListener(v ->
                            showInfoDialog("GHG Emissions Details", ghgDetail)
                    );
                    btnWaterInfo.setOnClickListener(v ->
                            showInfoDialog("Water Use Details", waterDetail)
                    );
                    // add to impact
                    btnAdd.setOnClickListener(v -> {
                        if (currentUserID < 1) {
                            Toast.makeText(
                                    ProductDetailActivity.this,
                                    "Invalid user – please log in again",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }
                        ImpactManager.addItem(name, ghg, water);
                        Toast.makeText(
                                ProductDetailActivity.this,
                                "Added to impact",
                                Toast.LENGTH_SHORT
                        ).show();
                        ApiManager.addImpact(currentUserID, ghg, water);
                    });
                });
            }
            @Override public void onError(String e) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            ProductDetailActivity.this,
                            "Error loading product details",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                });
            }
        });

        // 7) Fetch alternates and hook up View Alternates
        ApiManager.getAlternateProducts(new ApiManager.JsonArrayCallback() {
            @Override public void onSuccess(JSONArray arr) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject alt = arr.optJSONObject(i);
                    if (alt != null && alt.optInt("ProductID") == productID) {
                        final int altID       = alt.optInt("Alternate_Product_ID");
                        final String altName  = alt.optString("Alternate_Product_Name");
                        runOnUiThread(() ->
                                btnViewAlt.setOnClickListener(v -> {
                                    Intent intent = new Intent(
                                            ProductDetailActivity.this,
                                            AlternateProductDetailActivity.class
                                    );
                                    intent.putExtra("alternateID", altID);
                                    intent.putExtra("userID", currentUserID);
                                    intent.putExtra("productName", altName);
                                    startActivity(intent);
                                })
                        );
                        break;
                    }
                }
            }
            @Override public void onError(String e) {
                // optional error handling
            }
        });
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
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
