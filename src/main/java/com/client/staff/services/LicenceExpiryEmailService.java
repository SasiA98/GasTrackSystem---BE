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
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(companyLicence.getCompany().getEmail());

            helper.setSubject("La Licenza sta per scadere!!");
            String messageText = "Messaggio di prova per notificare che " +
                    "la licenza : " + companyLicence.getLicence().getName() +
                    " scadrà in data " + getLocalDate(companyLicence.getExpiryDate()) + "!!" + "<br><br>";
            helper.setText(messageText, true);

            javaMailSender.send(message);

            String msg = "Expiring licence email has been set to the client";
            logger.debug(msg);


        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }

    }

}
