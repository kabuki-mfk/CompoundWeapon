package com.github.kabuki.compoundweapon.utils;

import java.util.regex.Pattern;

public class ConvertHelper {
    public static boolean isInteger(String str)
    {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumber(String str)
    {
        return str.matches("-?\\d+.?\\d+");
    }

    public static double pasteDouble(String str)
    {
        return isNumber(str) ? Double.parseDouble(str) : 0;
    }
}
