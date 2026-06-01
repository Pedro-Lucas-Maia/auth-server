package com.PedroMaia.auth_server.infra.mail;

public interface EmailSender {
    void sendEmail(String to, String subject, String content);
}
