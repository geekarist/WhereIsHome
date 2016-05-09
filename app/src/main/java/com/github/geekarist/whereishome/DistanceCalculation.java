package com.github.geekarist.whereishome;

import com.annimon.stream.function.BiConsumer;

public interface DistanceCalculation {
    DistanceCalculation from(String address);

    DistanceCalculation to(String address);

    void complete(BiConsumer<String, Integer> callback, BiConsumer<Throwable, String> errorCallback);
}
