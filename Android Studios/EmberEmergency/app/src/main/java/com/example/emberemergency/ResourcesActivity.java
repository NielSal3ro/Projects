package com.example.emberemergency;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        // Set up the toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Resources");
        }

        // State and County Office
        CardView stateAndCountyCard = findViewById(R.id.stateAndCountyCard);
        stateAndCountyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, StateCountyActivity.class);
                startActivity(intent);
            }
        });

        // Legal
        CardView legalCard = findViewById(R.id.legalCard);
        legalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, LegalActivity.class);
                startActivity(intent);
            }
        });

        // Food
        CardView foodCard = findViewById(R.id.foodCard);
        foodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, FoodActivity.class);
                startActivity(intent);
            }
        });

        // Housing
        CardView housingCard = findViewById(R.id.housingCard);
        housingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, HousingActivity.class);
                startActivity(intent);
            }
        });

        // Other Non-Profit Organizations
        CardView otherNonProfitCard = findViewById(R.id.otherNonProfitCard);
        otherNonProfitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, OtherNonProfitActivity.class);
                startActivity(intent);
            }
        });
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
