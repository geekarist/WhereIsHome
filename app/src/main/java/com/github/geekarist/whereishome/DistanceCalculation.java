package com.github.geekarist.whereishome;

import com.annimon.stream.function.BiConsumer;

import java.sql.Time;

public interface DistanceCalculation {
    DistanceCalculation from(String address);

    DistanceCalculation from(double latitude, double longitude);

    DistanceCalculation to(String address);

    DistanceCalculation to(double latitude, double longitude);

    void complete(BiConsumer<String, Integer> callback, BiConsumer<Throwable, String> errorCallback);

    DistanceCalculation at(Time timeOfCommute);
}
