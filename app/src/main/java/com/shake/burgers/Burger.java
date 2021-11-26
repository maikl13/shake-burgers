package com.shake.burgers;

import org.json.JSONException;
import org.json.JSONObject;

public class Burger {
    String name,details,icon;
    int price;
    boolean fav;


    public Burger(JSONObject jsonObject) throws JSONException {

         this.name = jsonObject.getString("name");
         this.details = jsonObject.getString("details");
         this.icon = jsonObject.getString("image");
         this.price =  Integer.parseInt(jsonObject.getString("price"));
         this.fav = false;

    }
}
