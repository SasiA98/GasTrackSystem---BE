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
import java.text.SimpleDateFormat;
import java.util.Set;

@Service
public class LicenceExpiryEmailService extends BasicService {

    private final JavaMailSender javaMailSender;
    private final Logger logger = LoggerFactory.getLogger(LicenceExpiryEmailService.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public LicenceExpiryEmailService(SessionService sessionService, JavaMailSender javaMailSender) {
        super(sessionService, LoggerFactory.getLogger(LicenceExpiryEmailService.class));
        this.javaMailSender = javaMailSender;
    }

    public void notifyCompanyAboutLicence(CompanyLicence companyLicence) {
        try {
            Set<String> companyEmails = companyLicence.getCompany().getEmails();
            String subject = "Avviso Scadenza";
            String messageText = buildEmailContent(companyLicence);

            for (String email : companyEmails)
                sendEmail(email, subject, messageText);

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
                .append("<p>Salve,</p>")
                .append("<p>La informiamo che il documento <strong>")
                .append(companyLicence.getLicence().getName())
                .append("</strong> relativo all'impianto <strong>")
                .append(companyLicence.getCompany().getName())
                .append("</strong> ha una scadenza fissata per il <strong>")
                .append(formatter.format(companyLicence.getExpiryDate()))
                .append("</strong>.</p>")
                .append("<p>Per maggiori dettagli o per discutere del rinnovo, siamo a sua disposizione.</p>")
                .append("<p>Grazie per l'attenzione.</p>")
                .append("<p style='margin: 0;'>Cordiali saluti,</p>")
                .append("<p style='margin: 0;'>Ing. Francesco Quintaluce</p>")
                .append("<br>")
                .append("<div style='text-align: right; font-size: 12px;'>")
                .append("<p>Cell. 3478421179</p>")
                .append("<p>Via A. Gramsci, 11 - 80040 Volla (NA)</p>")
                .append("<p>e-mail: <a href='mailto:franquinta@libero.it'>franquinta@libero.it</a></p>")
                .append("<p>pec: <a href='mailto:francesco.quintaluce@ordingna.it'>francesco.quintaluce@ordingna.it</a></p>")
                .append("<p>C.F.: QNTFNC72L18F839V - P.IVA: 06814961212</p>")
                .append("</div>")
                .append("</body>")
                .append("</html>");
        return sb.toString();
    }

}
