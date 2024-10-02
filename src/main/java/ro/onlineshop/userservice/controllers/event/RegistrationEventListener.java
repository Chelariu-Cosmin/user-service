package ro.onlineshop.userservice.controllers.event;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ro.onlineshop.api.payload.request.EmailRequest;
import ro.onlineshop.userservice.entities.RefreshToken;
import ro.onlineshop.userservice.entities.User;
import ro.onlineshop.userservice.services.UserService;
import ro.onlineshop.userservice.utils.beans.MailSenderBean;

import java.io.UnsupportedEncodingException;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationEventListener implements ApplicationListener<RegistrationEvent> {

    private final UserService userService;

    private final MailSenderBean mailSender;

    private User user;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void onApplicationEvent(RegistrationEvent event) {
        user = event.getUser();

        RefreshToken refreshToken = userService.generateTokenSendingEmail(user);
        String jwt = refreshToken.getToken();

        String url = event.getUrl() + "/users/verifyEmail?token=" + jwt;
        //5. Send the email.
        try {
            sendVerificationEmail(url);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration :  {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {

        var email = new EmailRequest();
        email.setSubject("Confirmation email");
        email.setSenderName("Your Online Shop");
        email.setMailContent("<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a><br>" +
                "<p> Thank you <br> Best regards! ");

        MimeMessage message = mailSender.getMailSender().createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(sender, email.getSenderName());
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getMailContent(), true);
        mailSender.getMailSender().send(message);
    }
}
