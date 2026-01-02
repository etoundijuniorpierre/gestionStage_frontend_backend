package com.internship.management.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Validateur de fichiers uploadés pour renforcer la sécurité
 */
@Component
public class FileValidator {

    // Types MIME autorisés pour les PDFs
    private static final List<String> ALLOWED_PDF_TYPES = Arrays.asList(
        "application/pdf"
    );

    // Types MIME autorisés pour les images
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    );

    // Taille maximale: 10 MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // Signatures de fichiers (magic numbers) pour validation
    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46}; // %PDF
    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47};

    /**
     * Valide un fichier PDF
     */
    public void validatePdfFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide ou null");
        }

        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "Le fichier est trop volumineux. Taille maximale: 10 MB"
            );
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PDF_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "Type de fichier non autorisé. Seuls les PDFs sont acceptés"
            );
        }

        // Vérifier le nom du fichier
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException(
                "Extension de fichier invalide. Seuls les fichiers .pdf sont acceptés"
            );
        }

        // Vérifier la signature du fichier (magic number)
        byte[] fileBytes = file.getBytes();
        if (fileBytes.length < PDF_SIGNATURE.length) {
            throw new IllegalArgumentException("Fichier PDF corrompu ou invalide");
        }

        for (int i = 0; i < PDF_SIGNATURE.length; i++) {
            if (fileBytes[i] != PDF_SIGNATURE[i]) {
                throw new IllegalArgumentException(
                    "Le fichier n'est pas un PDF valide (signature invalide)"
                );
            }
        }
    }

    /**
     * Valide un fichier image
     */
    public void validateImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide ou null");
        }

        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "Le fichier est trop volumineux. Taille maximale: 10 MB"
            );
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "Type de fichier non autorisé. Seules les images (JPEG, PNG, WebP) sont acceptées"
            );
        }

        // Vérifier le nom du fichier
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        String lowerFilename = filename.toLowerCase();
        if (!lowerFilename.endsWith(".jpg") && 
            !lowerFilename.endsWith(".jpeg") && 
            !lowerFilename.endsWith(".png") &&
            !lowerFilename.endsWith(".webp")) {
            throw new IllegalArgumentException(
                "Extension de fichier invalide. Seuls .jpg, .jpeg, .png, .webp sont acceptés"
            );
        }

        // Vérifier la signature du fichier
        byte[] fileBytes = file.getBytes();
        if (!isValidImageSignature(fileBytes)) {
            throw new IllegalArgumentException(
                "Le fichier n'est pas une image valide (signature invalide)"
            );
        }
    }

    /**
     * Vérifie la signature d'une image
     */
    private boolean isValidImageSignature(byte[] fileBytes) {
        if (fileBytes.length < 4) {
            return false;
        }

        // Vérifier JPEG
        if (fileBytes.length >= JPEG_SIGNATURE.length) {
            boolean isJpeg = true;
            for (int i = 0; i < JPEG_SIGNATURE.length; i++) {
                if (fileBytes[i] != JPEG_SIGNATURE[i]) {
                    isJpeg = false;
                    break;
                }
            }
            if (isJpeg) return true;
        }

        // Vérifier PNG
        if (fileBytes.length >= PNG_SIGNATURE.length) {
            boolean isPng = true;
            for (int i = 0; i < PNG_SIGNATURE.length; i++) {
                if (fileBytes[i] != PNG_SIGNATURE[i]) {
                    isPng = false;
                    break;
                }
            }
            if (isPng) return true;
        }

        return false;
    }

    /**
     * Nettoie le nom de fichier pour éviter les attaques path traversal
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed";
        }
        
        // Supprimer les caractères dangereux
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
