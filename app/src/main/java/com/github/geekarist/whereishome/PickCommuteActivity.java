package com.github.geekarist.whereishome;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Time;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickCommuteActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static final String DATA_RESULT_COMMUTE = "RESULT_PLACE";
    public static final String EXTRA_COMMUTE_TO_MODIFY = "EXTRA_COMMUTE";
    public static final String EXTRA_ERROR_MSG = "EXTRA_ERROR_MSG";

    private static final int REQUEST_PLACE_PICKER = 1;
    private static final String TAG = PickCommuteActivity.class.getSimpleName();

    private static final String EXTRA_PICK_HOME_ADDRESS = "EXTRA_PICK_HOME_ADDRESS";
    private static final String EXTRA_HOME_LAT = "EXTRA_HOME_LAT";
    private static final String EXTRA_HOME_LON = "EXTRA_HOME_LON";

    private double mHomeLat;
    private double mHomeLon;

    private boolean mPickHomeAddress;
    private Place mSelectedPlace;
    private Commute mCommuteToModify;
    private Time mTimeOfCommute;

    private DistanceCalculation mDistanceCalculation;

    @Bind(R.id.pick_commute_text_address_value)
    TextView mAddressText;
    @Bind(R.id.pick_commute_edit_number_per_week)
    EditText mEditNumber;
    @Bind(R.id.pick_commute_text_number_per_week)
    TextView mEditNumberLabel;
    @Bind(R.id.pick_commute_time_of_arrival)
    Button mButtonTimeOfCommute;

    public static Intent newCreationIntent(Context context, boolean pickHomeAddress,
                                           double homeLat, double homeLon) {
        Intent intent = new Intent(context, PickCommuteActivity.class);
        intent.putExtra(EXTRA_HOME_LAT, homeLat);
        intent.putExtra(EXTRA_HOME_LON, homeLon);
        intent.putExtra(EXTRA_PICK_HOME_ADDRESS, pickHomeAddress);
        return intent;
    }

    public static Intent newModificationIntent(Context context, boolean pickHomeAddress,
                                               Commute commuteToModify, double homeLat,
                                               double homeLon) {
        Intent intent = newCreationIntent(context, pickHomeAddress, homeLat, homeLon);
        intent.putExtra(EXTRA_COMMUTE_TO_MODIFY, commuteToModify);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_commute);
        ButterKnife.bind(this);

        mHomeLat = getIntent().getDoubleExtra(EXTRA_HOME_LAT, 0);
        mHomeLon = getIntent().getDoubleExtra(EXTRA_HOME_LON, 0);
        mPickHomeAddress = getIntent().getBooleanExtra(EXTRA_PICK_HOME_ADDRESS, false);
        mCommuteToModify = getIntent().getParcelableExtra(EXTRA_COMMUTE_TO_MODIFY);

        if (mPickHomeAddress) {
            mEditNumberLabel.setVisibility(View.GONE);
            mEditNumber.setVisibility(View.GONE);
        }

        Optional.ofNullable(mCommuteToModify)
                .ifPresent(c -> mEditNumber.setText(String.valueOf(c.mNumberPerWeek)));

        try {
            final PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Optional.ofNullable(mCommuteToModify)
                    .map(this::getInitialBounds)
                    .ifPresent(intentBuilder::setLatLngBounds);
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            StringWriter msg = new StringWriter();
            PrintWriter msgPrint = new PrintWriter(msg);
            e.printStackTrace(msgPrint);
            new AlertDialog.Builder(this)
                    .setTitle("Google Play Services Error")
                    .setMessage(msg.toString()).create().show();
        }

        mDistanceCalculation = new CityMapperDistanceCalculation();
    }

    private LatLngBounds getInitialBounds(Commute commute) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LatLngBounds bounds;
        try {
            List<Address> addresses = geocoder.getFromLocationName(commute.mAddress, 1);
            bounds = Optional.ofNullable(addresses)
                    .filter(l -> !l.isEmpty())
                    .map(l -> l.get(0))
                    .map(address -> {
                        double longitude = address.getLongitude();
                        double latitude = address.getLatitude();
                        return LatLngBounds.builder().include(new LatLng(latitude, longitude)).build();
                    }).orElse(null);
        } catch (IOException e) {
            return null;
        }
        return bounds;
    }

    @OnClick(R.id.pick_commute_time_of_arrival)
    public void showArrivalTimePicker() {
        DialogFragment newFragment = TimePickerFragment.newInstance(this, 9, 0);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimeOfCommute = Time.valueOf(String.format("%02d:%02d:00", hourOfDay, minute));
        mButtonTimeOfCommute.setText(String.format("%02d:%02d", hourOfDay, minute));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent placeData) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                mSelectedPlace = PlacePicker.getPlace(this, placeData);
                mAddressText.setText(placeLabel(mSelectedPlace.getAddress(), mSelectedPlace.getLatLng()));
            } else {
                Intent resultData = new Intent();
                Optional.ofNullable(placeData)
                        .map(data -> PlacePicker.getStatus(this, data))
                        .map(String::valueOf)
                        .ifPresent(statusStr -> resultData.putExtra(EXTRA_ERROR_MSG, statusStr));
                setResult(RESULT_CANCELED, resultData);
                finish();
            }
        }
    }

    public void createCommute(Consumer<Commute> callback) {
        String selectedPlaceStr = placeLabel(mSelectedPlace.getAddress(), mSelectedPlace.getLatLng());
        if (mPickHomeAddress) {
            callback.accept(new Commute(
                    selectedPlaceStr, 0, getString(R.string.pick_commute_your_home_duration_text), 0,
                    mSelectedPlace.getLatLng().latitude, mSelectedPlace.getLatLng().longitude));
        } else {
            mDistanceCalculation
                    .from(mHomeLat, mHomeLon)
                    .to(mSelectedPlace.getLatLng().latitude, mSelectedPlace.getLatLng().longitude)
                    .at(mTimeOfCommute)
                    .complete((durationText, durationSeconds) -> {
                        Commute commute =
                                new Commute(
                                        selectedPlaceStr, durationSeconds, durationText,
                                        Integer.parseInt(String.valueOf(mEditNumber.getText())),
                                        mSelectedPlace.getLatLng().latitude,
                                        mSelectedPlace.getLatLng().longitude);
                        callback.accept(commute);
                    }, (error, msg) -> {
                        Toast.makeText(PickCommuteActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, msg, error);
                    });
        }
    }

    private String placeLabel(CharSequence placeAddress, LatLng placeLatLng) {
        return String.valueOf(placeAddress != null ? placeAddress : String.format("%f,%f", placeLatLng.latitude, placeLatLng.longitude));
    }

    @OnClick(R.id.pick_commute_button_accept)
    void onClickAccept() {
        createCommute(newCommute -> {
            Intent data = new Intent();
            data.putExtra(DATA_RESULT_COMMUTE, newCommute);
            Optional.ofNullable(mCommuteToModify)
                    .ifPresent(c -> data.putExtra(EXTRA_COMMUTE_TO_MODIFY, c));
            setResult(RESULT_OK, data);
            finish();
        });
    }
}
