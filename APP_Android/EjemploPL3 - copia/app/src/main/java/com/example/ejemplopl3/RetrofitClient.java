package com.example.ejemplopl3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date; // Importa Date
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // --- INICIO DE LA CORRECCIÓN ---
            // 1. Crea el constructor de Gson
            GsonBuilder gsonBuilder = new GsonBuilder();

            // 2. REGISTRA NUESTRO ADAPTADOR PERSONALIZADO para el tipo 'Date'
            gsonBuilder.registerTypeAdapter(Date.class, new CustomDateAdapter());

            // 3. Construye el objeto Gson
            Gson gson = gsonBuilder.create();
            // --- FIN DE LA CORRECCIÓN ---


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    // Pasa el objeto 'gson' personalizado
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
