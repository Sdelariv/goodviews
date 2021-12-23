package be.svend.goodviews.services;

public class StringValidator {

    public static boolean isValidString(String string) {
        if (string == null) return false;
        if (string.contains(";")) return false;

        return true;

    }
}
