package com.github.geekarist.whereishome;

import com.annimon.stream.Optional;
import com.annimon.stream.function.BiConsumer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class CitymapperDistanceCalculation implements DistanceCalculation {

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";
    private String mFrom;
    private String mTo;
    private TravelTimeService mRetrofitService;
    private Date mTimeOfCommute;

    public CitymapperDistanceCalculation() {
        createRetrofitService();
    }

    @Override
    public DistanceCalculation from(String address) {
        throw new UnsupportedOperationException("Addresses are not supported by the Citymapper API");
    }

    @Override
    public DistanceCalculation from(double latitude, double longitude) {
        mFrom = String.format(Locale.ENGLISH, "%f,%f", latitude, longitude);
        return this;
    }

    @Override
    public DistanceCalculation to(String address) {
        throw new UnsupportedOperationException("Addresses are not supported by the Citymapper API");
    }

    @Override
    public DistanceCalculation to(double latitude, double longitude) {
        mTo = String.format(Locale.ENGLISH, "%f,%f", latitude, longitude);
        return this;
    }

    @Override
    public DistanceCalculation at(Time timeOfCommute) {
        mTimeOfCommute = new CitymapperDate(0);
        return this;
    }

    private void createRetrofitService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(DATE_PATTERN)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://developer.citymapper.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mRetrofitService = retrofit.create(TravelTimeService.class);
    }

    @Override
    public void complete(BiConsumer<String, Integer> callback, BiConsumer<Throwable, String> errorCallback) {
        mRetrofitService.getTravelTime(
                mFrom, mTo, mTimeOfCommute,
                BuildConfig.CITYMAPPER_API_KEY)
                .enqueue(new Callback<TravelTime>() {
                    @Override
                    public void onResponse(Call<TravelTime> call, Response<TravelTime> response) {
                        int durationSeconds = optionalOfBody(response)
                                .map(t -> t.travelTimeMinutes * 60)
                                .orElse(0);
                        String durationText = optionalOfBody(response)
                                .map(t -> String.format("%d minutes", t.travelTimeMinutes))
                                .orElse("No itinerary found");
                        callback.accept(durationText, durationSeconds);
                    }

                    @Override
                    public void onFailure(Call<TravelTime> call, Throwable t) {
                        errorCallback.accept(t, "Error during Citymapper call");
                    }
                });
    }

    private Optional<TravelTime> optionalOfBody(Response<TravelTime> response) {
        return Optional.ofNullable(response)
                .map(Response::body);
    }

    interface TravelTimeService {
        @GET("/api/1/traveltime/")
        Call<TravelTime> getTravelTime(@Query("startcoord") String startCoord, @Query("endcoord") String endCoord, @Query("time") Date timeOfCommute, @Query("key") String key);
    }

    public static class TravelTime {
        int travelTimeMinutes;
    }
}
