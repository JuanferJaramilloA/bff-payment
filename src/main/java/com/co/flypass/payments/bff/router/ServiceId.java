package com.co.flypass.payments.bff.router;

import java.util.Locale;

public enum ServiceId {
    BANCOLOMBIA("bancolombia");

    private final String value;

    ServiceId(String value) { this.value = value; }

    public String value() { return value; }

    public static ServiceId from(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toLowerCase(Locale.ROOT);
        for (var s : values()) if (s.value.equals(v)) return s;
        return null;
    }
}
