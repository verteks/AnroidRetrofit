package course.labs.retrofitlab.rest;

import course.labs.retrofitlab.model.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Интерфейс для взаимодействия с API сервера themoviedb.org
 * См. примеры на
 * https://www.themoviedb.org/documentation/api/discover
 */
public interface TMDBInterface {

    //Для получения ТОПа фильмов http://api.themoviedb.org/3/movie/top_rated?api_key=###
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    //Для получения фильма по ID http://api.themoviedb.org/3/movie/123?api_key=###
    @GET("movie/{id}")
    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    /*
    Для получения списка ожидаемых новинок
    http://api.themoviedb.org/3/movie/upcoming?api_key=###
    */
    //TODO добавить метод для получения списка новинок, ожидаемых в прокате

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey);
}
