package com.example.myearthfootprint;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.List;

public class CurrentImpactActivity extends AppCompatActivity {
    private static final double THRESHOLD_GHG   = 50.0;   // kg CO₂e
    private static final double THRESHOLD_WATER = 1000.0; // L

    private TextView tvTotalGhg, tvTotalWater, tvImpactList;
    private Button btnClearImpact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_impact);

        // Toolbar setup
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Current Impact");
        }

        // Bind views
        tvTotalGhg     = findViewById(R.id.tvTotalGhg);
        tvTotalWater   = findViewById(R.id.tvTotalWater);
        tvImpactList   = findViewById(R.id.tvImpactList);
        btnClearImpact = findViewById(R.id.btnClearImpact);

        // Initial display
        updateImpactDisplay();

        // Clear button handler
        btnClearImpact.setOnClickListener(v -> {
            ImpactManager.clearItems();
            updateImpactDisplay();
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

    private void updateImpactDisplay() {
        double totalGhg   = ImpactManager.getTotalGhg();
        double totalWater = ImpactManager.getTotalWater();
        List<String> names   = ImpactManager.getNames();
        List<Double> ghgs    = ImpactManager.getGhgs();
        List<Double> waters  = ImpactManager.getWaters();

        // Display totals with units
        tvTotalGhg.setText(String.format("Total GHG: %.2f kg", totalGhg));
        tvTotalWater.setText(String.format("Total Water: %.2f L", totalWater));

        // Build item list
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            sb.append(names.get(i))
                    .append(" (GHG: ")
                    .append(String.format("%.2f kg", ghgs.get(i)))
                    .append(", Water: ")
                    .append(String.format("%.2f L", waters.get(i)))
                    .append(")\n");
        }
        tvImpactList.setText(sb.toString().trim());

        // Success alert if both metrics under thresholds
        if (totalGhg < THRESHOLD_GHG && totalWater < THRESHOLD_WATER) {
            showSuccessAlert(
                    "Great Job!",
                    String.format(
                            "Your total GHG (%.2f kg) and Water use (%.2f L) are below thresholds. You have made the world a better place!",
                            totalGhg, totalWater
                    )
            );
        }

        // Threshold alerts
        if (totalGhg > THRESHOLD_GHG) {
            showThresholdAlert(
                    "GHG Threshold Exceeded",
                    String.format("Your total GHG (%.2f kg) exceeds %.1f kg.", totalGhg, THRESHOLD_GHG)
            );
        }
        if (totalWater > THRESHOLD_WATER) {
            showThresholdAlert(
                    "Water Threshold Exceeded",
                    String.format("Your total Water (%.2f L) exceeds %.1f L.", totalWater, THRESHOLD_WATER)
            );
        }
    }

    private void showSuccessAlert(String title, String message) {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"))
                .setMessage(Html.fromHtml("<font color=\"#FFFFFF\">" + message + "</font>"))
                .setPositiveButton("OK", null)
                .create();
        dlg.show();

        // Paint entire dialog background green
        if (dlg.getWindow() != null) {
            dlg.getWindow().setBackgroundDrawable(
                    new ColorDrawable(
                            ContextCompat.getColor(this, R.color.accent_green)
                    )
            );
        }

        // Tint OK button text white
        Button ok = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        if (ok != null) {
            ok.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    private void showThresholdAlert(String title, String message) {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"))
                .setMessage(Html.fromHtml("<font color=\"#FFFFFF\">" + message + "</font>"))
                .setPositiveButton("OK", null)
                .create();
        dlg.show();

        // Paint entire dialog background red
        if (dlg.getWindow() != null) {
            dlg.getWindow().setBackgroundDrawable(
                    new ColorDrawable(
                            ContextCompat.getColor(this, R.color.error_red)
                    )
            );
        }

        // Tint OK button text white
        Button ok = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        if (ok != null) {
            ok.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }
}
