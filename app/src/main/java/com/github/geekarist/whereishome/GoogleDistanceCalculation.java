package com.github.geekarist.whereishome;

import com.annimon.stream.Optional;
import com.annimon.stream.function.BiConsumer;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GoogleDistanceCalculation implements DistanceCalculation {

    private String mFrom;
    private String mTo;
    private DistanceMatrixService mDistanceMatrixService;

    public GoogleDistanceCalculation() {
        createDistanceMatrixService();
    }

    @Override
    public DistanceCalculation from(String address) {
        mFrom = address;
        return this;
    }

    @Override
    public DistanceCalculation to(String address) {
        mTo = address;
        return this;
    }

    private void createDistanceMatrixService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mDistanceMatrixService = retrofit.create(DistanceMatrixService.class);
    }

    @Override
    public void complete(BiConsumer<String, Integer> callback, BiConsumer<Throwable, String> errorCallback) {
        mDistanceMatrixService.getDistanceMatrix(
                String.valueOf(mFrom), mTo,
                "AIzaSyB1FGeq0g-kv2_pa7N9J-t601V9Nj9ibfw")
                .enqueue(new Callback<DistanceMatrix>() {
                    @Override
                    public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
                        int durationSeconds = optionalOfDuration(response).map(d -> (int) Math.round(d.value)).orElse(0);
                        String durationText = optionalOfDuration(response).map(d -> d.text).orElse("No itinerary found");
                        callback.accept(durationText, durationSeconds);
                    }

                    @Override
                    public void onFailure(Call<DistanceMatrix> call, Throwable t) {
                        errorCallback.accept(t, "Error during DistanceMatrix call");
                    }
                });
    }

    private Optional<DistanceMatrixElement> optionalOfDuration(Response<DistanceMatrix> response) {
        return Optional.ofNullable(response)
                .map(Response::body)
                .map(b -> b.rows)
                .filter(rows -> !rows.isEmpty())
                .map(rows -> rows.get(0))
                .map(r -> r.elements).map(elements -> elements.get(0))
                .map(e -> e.duration);
    }

    interface DistanceMatrixService {
        @GET("/maps/api/distancematrix/json?mode=transit")
        Call<DistanceMatrix> getDistanceMatrix(@Query("origins") String origin, @Query("destinations") String destination, @Query("key") String key);
    }

    public static class DistanceMatrix {
        List<String> destinationAddresses;
        List<String> originAddresses;
        List<DistanceMatrixRow> rows;
    }

    public static class DistanceMatrixRow {
        List<DistanceMatrixElements> elements;
    }

    public static class DistanceMatrixElements {
        DistanceMatrixElement distance;
        DistanceMatrixElement duration;
    }

    public static class DistanceMatrixElement {
        String text;
        double value;
    }
}
