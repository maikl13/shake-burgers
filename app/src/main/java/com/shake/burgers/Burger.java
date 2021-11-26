package com.shake.burgers;

public class Burger {
    String name,details;
    int icon,price;
    boolean fav;

    public Burger(String name, String details, int icon, int price) {
        this.name = name;
        this.details = details;
        this.icon = icon;
        this.price = price;
        this.fav = false;
    }
}
