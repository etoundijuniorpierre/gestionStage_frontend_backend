package com.internship.management.services.registrationService;

import com.internship.management.entities.Users;
import com.internship.management.entities.VerificationToken;
import com.internship.management.exception.BusinessException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.mappers.RegistrationMapper;
import com.internship.management.repositories.UsersRepository;
import com.internship.management.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour VerificationTokenService.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VerificationTokenService Tests")
class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    private Users user;
    private VerificationToken token;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setEmailVerified(false);

        token = new VerificationToken();
        token.setId(1L);
        token.setCode("123456");
        token.setUser(user);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        token.setUsed(false);
    }

    @Test
    @DisplayName("Should generate 6-digit code")
    void testGenerateCode() {
        // When
        String code = verificationTokenService.generateCode();

        // Then
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
        assertThat(Integer.parseInt(code)).isBetween(100000, 999999);
    }

    @Test
    @DisplayName("Should create and send token successfully")
    void testCreateAndSendToken() {
        // Given
        when(registrationMapper.verificationTokenUpdate(anyString(), eq(user))).thenReturn(token);

        // When
        verificationTokenService.createAndSendToken(user);

        // Then
        verify(tokenRepository).save(token);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should resend token successfully")
    void testResendToken() {
        // Given
        when(registrationMapper.verificationTokenUpdate(anyString(), eq(user))).thenReturn(token);

        // When
        verificationTokenService.resendToken(user);

        // Then
        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(token);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should verify code successfully")
    void testVerifyCode_Success() {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));
        when(userRepository.save(user)).thenReturn(user);

        // When
        Users result = verificationTokenService.verifyCode("user@test.com", "123456");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailVerified()).isTrue();
        assertThat(token.isUsed()).isTrue();
        verify(tokenRepository).save(token);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testVerifyCode_UserNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.verifyCode("unknown@test.com", "123456"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when token not found")
    void testVerifyCode_TokenNotFound() {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.verifyCode("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No verification code found");
    }

    @Test
    @DisplayName("Should throw exception when code already used")
    void testVerifyCode_AlreadyUsed() {
        // Given
        token.setUsed(true);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.verifyCode("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already been used");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should send new code when token expired")
    void testVerifyCode_Expired() {
        // Given
        token.setExpirationDate(LocalDateTime.now().minusMinutes(1));
        VerificationToken newToken = new VerificationToken();
        newToken.setCode("654321");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));
        when(registrationMapper.updateToken(eq(token), anyString())).thenReturn(newToken);

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.verifyCode("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expired");

        verify(tokenRepository).save(newToken);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should throw exception when code is incorrect")
    void testVerifyCode_IncorrectCode() {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.verifyCode("user@test.com", "wrong"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Incorrect verification code");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle email sending failure gracefully")
    void testCreateAndSendToken_EmailFailure() {
        // Given
        when(registrationMapper.verificationTokenUpdate(anyString(), eq(user))).thenReturn(token);
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertThatThrownBy(() -> verificationTokenService.createAndSendToken(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Failed to send verification email");

        verify(tokenRepository).save(token);
    }
}
