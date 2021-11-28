package com.shake.burgers.manager;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shake.burgers.BurgerViewer;
import com.shake.burgers.R;
import com.shake.burgers.libs.BaseActivity;

public class BurgerHolder extends RecyclerView.ViewHolder {

public  void bind(BaseActivity baseActivity , Burger burger){
   baseActivity.load(burger.image, icon , R.drawable.loading);
  name.setText(burger.name);
  price.setText(String.valueOf(burger.price));
  fav.setImageResource(burger.fav ? R.drawable.fav_select:R.drawable.fav_unselect);
  if(border!= null){

      border.setCardBackgroundColor(baseActivity.getResources().getColor(burger.fav ? R.color.colorPrimary:R.color.colorAccent));
name.setTextColor(itemView.getContext().getResources().getColor(burger.fav ? R.color.colorPrimary:R.color.colorPrimaryDark));
  }
  if(description!= null){
      description.setText(burger.description);
  }
    fav.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            burger.fav = !burger.fav;
            bind(baseActivity,burger);
        }
    });

  itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {


          baseActivity.startActivity(new Intent(baseActivity, BurgerViewer.class).putExtra("burger" , burger));
      }
  });
}
ImageView icon,fav;
    TextView name , price,description;
    CardView border;
    public BurgerHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.img);
        fav = itemView.findViewById(R.id.fav);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        description = itemView.findViewById(R.id.description);

        border= itemView.findViewById(R.id.border);
    }
}
