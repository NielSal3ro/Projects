package com.example.emberemergency;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fire Simulation Button (Placeholder)
        Button fireSimulationButton = findViewById(R.id.fireSimulationButton);
        fireSimulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FireSimulationActivity.class);
                startActivity(intent);
            }
        });

        // Resources Button
        Button resourcesButton = findViewById(R.id.resourcesButton);
        resourcesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResourcesActivity.class);
                startActivity(intent);
            }
        });
    }
}
