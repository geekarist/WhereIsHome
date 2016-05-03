package com.github.geekarist.whereishome;

import com.annimon.stream.function.BiConsumer;

public class AddressSearch {
    public AddressSearch from(String address) {
        return this;
    }

    public AddressSearch to(String address) {
        return this;
    }

    public void complete(BiConsumer<String, Integer> callback) {
    }
}
