package ro.onlineshop.userservice.utils.beans;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderBean {

    private final JavaMailSender mailSender;

    public JavaMailSender getMailSender() {
        return mailSender;
    }

}
