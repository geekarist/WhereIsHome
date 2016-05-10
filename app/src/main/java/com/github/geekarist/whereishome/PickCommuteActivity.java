package com.github.geekarist.whereishome;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickCommuteActivity extends AppCompatActivity {
    public static final String DATA_RESULT_COMMUTE = "RESULT_PLACE";
    public static final String EXTRA_COMMUTE_TO_MODIFY = "EXTRA_COMMUTE";

    private static final int REQUEST_PLACE_PICKER = 1;
    private static final String TAG = PickCommuteActivity.class.getSimpleName();

    private static final String EXTRA_PICK_HOME_ADDRESS = "EXTRA_PICK_HOME_ADDRESS";
    private static final String EXTRA_HOME_ADDRESS = "EXTRA_HOME_ADDRESS";

    private String mHomeAddress;
    private boolean mPickHomeAddress;
    private Place mSelectedPlace;
    private Commute mCommuteToModify;

    private DistanceCalculation mDistanceCalculation;

    @Bind(R.id.pick_commute_text_address_value)
    TextView mAddressText;
    @Bind(R.id.pick_commute_edit_number_per_week)
    EditText mEditNumber;
    @Bind(R.id.pick_commute_text_number_per_week)
    TextView mEditNumberLabel;

    public static Intent newCreationIntent(Context context, String homeAddress, boolean pickHomeAddress) {
        Intent intent = new Intent(context, PickCommuteActivity.class);
        intent.putExtra(EXTRA_HOME_ADDRESS, homeAddress);
        intent.putExtra(EXTRA_PICK_HOME_ADDRESS, pickHomeAddress);
        return intent;
    }

    public static Intent newModificationIntent(Context context, String homeAddress,
                                               boolean pickHomeAddress, Commute commuteToModify) {
        Intent intent = newCreationIntent(context, homeAddress, pickHomeAddress);
        intent.putExtra(EXTRA_COMMUTE_TO_MODIFY, commuteToModify);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_commute);
        ButterKnife.bind(this);

        mHomeAddress = getIntent().getStringExtra(EXTRA_HOME_ADDRESS);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent placeData) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                mSelectedPlace = PlacePicker.getPlace(this, placeData);
                mAddressText.setText(placeStr(mSelectedPlace.getAddress(), mSelectedPlace.getLatLng()));
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    public void createCommute(Consumer<Commute> callback) {
        String selectedPlaceStr = placeStr(mSelectedPlace.getAddress(), mSelectedPlace.getLatLng());
        if (mPickHomeAddress) {
            callback.accept(new Commute(selectedPlaceStr, 0, getString(R.string.pick_commute_your_home_duration_text), 0));
        } else {
            mDistanceCalculation
                    .from(mHomeAddress)
                    .to(selectedPlaceStr)
                    .complete((durationText, durationSeconds) -> {
                        Commute commute =
                                new Commute(
                                        selectedPlaceStr, durationSeconds, durationText,
                                        Integer.parseInt(String.valueOf(mEditNumber.getText())));
                        callback.accept(commute);
                    }, (error, msg) -> {
                        Toast.makeText(PickCommuteActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, msg, error);
                    });
        }
    }

    private String placeStr(CharSequence placeAddress, LatLng placeLatLng) {
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
