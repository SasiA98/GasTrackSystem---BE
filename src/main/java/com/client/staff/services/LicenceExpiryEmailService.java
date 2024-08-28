package com.client.staff.services;

import com.client.staff.entities.CompanyLicence;
import com.client.staff.security.services.SessionService;
import com.client.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Set;

@Service
public class LicenceExpiryEmailService extends BasicService {

    private final JavaMailSender javaMailSender;
    private final Logger logger = LoggerFactory.getLogger(LicenceExpiryEmailService.class);

    public LicenceExpiryEmailService(SessionService sessionService, JavaMailSender javaMailSender) {
        super(sessionService, LoggerFactory.getLogger(LicenceExpiryEmailService.class));
        this.javaMailSender = javaMailSender;
    }

    public void notifyCompanyAboutLicence(CompanyLicence companyLicence) {
        try {
            Set<String> companyEmails = companyLicence.getCompany().getEmails();
            String subject = "Avviso di Scadenza Licenza";
            String messageText = buildEmailContent(companyLicence);

            for (String email : companyEmails) {
                sendEmail(email, subject, messageText);
            }

            logger.debug("Expiring licence emails have been sent to the clients");

        } catch (Exception e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }


    private void sendEmail(String email, String subject, String messageText) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(messageText, true); // Enable HTML

        javaMailSender.send(message);
    }

    private String buildEmailContent(CompanyLicence companyLicence) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>")
                .append("<body>")
                .append("<p>Gentile Cliente,</p>")
                .append("<p>La licenza <strong>")
                .append(companyLicence.getLicence().getName())
                .append("</strong> scadrà in data <strong>")
                .append(getLocalDate(companyLicence.getExpiryDate()))
                .append("</strong>.</p>")
                .append("<p>La invitiamo a prendere le necessarie azioni per rinnovare la licenza.</p>")
                .append("<p>Grazie per la sua attenzione.</p>")
                .append("<br><p>Distinti saluti,<br>Il Team</p>")
                .append("</body>")
                .append("</html>");
        return sb.toString();
    }

}
