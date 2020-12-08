package com.kalistratov.bikepost.entitys;

import com.google.gson.annotations.SerializedName;

public class Entity<C> {
    @SerializedName("id")
    private int id;

    @SerializedName("type")
    private String type;

    @SerializedName("attributes")
    private C property;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public C getProperty() {
        return property;
    }
}

