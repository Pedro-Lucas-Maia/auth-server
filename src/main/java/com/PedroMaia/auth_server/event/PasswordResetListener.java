package com.PedroMaia.auth_server.event;

import com.PedroMaia.auth_server.service.MailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordResetListener {
    private final MailService mailService;

    public PasswordResetListener(MailService mailService) {
        this.mailService = mailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePasswordReset(OnPasswordResetEvent event) {
        mailService.sendResetPasswordEmail(event.user(), event.token());
    }
}
