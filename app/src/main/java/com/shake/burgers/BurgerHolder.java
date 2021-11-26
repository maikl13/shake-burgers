package com.shake.burgers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shake.burgers.libs.BaseActivity;

public class BurgerHolder extends RecyclerView.ViewHolder {

void bind(BaseActivity libs , Burger burger){
   libs.load(burger.icon , icon , R.drawable.loading);
  name.setText(burger.name);
  price.setText(String.valueOf(burger.price));
  fav.setImageResource(burger.fav ? R.drawable.fav_select:R.drawable.fav_unselect);
  if(border!= null){

      border.setCardBackgroundColor(itemView.getContext().getResources().getColor(burger.fav ? R.color.colorPrimary:R.color.colorAccent));
name.setTextColor(itemView.getContext().getResources().getColor(burger.fav ? R.color.colorPrimary:R.color.colorPrimaryDark));
  }
    fav.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            burger.fav = !burger.fav;
            bind(libs,burger);
        }
    });
}
ImageView icon,fav;
    TextView name , price;
    CardView border;
    public BurgerHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.img);
        fav = itemView.findViewById(R.id.fav);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        border= itemView.findViewById(R.id.border);
    }
}
