package course.labs.retrofitlab.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс для генерации клиента взаимодействия с сетью посредством библиотеки Retrofit
 */
public class ApiClient {

    //Базовый URL, с которого начинаются все запросы к API сервера
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    //базовый URL
                    .baseUrl(BASE_URL)
                    //конвертер
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
