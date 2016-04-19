package com.github.geekarist.whereishome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.PrintWriter;
import java.io.StringWriter;
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

public class PickCommuteActivity extends AppCompatActivity {
    private static final int REQUEST_PLACE_PICKER = 1;
    public static final String DATA_RESULT_COMMUTE = "RESULT_PLACE";
    private static final String EXTRA_PICK_HOME_ADDRESS = "EXTRA_PICK_HOME_ADDRESS";
    private static final String EXTRA_HOME_ADDRESS = "EXTRA_HOME_ADDRESS";

    private String mHomeAddress;
    private boolean mPickHomeAddress;

    public static Intent newIntent(Context context, String homeAddress, boolean pickHomeAddress) {
        Intent intent = new Intent(context, PickCommuteActivity.class);
        intent.putExtra(EXTRA_HOME_ADDRESS, homeAddress);
        intent.putExtra(EXTRA_PICK_HOME_ADDRESS, pickHomeAddress);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeAddress = getIntent().getStringExtra(EXTRA_HOME_ADDRESS);
        mPickHomeAddress = getIntent().getBooleanExtra(EXTRA_PICK_HOME_ADDRESS, false);
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            StringWriter msg = new StringWriter();
            PrintWriter msgPrint = new PrintWriter(msg);
            e.printStackTrace(msgPrint);
            new AlertDialog.Builder(this)
                    .setTitle("Google Play Services Error")
                    .setMessage(msg.toString()).create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent placeData) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, placeData);
                Intent data = new Intent();
                newCommute(place, commute -> {
                    data.putExtra(DATA_RESULT_COMMUTE, commute);
                    setResult(resultCode, data);
                });
            }
        }
    }

    public void newCommute(Place place, Consumer<Commute> callback) {
        if (mPickHomeAddress) {
            callback.accept(new Commute(placeLabel(place), 0));
        } else {
            findDistance(mHomeAddress, place, callback);
        }
    }

    private void findDistance(String fromAddress, final Place toPlace, Consumer<Commute> callback) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DistanceMatrixService service = retrofit.create(DistanceMatrixService.class);
        service.getDistanceMatrix(
                String.valueOf(fromAddress), String.valueOf(toPlace.getAddress()),
                "AIzaSyB1FGeq0g-kv2_pa7N9J-t601V9Nj9ibfw")
                .enqueue(new Callback<DistanceMatrix>() {
                    @Override
                    public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
                        int duration = Optional.ofNullable(response)
                                .map(Response::body).map(b -> b.rows).map(rows -> rows.get(0))
                                .map(r -> r.elements).map(elements -> elements.get(0))
                                .map(e -> e.duration).map(d -> (int) Math.round(d.value))
                                .orElse(0);
                        String label = placeLabel(toPlace);
                        Commute commute = new Commute(label, duration);
                        callback.accept(commute);
                    }

                    @Override
                    public void onFailure(Call<DistanceMatrix> call, Throwable t) {
                    }
                });
    }

    private String placeLabel(Place place) {
        return String.valueOf(place.getAddress() != null ? place.getAddress() : place.getLatLng());
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
