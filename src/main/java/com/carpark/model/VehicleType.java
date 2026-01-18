package com.carpark.model;

public enum VehicleType {
    SMALL(1, 0.10),
    MEDIUM(2, 0.20),
    LARGE(3, 0.40);

    private final int code;
    private final double ratePerMinute;

    VehicleType(int code, double ratePerMinute) {
        this.code = code;
        this.ratePerMinute = ratePerMinute;
    }

    public static VehicleType fromCode(int code) {
        for (VehicleType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle type code: " + code);
    }

    public double getRatePerMinute() {
        return ratePerMinute;
    }
}
