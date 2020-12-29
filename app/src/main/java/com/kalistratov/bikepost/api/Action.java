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
     * Действие при удачном подключении к серверу.
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
