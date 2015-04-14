package com.ibm.mil.smartringer;

import java.util.HashMap;
import java.util.Map;

public enum SensitivityLevel {
    // do not change code value passed to the enum's constructor as this is used for making
    // an enum value persistable within SharedPreferences
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int mCode;
    private static final Map<Integer, SensitivityLevel> mValuesByCode;

    static {
        mValuesByCode = new HashMap<>();
        for (SensitivityLevel level : values()) {
            mValuesByCode.put(level.mCode, level);
        }
    }

    private SensitivityLevel(int code) {
        mCode = code;
    }

    public static SensitivityLevel lookupByCode(int code) {
        return mValuesByCode.get(code);
    }

    public int getCode() {
        return mCode;
    }
}
