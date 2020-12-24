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

public class Server {

    private static Server init;
    private final Retrofit retrofit;
    public static Server get() {
        if (init == null) {
            return init = new Server();
        }
        return init;
    }

     private Server() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer c2hhMjU2OjY3NTpjZDkwOWVmNWQwNTExOTY2NWUzZGQ5NmIyYTk0YjI3NDM0NGY3OGYwNmJlZjM3MTg5Y2MzZGU5MGJkMTJhNTNl")
                        .addHeader("Content-Type", "application/vnd.api+json; charset=utf-8")
                        .addHeader("User-Agent", "request")
                        .addHeader("Accept", "*/*");
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://bike.kalistratov.ru/") // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .client(okHttpClient)
                .build();
    }

    public <E> void getResponse(Action<EntityList<E>> action) {
        action.getCall(retrofit).enqueue(new Callback<EntityList<E>>() {
            @Override
            public void onResponse(Call<EntityList<E>> call, Response<EntityList<E>> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    action.action(response);
                    Log.e(this.getClass().getName(), "Server apply : " + response.message());
                } else {
                    // сервер вернул ошибку
                    Log.e(this.getClass().getName(), "Server error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<EntityList<E>> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.e(this.getClass().getName(), "Server error: " + t.getMessage());
            }
        });
    }

    /*
    public void getResponse (Action<EntityList<BikePost>> action) {
        ServerInquiries service = retrofit.create(ServerInquiries.class);
        Call<EntityList<BikePost>> call = service.getPosts();
        call.enqueue(new Callback<EntityList<BikePost>>() {
            @Override
            public void onResponse(Call<EntityList<BikePost>> call, Response<EntityList<BikePost>> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    action.action(response);
                    Log.e(this.getClass().getName(), "Server apply : " + response.message());
                } else {
                    // сервер вернул ошибку
                    Log.e(this.getClass().getName(), "Server error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<EntityList<BikePost>> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.e(this.getClass().getName(), "Server error: " + t.getMessage();
            }
        });
    }

     */
}
