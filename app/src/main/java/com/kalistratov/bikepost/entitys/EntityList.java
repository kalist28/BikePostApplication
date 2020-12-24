package com.kalistratov.bikepost.entitys;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntityList<C> {
    @SerializedName("data")
    public List<Entity<C>> data;
}
