package com.internship.management.services.registrationService;

import com.internship.management.entities.*;
import com.internship.management.exception.BusinessException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.mappers.RegistrationMapper;
import com.internship.management.repositories.UsersRepository;
import com.internship.management.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Service de gestion des tokens de vérification d'email.
 * 
 * @author Backend Team
 * @version 2.0
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VerificationTokenService {

    private static final int CODE_LENGTH = 6;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int EXPIRATION_MINUTES = 10;

    private final VerificationTokenRepository tokenRepository;
    private final UsersRepository userRepository;
    private final RegistrationMapper registrationMapper;
    private final JavaMailSender mailSender;

    /**
     * Génère un code de vérification aléatoire sécurisé à 6 chiffres.
     *
     * @return le code généré
     */
    public String generateCode() {
        int code = SECURE_RANDOM.nextInt(900000) + 100000; // 6 chiffres (100000-999999)
        return String.valueOf(code);
    }

    /**
     * Crée un token de vérification et l'envoie par email.
     *
     * @param user l'utilisateur pour lequel créer le token
     */
    @Transactional
    public void createAndSendToken(Users user) {
        log.info("Creating verification token for user: {}", user.getEmail());

        String code = generateCode();
        VerificationToken token = registrationMapper.verificationTokenUpdate(code, user);

        tokenRepository.save(token);
        sendEmail(user.getEmail(), code);

        log.info("Verification token created and sent to: {}", user.getEmail());
    }

    /**
     * Renvoie un nouveau token de vérification.
     *
     * @param user l'utilisateur pour lequel renvoyer le token
     */
    @Transactional
    public void resendToken(Users user) {
        log.info("Resending verification token for user: {}", user.getEmail());

        tokenRepository.deleteByUser(user);
        createAndSendToken(user);
    }

    /**
     * Vérifie le code de vérification et active le compte utilisateur.
     *
     * @param email     l'email de l'utilisateur
     * @param inputCode le code saisi par l'utilisateur
     * @return l'utilisateur vérifié
     * @throws ResourceNotFoundException si l'utilisateur ou le token n'existe pas
     * @throws BusinessException         si le code est invalide, expiré ou déjà
     *                                   utilisé
     */
    @Transactional
    public Users verifyCode(String email, String inputCode) {
        log.info("Verifying code for user: {}", email);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        VerificationToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("No verification code found for this user"));

        // Vérifier si le code a déjà été utilisé
        if (token.isUsed()) {
            throw new BusinessException("This verification code has already been used");
        }

        // Vérifier si le code a expiré
        if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Verification code expired for user: {}", email);

            // Générer et envoyer un nouveau code
            String newCode = generateCode();
            VerificationToken newToken = registrationMapper.updateToken(token, newCode);
            tokenRepository.save(newToken);

            sendEmail(email, newCode);
            throw new BusinessException("The verification code has expired. A new code has been sent to your email");
        }

        // Vérifier si le code est correct
        if (!token.getCode().equals(inputCode)) {
            throw new BusinessException("Incorrect verification code");
        }

        // Marquer le token comme utilisé
        token.setUsed(true);
        tokenRepository.save(token);

        // Activer le compte utilisateur
        user.setEmailVerified(true);
        Users verifiedUser = userRepository.save(user);

        log.info("User verified successfully: {}", email);
        return verifiedUser;
    }

    /**
     * Envoie un email avec le code de vérification.
     *
     * @param toEmail l'adresse email du destinataire
     * @param code    le code de vérification
     * @throws BusinessException si l'envoi de l'email échoue
     */
    private void sendEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your account verification code");
            message.setText("Hello,\n\n" +
                    "Here is your verification code: " + code + "\n\n" +
                    "This code will expire in " + EXPIRATION_MINUTES + " minutes.\n\n" +
                    "Best regards,\n" +
                    "Internship Platform Team");

            mailSender.send(message);
            log.debug("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new BusinessException("Failed to send verification email");
        }
    }
}
