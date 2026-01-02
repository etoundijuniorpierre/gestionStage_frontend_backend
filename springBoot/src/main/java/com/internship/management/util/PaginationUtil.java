package com.internship.management.util;

import com.internship.management.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utilitaire pour créer des réponses paginées
 */
public class PaginationUtil {

        /**
         * Crée un objet Pageable à partir des paramètres de requête
         */
        public static Pageable createPageable(int page, int size, String sortField) {
                return PageRequest.of(page, size, Sort.by(sortField).ascending());
        }

        /**
         * Crée les informations de pagination à partir d'une Page Spring Data
         */
        public static ApiResponse.PageInfo createPageInfo(Page<?> page) {
                return ApiResponse.PageInfo.builder()
                                .currentPage(page.getNumber())
                                .pageSize(page.getSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .hasNext(page.hasNext())
                                .hasPrevious(page.hasPrevious())
                                .build();
        }

        /**
         * Crée une ApiResponse avec pagination à partir d'une Page Spring Data
         */
        public static <T> ApiResponse<T> createPagedResponse(Page<T> page) {
                return createPagedResponse(page, "Success");
        }

        /**
         * Crée une ApiResponse avec pagination et message personnalisé
         */
        public static <T> ApiResponse<T> createPagedResponse(Page<T> page, String message) {
                return ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data((T) page.getContent())
                                .pagination(createPageInfo(page))
                                .build();
        }
}