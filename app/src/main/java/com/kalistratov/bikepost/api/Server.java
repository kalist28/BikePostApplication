package com.kalistratov.bikepost.api;

import android.util.Log;

import com.kalistratov.bikepost.entitys.EntityList;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class server.
 *
 * Establishes a connection to the server.
 * Allows making post and get requests.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class Server {

    /** Logging tag. */
    private final String TAG = getClass().getSimpleName();

    /**
     * Single instance.
     */
    private static Server init;

    /**
     * Library helper for requests
     */
    private final Retrofit retrofit;

    /**
     * Get instance.
     */
    public static Server get() {
        if (init == null) {
            return init = new Server();
        }
        return init;
    }

    /**
     * Private constructor.
     * Create connection from server.
     */
    private Server() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer c2hhMjU2OjY3NTpjZDkwOWVmNWQwNTExOTY2NWUzZGQ5NmIyYTk0YjI3NDM0NGY3OGYwNmJlZjM3MTg5Y2MzZGU5MGJkMTJhNTNl")
                    .addHeader("Content-Type", "application/vnd.api+json; charset=utf-8")
                    .addHeader("User-Agent", "request")
                    .addHeader("Accept", "*/*");
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://bike.kalistratov.ru/") // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .client(okHttpClient)
                .build();
    }

    /** Create a get request to the server. */
    public <E> void getResponse(Action<EntityList<E>> action) {
        action.getCall(retrofit).enqueue(new Callback<EntityList<E>>() {
            @Override
            public void onResponse(Call<EntityList<E>> call, Response<EntityList<E>> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    action.action(response);
                    Log.e(TAG, "Server apply : " + response.message());
                } else {
                    // сервер вернул ошибку
                    Log.e(TAG, "Server error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<EntityList<E>> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.e(TAG, "Server error: " + t.getMessage());
            }
        });
    }
}
