package course.labs.retrofitlab.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import course.labs.retrofitlab.R;
import course.labs.retrofitlab.activity.MovieInfo;
import course.labs.retrofitlab.model.Movie;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "Lab-Retrofit";
    //Внутрянняя модель - список фильмов для отображения компонентой
    private List<Movie> movies;
    //код, идентификатор ресурса для визуализации отдельного элемента, его макета
    private int rowLayout;
    //Ссылка на контекст, который мы сохраняем при создании объекта адаптера
    private Context context;

    //Класс ViewHolder необходим для наполнения данными элементы пользовательского интерфейса отдельного элемента списка
    //Класс MovieViewHolder - частный случай, использует макет list_item_movie.xml для отображения модели Movie
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView data;
        TextView movieDescription;
        TextView rating;
        ImageView poster;

        //Главный метод любого ViewHolder-а. Ему отдается View, узел дерева интерфейса, корневой для макета отдельного элемента
        //Основная задача данного метода - осуществить привязки к компонентам Android, чтобы в дальнейшем их можно было изменять/наполнять
        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            movieTitle = (TextView) v.findViewById(R.id.title);
            data = (TextView) v.findViewById(R.id.subtitle);
            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.rating);
            //TODO аналогично присвоить переменной poster соответствующий ImageView с помощью метода findViewById
            poster = (ImageView) v.findViewById(R.id.poster);
        }
    }

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    //

    /**
     * Данный метод используется компонентой RecyclerView, когда ей необходимо отрисовать элемент указанного типа
     * @param parent компонента для наполнения макетом отдельного элемента
     * @param viewType тип элемента (может быть различным, используется в случае разных типов элементов списка)
     * @return ViewHolder для отрисовки отдельного элемента
     */
    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        //берем макет из ресурсов по идентификатору rowLayout и заполняем им область для отдельного элемента списка
        //возвращается View, компонент, корневой для данного поддерева элементов пользовательского интерфейса
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        //возвращаем MovieViewHolder в нашем случае для привязки к компонентам
        return new MovieViewHolder(view);
    }


    /**
     * Данный метод используется компонентой RecyclerView для непосредственного наполнения пользовательского интерфейса отдельного элемента
     * По номеру элемента (position) списка находится его ViewHolder и вызывается команда привязки (bind) для подгрузки информации из внутренней модели
     * @param holder холдер, для управления интерфейсом
     * @param position номер элемента списка, обычно используется для выбора данных из модели для наполнения
     */
    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        //Получаем i-ый элемента списка моделей. В данном случае элемент списка фильмов с индексом position
        final Movie movie = movies.get(position);
        //Наполняем элементы интерфейса данными из модели
        holder.movieTitle.setText(movie.getTitle());
        holder.data.setText(movie.getReleaseDate());
        holder.movieDescription.setText(movie.getOverview());
        holder.rating.setText(movie.getVoteAverage().toString());

	    //Для обработки нажатий мыши на элемент списка, устанавливаем OnClickListener
	    //Это один из возможных вариантов
        holder.moviesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , MovieInfo.class );
                intent.putExtra("id",movie.getId());
                context.startActivity(intent);




                //Toast.makeText(context,"Надо сделать переход на полную информацию о фильме! Выбрано: "+ movie.getTitle(),Toast.LENGTH_LONG).show();
            }
        });

        //Загружаем изображение постера
        //Полный путь до изображения постера http://image.tmdb.org/t/p/w185/###.png
        //общий префикс http://image.tmdb.org/t/p/w185/
        String IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
        //TODO получить относительный путь до изображения постера данного фильма с помощью метода getPosterPath()
        String moviePoster = movie.getPosterPath();
        //Получаем полный путь до постера путем конкатенации строк
        String posterURL = IMAGE_URL_PREFIX+moviePoster;
        //TODO Запустить DownloadImageTask передав в конструкторе соответствующую компоненту ImageView, а в качестве параметра запуска URL файлаизображения
        DownloadImageTask downloadImageTask = new DownloadImageTask(holder.poster);
        downloadImageTask.execute(posterURL);
    }

    /**
     * Количество элементов списка
     * @return возвращает количество элементов для отрисовки, берется из внтутренней модели данных
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * DownloadImageTask - это класс, реализующй AsyncTask для асинхронной загрузки изображений
     *
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        /**
         * Конструктор класса. Создает объект AsyncTask, для загрузки изображения в указанный ImageView
         * @param bmImage компонента ImageView для загрузки в нее изображения
         */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        /**
         * Метод фоновой загрузки изображения, запускаемый неявно с помощью AsyncTask.execute()
         * Загружает файл по URL, переданному в качестве параметра и возвращает объект Bitmap изображения
         * @param urls ссылка URL на файл изображения
         * @return объект Bitmap скачанного изображения для использования в отрисовке компонентой ImageView в главном потоке
         */
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /**
         * Обновляет картинку в компоненте ImageView
         * Данный метод класса AsyncTask  работает в главном потоке интерфейса.
         * Когда уже все загружено, полученный Bitmap передается ImageView для отображения
         * @param result Bitmap, полученный в фоновом потоке doInBackground для отрисовки компонентой
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}