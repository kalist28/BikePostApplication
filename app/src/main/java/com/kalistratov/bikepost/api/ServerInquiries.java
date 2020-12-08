package com.kalistratov.bikepost.api;

import com.kalistratov.bikepost.entitys.BikePost;
import com.kalistratov.bikepost.entitys.EntityList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerInquiries {
    @GET("api/index.php/v1/bike_posts")
    Call<EntityList<BikePost>> getPosts();
}
