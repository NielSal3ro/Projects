package com.example.myearthfootprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<JSONObject> products = new ArrayList<>();
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        userID = getIntent().getIntExtra("userID", -1);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tb.getNavigationIcon()
                    .setTint(ContextCompat.getColor(this, R.color.beige_light));
        }

        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(products, this, userID);
        rvProducts.setAdapter(adapter);

        String cat = getIntent().getStringExtra("category");
        setTitle(cat);
        ApiManager.getProducts(cat, new ApiManager.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray arr) {
                products.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    if (o!=null) products.add(o);
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
            @Override
            public void onError(String e) {
                runOnUiThread(() ->
                        Toast.makeText(ProductListActivity.this,
                                "Error loading products", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_impact, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem it) {
        if (it.getItemId()==android.R.id.home) {
            finish(); return true;
        }
        if (it.getItemId()==R.id.action_current_impact) {
            startActivity(new Intent(this, CurrentImpactActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(it);
    }
}
