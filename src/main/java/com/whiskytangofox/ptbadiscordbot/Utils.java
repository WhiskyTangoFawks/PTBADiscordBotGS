package com.whiskytangofox.ptbadiscordbot;

public class Utils {

    public static String cleanAndTruncateString(String string) {
        if (string.contains("(")) {
            string = string.substring(0, string.indexOf("(") - 1);
        }
        return cleanString(string);
    }

    public static String cleanString(String string) {
        return string.toLowerCase().replace(" ", "");
    }


    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBooleanString(String string) {
        return "false".equalsIgnoreCase(string) || "true".equalsIgnoreCase(string);
    }

}


