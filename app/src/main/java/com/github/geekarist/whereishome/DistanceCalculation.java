package com.github.geekarist.whereishome;

import android.util.Log;
import android.widget.Toast;

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

// TODO extract inteface and implement with Google DistanceMatrix or Citymapper
public class DistanceCalculation {
    private static final String TAG = DistanceCalculation.class.getSimpleName();

    private String mFrom;
    private String mTo;
    private DistanceMatrixService mDistanceMatrixService;

    public DistanceCalculation() {
        createDistanceMatrixService();
    }

    public DistanceCalculation from(String address) {
        mFrom = address;
        return this;
    }

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

    public void complete(BiConsumer<String, Integer> callback) {
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
                        String msg = "Error during DistanceMatrix call";
                        Toast.makeText(PickCommuteActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, msg, t);
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
