package com.example.wheretogo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder> {
    private List<Cafe> cafes;
    private Context context;
    private Set<Cafe> favoriteCafes = new HashSet<>();
    private boolean isAdmin;

    public CafeAdapter(Context context, List<Cafe> cafes, boolean isAdmin) {
        this.context = context;
        this.cafes = cafes;
        this.isAdmin = isAdmin;
        loadFavorites();
    }

    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isAdmin) {
            view = LayoutInflater.from(context).inflate(R.layout.admin_item_cafe, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.user_item_cafe, parent, false);
        }
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        Cafe cafe = cafes.get(position);
        holder.cafeName.setText(cafe.getName());
        holder.cafeLocation.setText(cafe.getLocation());
        holder.cafeDescription.setText(cafe.getDescription());

        String base64Image = cafe.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.cafeImage.setImageBitmap(decodedImage);
        }

        if (!isAdmin && holder.favoriteIcon != null) {
            if (favoriteCafes.contains(cafe)) {
                holder.favoriteIcon.setImageResource(R.drawable.heart_filled_red);
            } else {
                holder.favoriteIcon.setImageResource(R.drawable.outline_favorite_border_24);
            }

            holder.favoriteIcon.setOnClickListener(v -> {
                if (favoriteCafes.contains(cafe)) {
                    favoriteCafes.remove(cafe);
                    holder.favoriteIcon.setImageResource(R.drawable.outline_favorite_border_24);
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteCafes.add(cafe);
                    holder.favoriteIcon.setImageResource(R.drawable.heart_filled_red);
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
                updateFavorites();
            });
        }

        holder.informationIcon.setOnClickListener(v -> {
            showCafeInfoDialog(cafe);
        });
    }


    @Override
    public int getItemCount() {
        return cafes.size();
    }

    private void updateFavorites() {
        SharedPreferences prefs = context.getSharedPreferences("UserFavorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteCafes);
        editor.putString("favorites", json);
        editor.apply();
    }

    private void loadFavorites() {
        SharedPreferences prefs = context.getSharedPreferences("UserFavorites", Context.MODE_PRIVATE);
        String json = prefs.getString("favorites", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Set<Cafe>>() {}.getType();
            favoriteCafes = gson.fromJson(json, type);
        }
        if (favoriteCafes == null) {
            favoriteCafes = new HashSet<>();
        }
    }

    private void showCafeInfoDialog(Cafe cafe) {
        Log.d("CafeAdapter", "Phone Number in Dialog: " + cafe.getPhonenumber());

        Dialog infoDialog = new Dialog(context);
        infoDialog.setContentView(R.layout.dialog_cafe_info);

        TextView phoneNumberTextView = infoDialog.findViewById(R.id.phoneNumberText);
        TextView emailTextView = infoDialog.findViewById(R.id.emailText);

        if (cafe.getPhonenumber() != null && !cafe.getPhonenumber().isEmpty()) {
            phoneNumberTextView.setText(cafe.getPhonenumber());
        } else {
            phoneNumberTextView.setText("Phone number not available");
        }

        if (cafe.getEmail() != null && !cafe.getEmail().isEmpty()) {
            emailTextView.setText(cafe.getEmail());
        } else {
            emailTextView.setText("Email not available");
        }

        infoDialog.show();
    }



    public static class CafeViewHolder extends RecyclerView.ViewHolder {
        TextView cafeName, cafeLocation, cafeDescription;
        ImageView cafeImage, favoriteIcon, informationIcon;

        public CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafeName);
            cafeLocation = itemView.findViewById(R.id.cafeLocation);
            cafeDescription = itemView.findViewById(R.id.description);
            cafeImage = itemView.findViewById(R.id.cafeImage);

            if (itemView.findViewById(R.id.favoriteicon) != null) {
                favoriteIcon = itemView.findViewById(R.id.favoriteicon);
            }

            informationIcon = itemView.findViewById(R.id.informationicon);
        }
    }

}
