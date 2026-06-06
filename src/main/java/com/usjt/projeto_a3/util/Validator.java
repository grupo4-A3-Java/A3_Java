package com.usjt.projeto_a3.util;

import com.usjt.projeto_a3.exception.ValidationException;

public class Validator {
    
    public static void notNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new ValidationException(fieldName + " não pode ser nulo.");
        }
    }

    public static void notEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(fieldName + " não pode estar vazio.");
        }
    }

    public static void notNegative(double value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName + " não pode ser negativo.");
        }
    }

    public static void notZero(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " deve ser maior que zero.");
        }
    }

    public static void validEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email inválido: " + email);
        }
    }

    public static void minLength(String str, int min, String fieldName) {
        if (str.length() < min) {
            throw new ValidationException(fieldName + " deve ter no mínimo " + min + " caracteres.");
        }
    }
}
