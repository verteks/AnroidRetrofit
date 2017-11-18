package course.labs.retrofitlab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import course.labs.retrofitlab.R;
import course.labs.retrofitlab.model.Movie;
import course.labs.retrofitlab.model.MoviesResponse;
import course.labs.retrofitlab.rest.ApiClient;
import course.labs.retrofitlab.rest.ServiceGenerator;
import course.labs.retrofitlab.rest.TMDBInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static course.labs.retrofitlab.rest.ApiClient.BASE_URL;

/**
 * Created by vshir on 18.11.2017.
 */

public class MovieInfo extends Activity {

    private final static String API_KEY = "7e8f60e325cd06e164799af1e317d7a7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("movie info");
        setContentView(R.layout.movie_info);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",-1);
        if (id==-1){
            Toast.makeText(this,"Ошибка передачи id фильма ",Toast.LENGTH_LONG).show();
        }else{

            //Генерируем адаптер для взаимодействия по сети с помощью библиотеки Retrofit
            TMDBInterface apiService = ServiceGenerator.createService(TMDBInterface.class, BASE_URL);
            ApiClient.getClient().create(TMDBInterface.class);

            //TODO заменить запрос получения топа фильмов на получение списка новинок, ожидаемых в прокате
            //Осуществляем асинхронный запрос к серверу в соответствии с описанием в классе TMDBInterface

            Call<MoviesResponse> call = apiService.getMovieDetails(id,API_KEY);
            //Обрабатываем ответ от сервера на запрос
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    //код ответа сервера (200 - ОК), в данном случае далее не используется
                    int statusCode = response.code();
                    //получаем список фильмов, произведя парсинг JSON ответа с помощью библиотеки Retrofit
                    List<Movie> movies = response.body().getResults();

                    //TODO перемешать полученный от сервера список фильмов с помощью Collections.shuffle(list) для наглядности
                    Collections.shuffle(movies);

                    //создаем адаптер для компонента RecyclerView.
                    //компонента станет отображать список загруженных фильмов


                 }
                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                }

            });
        }
    }
}