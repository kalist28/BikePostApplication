package com.kalistratov.bikepost.api;

import com.kalistratov.bikepost.entitys.BikePost;
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

    public void getResponse (Action<EntityList<BikePost>> action) {
        ServerInquiries service = retrofit.create(ServerInquiries.class);
        Call<EntityList<BikePost>> call = service.getPosts();
        call.enqueue(new Callback<EntityList<BikePost>>() {
            @Override
            public void onResponse(Call<EntityList<BikePost>> call, Response<EntityList<BikePost>> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                   action.action(response);
                } else {
                    // сервер вернул ошибку

                }
            }

            @Override
            public void onFailure(Call<EntityList<BikePost>> call, Throwable t) {
                // ошибка во время выполнения запроса
            }
        });
    }
}
