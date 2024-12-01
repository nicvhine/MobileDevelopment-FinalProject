package com.example.wheretogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private List<Cafe> favoriteCafes;
    private Context context;

    public FavoritesAdapter(Context context, List<Cafe> favoriteCafes) {
        this.context = context;
        this.favoriteCafes = favoriteCafes;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_item_cafe, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Cafe cafe = favoriteCafes.get(position);

        holder.cafeName.setText(cafe.getName());
        holder.cafeLocation.setText(cafe.getLocation());
        holder.cafeDescription.setText(cafe.getDescription());

        String base64Image = cafe.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.cafeImage.setImageBitmap(decodedImage);
        }

        holder.favoriteIcon.setOnClickListener(v -> {
            favoriteCafes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteCafes.size());
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            updateFavorites();
        });
    }

    @Override
    public int getItemCount() {
        return favoriteCafes.size();
    }

    private void updateFavorites() {
        SharedPreferences prefs = context.getSharedPreferences("UserFavorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteCafes);
        editor.putString("favorites", json);
        editor.apply();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView cafeName, cafeLocation, cafeDescription;
        ImageView cafeImage, favoriteIcon;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafeName);
            cafeLocation = itemView.findViewById(R.id.cafeLocation);
            cafeDescription = itemView.findViewById(R.id.description);
            cafeImage = itemView.findViewById(R.id.cafeImage);
            favoriteIcon = itemView.findViewById(R.id.favoriteicon);
        }
    }
}
