package com.kalistratov.bikepost.api;


import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public interface Action<T> {
    void action(final Response<T> response);
    Call<T> getCall(Retrofit retrofit);
}
