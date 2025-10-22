package com.co.flypass.payments.bff.adapter;

public final class NormalizationUtils {

    private NormalizationUtils() {}

    public static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    public static String defaultIfBlank(String value, String def) {
        return isBlank(value) ? def : value.trim();
    }

    public static String normalizeBrand(String brand) {
        if (isBlank(brand)) return "";
        String up = brand.trim().toUpperCase().replace(" ", "");
        return switch (up) {
            case "MC", "MASTERCARD"      -> "Mastercard";
            case "VISA"                  -> "Visa";
            case "AMEX", "AMERICANEXPRESS" -> "American Express";
            default                      -> brand.trim();
        };
    }

    public static String maskCardNumber(String number) {
        if (number == null) return "****";
        String digits = number.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****" + last4;
    }

    public static String normalizeProductLabel(String raw, String def) {
        if (isBlank(raw)) return def;
        String n = raw.trim().toLowerCase();
        if (n.contains("débito") || n.contains("debito")) return "Tarjeta débito";
        if (n.contains("crédito") || n.contains("credito")) return "Tarjeta crédito";
        return raw.trim();
    }
}
