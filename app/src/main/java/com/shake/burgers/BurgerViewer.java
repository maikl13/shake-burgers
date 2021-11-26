package com.shake.burgers;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BurgerViewer extends AppCompatActivity {
TextView counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger_viewer);
        counter = findViewById(R.id.count);
        RecyclerView ingredients = findViewById(R.id.ingredients);

        ArrayList<Ingredient> ingredientsLise = new ArrayList<>();
        ingredientsLise.add(new Ingredient(R.drawable.i1));
        ingredientsLise.add(new Ingredient(R.drawable.i2));
        ingredientsLise.add(new Ingredient(R.drawable.i3));
        ingredientsLise.add(new Ingredient(R.drawable.i4));
        ingredientsLise.add(new Ingredient(R.drawable.i5));
        ingredientsLise.add(new Ingredient(R.drawable.i6));
        ingredients.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.ingredients_item,parent,false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         Ingredient ingredient = ingredientsLise.get(position);
                (holder.itemView.findViewById(R.id.item_background)).setBackgroundResource(ingredient.selected ? R.drawable.ingredients_selected : R.drawable.ingredients_unselected);
                // change small image tint if selected
                ImageView imageView = holder.itemView.findViewById(R.id.image);

            imageView.setImageResource(ingredient.image);
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(getResources().getColor(ingredient.selected? R.color.colorPrimary : R.color.colorPrimaryDark)));
// change selected on click

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ingredient.selected = !ingredient.selected;
                        notifyDataSetChanged();

                    }
                });
            }

            @Override
            public int getItemCount() {
                return ingredientsLise.size();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void double_order(View view) {
        setSingle(false);
    }

    public void single_order(View view) {
        setSingle(true);
    }

    void setSingle(boolean active){
        int colorGreen  = getResources().getColor(R.color.colorPrimary);
        int colorBlack  = getResources().getColor(R.color.colorPrimaryDark);


        findViewById(R.id.v1).setBackgroundResource(active ? R.drawable.patty_selected : R.drawable.patty_unselected  );

        ((TextView)findViewById(R.id.v2)).setTextColor(active ? colorGreen:colorBlack);
        ((TextView)findViewById(R.id.v3)).setTextColor(active ? colorGreen:colorBlack);
        ((TextView)findViewById(R.id.v4)).setTextColor(active ? colorGreen:colorBlack);


        findViewById(R.id.d1).setBackgroundResource(!active ? R.drawable.patty_selected : R.drawable.patty_unselected  );

        ((TextView)findViewById(R.id.d2)).setTextColor(!active ? colorGreen:colorBlack);
        ((TextView)findViewById(R.id.d3)).setTextColor(!active ? colorGreen:colorBlack);
        ((TextView)findViewById(R.id.d4)).setTextColor(!active ? colorGreen:colorBlack);


    }
int count = 1;
    public void minus(View view) {
        if(count>1){
            count--;
        }
        counter.setText(String.valueOf(count));

    }

    public void add(View view) {
count++;
        counter.setText(String.valueOf(count));
    }

    public void add_to_cart(View view) {
        Toast.makeText(this, count + " Burger Added Successfully ❤", Toast.LENGTH_SHORT).show();
        finish();
    }
}