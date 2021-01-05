package com.kalistratov.bikepost.api;


import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * An interface for creating and processing a get request.
 * @param <T>
 */
public interface Action<T> {
    /**
     * Action on successful connection to the server.
     * @param response - query result object.
     */
    void action(final Response<T> response);

    /**
     * Calling interface creation.
     * @param retrofit -
     * @return call object.
     */
    Call<T> getCall(Retrofit retrofit);
}
