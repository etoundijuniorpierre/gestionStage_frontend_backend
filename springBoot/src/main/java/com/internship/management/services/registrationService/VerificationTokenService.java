package com.internship.management.services.registrationService;

import com.internship.management.entities.*;
import com.internship.management.mappers.RegistrationMapper;
import com.internship.management.repositories.UsersRepository;
import com.internship.management.repositories.VerificationTokenRepository;
import com.internship.management.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UsersRepository userRepository;
    private final RegistrationMapper registrationMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public String generateCode() {
        return String.valueOf(new Random().nextInt(90000) + 10000);
    }

    @Transactional
    public void createAndSendToken(Users user) {

        String code = generateCode();
        VerificationToken token = registrationMapper.verificationTokenUpdate(code, user);

        tokenRepository.save(token);
        sendEmail(user.getEmail(), code);
    }

    @Transactional
    public void resendToken(Users user) {
        tokenRepository.deleteByUser(user);
        createAndSendToken(user);
    }

    public Users verifyCode(String email, String inputCode) {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        VerificationToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("code not found with email: " + email));

        if (token.isUsed()) {
            throw new RuntimeException("This code has already been used.");
        }

        if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
            String newCode = generateCode();
            VerificationToken newToken = registrationMapper.updateToken(token, newCode);
            tokenRepository.save(newToken);

            sendEmail(email, newCode);
            throw new RuntimeException("The code has expired. A new code has been sent to you.");
        }

        if (!token.getCode().equals(inputCode)) {
            throw new RuntimeException("incorrect code.");
        }

        token.setUsed(true);
        tokenRepository.save(token);

        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIF);
        return userRepository.save(user);
    }

    private void sendEmail(String toEmail, String code) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("email", senderEmail, "name", senderName));
        body.put("to", List.of(Map.of("email", toEmail)));
        body.put("subject", "Votre code de vérification");
        body.put("htmlContent", "<html><body><p>Bonjour,</p><p>Voici votre code de vérification : <b>" + code
                + "</b></p><p>Cordialement,<br>L'équipe Internship Platform</p></body></html>");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur lors de l'envoi de l'email : " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Échec de la connexion à l'API Brevo : " + e.getMessage());
        }
    }
}
