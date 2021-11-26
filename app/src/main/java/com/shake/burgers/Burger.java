package com.shake.burgers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Burger implements Serializable {
    String name,details, image,description;
    int price;
    boolean fav;


    public Burger(JSONObject jsonObject) throws JSONException {

         this.name = jsonObject.getString("name");
         this.details = jsonObject.getString("details");
         this.image = jsonObject.getString("image");
        this.description = jsonObject.getString("description");

         this.price =  Integer.parseInt(jsonObject.getString("price"));
         this.fav = false;

    }
}
