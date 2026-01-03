package com.internship.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lorsqu'une règle métier est violée.
 * Retourne automatiquement un code HTTP 400 (BAD_REQUEST).
 * 
 * @author Backend Team
 * @version 1.0
 * @since 2026-01-03
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructeur avec message par défaut.
     *
     * @param message le message d'erreur
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_RULE_VIOLATION";
    }

    /**
     * Constructeur avec code d'erreur personnalisé.
     *
     * @param errorCode le code d'erreur spécifique
     * @param message   le message d'erreur
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
