package com.example.wheretogo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;  // Correct import for Android's Base64

import java.util.List;

public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder> {
    private List<Cafe> cafes;
    private Context context;

    public CafeAdapter(Context context, List<Cafe> cafes) {
        this.context = context;
        this.cafes = cafes;
    }

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_cafe, parent, false);
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        Cafe cafe = cafes.get(position);
        holder.cafeName.setText(cafe.getName());
        holder.cafeLocation.setText(cafe.getLocation());
        holder.cafeDescription.setText(cafe.getDescription());

        String base64Image = cafe.getImageBase64();
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        holder.cafeImage.setImageBitmap(decodedImage);
    }

    @Override
    public int getItemCount() {
        return cafes.size();
    }

    public static class CafeViewHolder extends RecyclerView.ViewHolder {
        TextView cafeName, cafeLocation, cafeDescription;
        ImageView cafeImage;

        public CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafeName);
            cafeLocation = itemView.findViewById(R.id.cafeLocation);
            cafeDescription = itemView.findViewById(R.id.description);
            cafeImage = itemView.findViewById(R.id.cafeImage);
        }
    }
}
