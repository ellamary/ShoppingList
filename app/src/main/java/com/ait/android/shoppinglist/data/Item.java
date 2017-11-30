package com.ait.android.shoppinglist.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ellamary on 11/3/17.
 */

public class Item extends RealmObject {

    @PrimaryKey
    private String itemId;
    private String category;
    private String name;
    private String description;
    private double price;
    private boolean purchased;

    public Item() {}

    public Item(String category, String name, String description, double price, boolean purchased) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.purchased = purchased;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public String getItemId() { return itemId; }
}
