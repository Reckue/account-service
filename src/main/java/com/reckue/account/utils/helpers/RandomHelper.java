package com.reckue.account.utils.helpers;

import java.util.UUID;

public class RandomHelper {

    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generate(String key) {
        if (key != null) {
            return UUID.nameUUIDFromBytes(key.getBytes()).toString().replaceAll("-", "");
        } else {
            return RandomHelper.generate();
        }
    }
}
