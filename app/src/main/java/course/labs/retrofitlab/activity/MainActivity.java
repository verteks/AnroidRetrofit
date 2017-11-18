package course.labs.retrofitlab.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import course.labs.retrofitlab.R;
import course.labs.retrofitlab.adapter.MoviesAdapter;
import course.labs.retrofitlab.model.Movie;
import course.labs.retrofitlab.model.MoviesResponse;
import course.labs.retrofitlab.rest.ApiClient;
import course.labs.retrofitlab.rest.ServiceGenerator;
import course.labs.retrofitlab.rest.TMDBInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static course.labs.retrofitlab.rest.ApiClient.BASE_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //полученный с сайта themoviedb.org ключ API KEY
    private final static String API_KEY = "7e8f60e325cd06e164799af1e317d7a7";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO Изменить на "UPCOMING"
        this.setTitle("UPCOMING");
        setContentView(R.layout.activity_main);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Необходимо получить API KEY от themoviedb.org!", Toast.LENGTH_LONG).show();
            return;
        }

        //получаем компоненту RecyclerView и задаем ей способ отображения, линейный менеджер макета
        this.recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Получаем компоненту SwipeRefreshLayout для управления поведением при обновлении списка
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Обновляем данные по сети
                refresh();
            }
        });

        //Первоначальная загрузка данных при создании окна
        refresh();
    }

    private void refresh() {
	    //TODO включить анимацию обновления для SwipeRefreshLayout с помощью метода setRefreshing
        //включаем анимацию обновления
        mSwipeRefreshLayout.setRefreshing(true);

        //Генерируем адаптер для взаимодействия по сети с помощью библиотеки Retrofit
        TMDBInterface apiService = ServiceGenerator.createService(TMDBInterface.class, BASE_URL);
        ApiClient.getClient().create(TMDBInterface.class);

	    //TODO заменить запрос получения топа фильмов на получение списка новинок, ожидаемых в прокате
        //Осуществляем асинхронный запрос к серверу в соответствии с описанием в классе TMDBInterface
        //Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY);
        Call<MoviesResponse> call = apiService.getUpcomingMovies(API_KEY);
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
                recyclerView.setAdapter(new MoviesAdapter(movies, R.layout.list_item_movie, getApplicationContext()));

                //TODO убрать анимацию обновления для SwipeRefreshLayout с помощью метода setRefreshing
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                //onFailure вызывается, когда проблема при отправке запроса. Например, сервер не отвечает или нет сети.
                //Заносим сведения об ошибке в журнал методом Log.e(TAG, MESSAGE)
                //Данный метод используется для журнализации ошибок (e = error)
                Log.e(TAG, t.toString());

                //TODO убираем анимацию обновления для SwipeRefreshLayout с помощью метода setRefreshing
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
