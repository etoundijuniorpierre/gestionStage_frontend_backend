package com.internship.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse API standardisé
 * Fournit une structure cohérente pour toutes les réponses de l'API
 *
 * @param <T> Type de données retournées
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indique si la requête a réussi
     */
    private boolean success;

    /**
     * Message descriptif
     */
    private String message;

    /**
     * Données de la réponse
     */
    private T data;

    /**
     * Timestamp de la réponse
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Informations de pagination (optionnel)
     */
    private PageInfo pagination;

    /**
     * Détails de l'erreur (optionnel)
     */
    private ErrorDetails error;

    /**
     * Crée une réponse de succès avec données
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Crée une réponse de succès avec message personnalisé
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crée une réponse de succès avec pagination
     */
    public static <T> ApiResponse<T> success(T data, PageInfo pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * Crée une réponse de succès avec message personnalisé et pagination
     */
    public static <T> ApiResponse<T> success(String message, T data, PageInfo pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * Crée une réponse d'erreur
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Crée une réponse d'erreur avec détails
     */
    public static <T> ApiResponse<T> error(String message, ErrorDetails errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errorDetails)
                .build();
    }

    /**
     * Informations de pagination
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * Détails de l'erreur
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String field;
        private Object rejectedValue;
    }
}
