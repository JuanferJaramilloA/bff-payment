package com.co.flypass.payments.bff.router;

import java.util.Locale;

public enum ServiceId {
    BANCOLOMBIA("bancolombia"),
    EXTERNAL("external");

    private final String id;

    ServiceId(String id) {
        this.id = id;
    }

    public String value() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

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
        for (ServiceId sid : values()) {
            if (sid.id.equals(n)) return sid;
        }
        throw new IllegalArgumentException("Unknown serviceId: '" + raw + "'. Allowed: " + java.util.Arrays.toString(values()));
    }
}
