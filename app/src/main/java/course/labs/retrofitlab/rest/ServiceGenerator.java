package course.labs.retrofitlab.rest;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.logging.Level;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Vladimir Ulyanov
 *
 * Retrofit with logging, cookie storing and authentication
 */

public class ServiceGenerator {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static Retrofit retrofit=null;

    public static <S> S createService(Class<S> serviceClass, String url) {
        return createService(serviceClass, url, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String url, String username, String password) {
        return retrofit(url, username,password).create(serviceClass);
    }

    public static Retrofit retrofit(String url){
        return retrofit(url, null,null);
    }

    public static Retrofit retrofit(String url, String username, String password){
        if (retrofit == null) {
            if (username != null && password != null) {
                String credentials = username + ":" + password;
                final String basic =
                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", basic)
                                .header("Accept", "application/json")
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
            }
            //logging
            Log.i("retrofit","retrofit logging");
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            Log.i("retrofit", "retrofit cookies");
            //create a cookieManager so your client can be cookie persistant
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            httpClient.cookieJar(new JavaNetCookieJar(cookieManager));

            OkHttpClient client = httpClient.build();
            retrofit = builder(url).client(client).build();
        }
        return retrofit;
    }
}
