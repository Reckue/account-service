package com.reckue.account.util.helper;

import java.sql.Timestamp;

/**
 * Class TimestampHelper represents a time generator.
 *
 * @author Kamila Meshcheryakova
 */
public class TimestampHelper {

    /**
     * This method is used to generate current time as long type.
     *
     * @return time as long type
     */
    public static long getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }
}
