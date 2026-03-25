package com.example.myearthfootprint;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<JSONObject> products;
    private Context context;
    private int userID;

    public ProductAdapter(List<JSONObject> products, Context context, int userID) {
        this.products = products;
        this.context  = context;
        this.userID   = userID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int vt) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.product_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        JSONObject p = products.get(pos);
        int    id    = p.optInt("ProductID");
        String name  = p.optString("ProductName");
        String img   = p.optString("ProductImage");

        h.tvName.setText(name);
        int res = context.getResources()
                .getIdentifier(img,"drawable",context.getPackageName());
        h.img.setImageResource(res);

        h.card.setOnClickListener(v -> {
            Intent i = new Intent(context, ProductDetailActivity.class);
            i.putExtra("productID",    id);
            i.putExtra("productName",  name);
            i.putExtra("productImage", img);
            i.putExtra("userID",       userID);
            context.startActivity(i);
        });
    }

    @Override public int getItemCount() { return products.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView img;
        TextView  tvName;
        ViewHolder(View vw) {
            super(vw);
            card   = vw.findViewById(R.id.cardProduct);
            img    = vw.findViewById(R.id.imgProduct);
            tvName = vw.findViewById(R.id.tvProductName);
        }
    }
}
