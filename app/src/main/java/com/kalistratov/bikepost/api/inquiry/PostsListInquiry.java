package com.kalistratov.bikepost.api.inquiry;

import com.kalistratov.bikepost.entitys.BikePost;
import com.kalistratov.bikepost.entitys.EntityList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface PostsListInquiry.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public interface PostsListInquiry {

    @GET("api/index.php/v1/bike_posts")
    Call<EntityList<BikePost>> getPosts();
}
