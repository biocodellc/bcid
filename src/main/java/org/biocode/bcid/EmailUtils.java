package org.biocode.bcid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Class to send email notifications via Gmail.  If this appears to be a useful class later we can break it down
 * and allow for different email applications.
 */
@Component
public class EmailUtils {
    private final static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    private static final Properties props;
    private final String username;
    private final String password;
    private final String from;

    static {
        // A properties to store mail server smtp information such
        // as the host name and the port number. With this properties
        // we create a Session object from which we'll create the
        // Message object.
        //
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
    }

    @Autowired
    public EmailUtils (BcidProperties props) {
        this.username = props.mailUser();
        this.from = props.mailFrom();
        this.password = props.mailPassword();
    }


    /**
     * send an email to the admin
     *
     * @param subject
     * @param text
     */
    public void sendAdminEmail(String subject, String text) {
        sendEmail(
                username,
                null,
                subject,
                text
        );

    }

    public void sendEmail(String to, String subject, String text) {
        sendEmail(to, null, subject, text);
    }

    /**
     * send an email
     */
    public void sendEmail(String to, String[] cc, String subject, String text) {
        new Thread(() -> {
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            //
            // Message is a mail message to be send through the
            // Transport object. In the Message object we set the
            // sender address and the recipient address. Both of
            // this address is a type of InternetAddress. For the
            // recipient address we can also set the type of
            // recipient, the value can be TO, CC or BCC. In the next
            // two lines we set the email subject and the content text.
            //

            try {
                Address[] ccAddresses = null;
                if (cc != null) {
                    ccAddresses = new InternetAddress[cc.length];

                    for (int i = 0; i < cc.length; i++) {
                        ccAddresses[i] = new InternetAddress(cc[i]);
                    }
                }

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));

                message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
                message.setSubject("[Biocode-Fims Application] " + subject);
                message.setRecipients(Message.RecipientType.CC, ccAddresses);
                message.setText(text);

                //
                // Send the message to the recipient.
                //
                Transport.send(message);
            } catch (MessagingException e) {
                logger.error("MessagingException thrown", e);
            }
        }).start();
    }
}

