package com.kalistratov.bikepost.api;


import retrofit2.Response;

public interface Action<T> {
    void action(final Response<T> response);
}
