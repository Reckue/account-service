package com.reckue.account.util.helper;

import java.util.UUID;

/**
 * Class RandomHelper represents a UUID generator as string.
 *
 * @author Kamila Meshcheryakova
 */
public class RandomHelper {

    /**
     * This method is used to generate a random string to identify the object.
     *
     * @return generated string
     */
    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * This method is used to generate a string using the passed word as a parameter.
     *
     * @param key passed word
     * @return generated string based on passed word
     */
    public static String generate(String key) {
        if (key != null) {
            return UUID.nameUUIDFromBytes(key.getBytes()).toString().replaceAll("-", "");
        } else {
            return RandomHelper.generate();
        }
    }
}
