package com.example.kjh.shakeit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidEmail(String email) {
        String patternString = "^[A-Z0-9a-z\\._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        String patternString = "^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static boolean isValidName(String name) {
        String patternString = "^[가-힣]{2,8}|[a-zA-Z]{4,20}$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }
}
