package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.infra.mail.ResendEmailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final ResendEmailSender emailSender;

    public MailService(ResendEmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;
        String subject = "Please verify your email";
        String content = """
                <h1> Welcome, %s!</h1>
                <p>Please click the link below to verify your account:</p>
                <a href="%s">Verify Account</a>
                """.formatted(user.getEmail(), verifyLink);

        emailSender.sendEmail(user.getEmail(), subject, content);
    }

    public void sendResetPasswordEmail(User user, String token) {
        String resetLink = "http://localhost:8080/api/auth/reset?token=" + token;
        String subject = "Reset your password";
        String content = """
                <h1> Hello, %s!</h1>
                <p>Please click the link below to reset your password:</p>
                <a href="%s">Reset Password</a>
                """.formatted(user.getEmail(), resetLink);

        emailSender.sendEmail(user.getEmail(), subject, content);
    }
}
