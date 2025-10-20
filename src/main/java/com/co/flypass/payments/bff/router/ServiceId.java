package com.co.flypass.payments.bff.router;

import java.util.Locale;

public record ServiceId(String value) {

    public static String normalize(String raw, boolean removeInnerSpaces) {
        if (raw == null) return null;
        String v = raw.trim().toLowerCase(Locale.ROOT);
        if (removeInnerSpaces) v = v.replace(" ", "");
        if (v.isBlank()) return null;
        return v;
    }

    public static String normalize(String raw) {
        return normalize(raw, true);
    }

    public static ServiceId from(String raw) {
        String n = normalize(raw, true);
        if (n == null) throw new IllegalArgumentException("serviceId must not be null or blank");
        return new ServiceId(n);
    }

    @Override
    public String toString() {
        return value;
    }
}
