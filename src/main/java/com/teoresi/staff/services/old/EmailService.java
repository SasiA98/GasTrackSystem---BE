package com.teoresi.staff.services.old;

import com.teoresi.staff.entities.old.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Set;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(@Lazy UserService userService, JavaMailSender javaMailSender) {
        super();
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }


    public void sendResetPassword(String token, User user) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getResource().getEmail());
            helper.setSubject("Reset Password");
            String messageText = "Hello,<br><br>"
                    + "Your temporary token is: " + token + "<br>"
                    + "You can use this token to change your password.<br><br>"
                    + "The token will expire in 30 minutes.<br><br>"
                    + "You can log in <a href='http://localhost:4200/'>here</a>.<br><br>"
                    + "Thank you!";
            helper.setText(messageText, true);

            javaMailSender.send(message);

            System.out.println("Email sent successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Async
    public void notifyUnauthorizedAccess(User userAttemptedAccess) {

        Set<User> higherAuthorityUsers  = userService.getHigherAuthorityUsers();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("UNAUTHORISED ACCESS");
            String messageText = "Warning!!,<br><br>"
                    + "There are too many incorrect access requests to RAT via this e-mail!: " + userAttemptedAccess.getEmail() + "<br><br>";
            helper.setText(messageText, true);

            for(User user : higherAuthorityUsers){
                helper.setTo(user.getEmail());
                javaMailSender.send(message);
            }

            String msg = "Unauthorized access notified to higher authority users";
            logger.debug(msg);


        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }

    }


    public void sendTempPasswordEmail(String email, String password) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your Temporary Password");
            String messageText = "Hello,<br><br>"
                    + "Your temporary password is: " + password + "<br>"
                    + "Please log in using this password and change it immediately.<br><br>"
                    + "You can log in <a href='http://192.168.126.32:4200/'>here</a>.<br><br>"
                    + "Thank you!";

            helper.setText(messageText, true);

            javaMailSender.send(message);

            System.out.println("Email sent successfully to " + email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
