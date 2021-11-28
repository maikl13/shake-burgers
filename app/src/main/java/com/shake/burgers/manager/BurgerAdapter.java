package com.shake.burgers.manager;

import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shake.burgers.R;
import com.shake.burgers.libs.BaseActivity;

import java.util.ArrayList;

public class BurgerAdapter extends RecyclerView.Adapter<BurgerHolder> {
    BaseActivity activity;

    ArrayList<Burger> burgersList;
    public  BurgerAdapter(BaseActivity activity){
        this.activity=activity;
        this.burgersList=new ArrayList<>();




    }

  public   String lastSearch = "";
    public void apply(String input,ArrayList<Burger> burgers){
        lastSearch = input;
        input=input.toLowerCase();
        burgersList.clear();
        for (Burger burger :burgers) {
            if(input.isEmpty()||burger.name.toLowerCase().contains(input)||burger.details.toLowerCase().contains(input)){
                burgersList.add(burger);
            }
        }

        if(!burgers.isEmpty() && burgersList.isEmpty()){
            Toast.makeText(activity, "Sorry no Products with this name ❤", Toast.LENGTH_SHORT).show();
            apply("", burgers);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BurgerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BurgerHolder(activity.getLayoutInflater().inflate(R.layout.burger_item, parent, false)) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BurgerHolder holder, int position) {
        Burger burger = burgersList.get(position);
        holder.bind(activity, burger);
    }

    @Override
    public int getItemCount() {
        return burgersList.size();
    }

}
