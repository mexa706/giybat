package api.giybat.uz.service;

import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {
    @Value("${spring.mail.username}")
    private String fromAccount;

    @Value("${server.domain}")
    private String serverDomain;


    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ResourceBundleService bundleService;

    public void SendRegistrationEmail(String email, Integer profileId, AppLanguage language) {
        String subject = "Complite Registration";

        String body =  generateHtml().formatted(email,serverDomain, JwtUtil.encode(profileId),language.name());

        sendMimeEmail(email, subject,body);
    }


    public String generateHtml( ) {
        return  "<!DOCTYPE html>\n" +
                "<html lang=\"ru\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Подтверждение регистрации</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea, #764ba2);\n" +
                "            text-align: center;\n" +
                "            padding: 50px;\n" +
                "            color: white;\n" +
                "        }\n" +
                "        .container {\n" +
                "            background: rgba(255, 255, 255, 0.1);\n" +
                "            padding: 30px;\n" +
                "            border-radius: 15px;\n" +
                "            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);\n" +
                "            max-width: 400px;\n" +
                "            margin: auto;\n" +
                "            backdrop-filter: blur(10px);\n" +
                "        }\n" +
                "        h2 {\n" +
                "            color: #fff;\n" +
                "        }\n" +
                "        p {\n" +
                "            color: #ddd;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 14px 28px;\n" +
                "            margin-top: 20px;\n" +
                "            background: #ff0000;\n" +
                "            color: white;\n" +
                "            text-decoration: none;\n" +
                "            font-size: 18px;\n" +
                "            font-weight: bold;\n" +
                "            border-radius: 8px;\n" +
                "            transition: all 0.3s ease;\n" +
                "            box-shadow: 0 4px 10px rgba(255, 0, 0, 0.5);\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            background: #cc0000;\n" +
                "            transform: scale(1.05);\n" +
                "        }\n" +
                "        .button:active {\n" +
                "            transform: scale(0.95);\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 14px;\n" +
                "            opacity: 0.8;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h2>Подтвердите вашу регистрацию</h2>\n" +
                "        <p>Добро пожаловать! Остался всего один шаг. Нажмите на кнопку ниже, чтобы подтвердить ваш email: %s</p>\n" +
                "        <a href=\"" + "%s/auth/registration/email-verification/%s?lang=%s" + "\" class=\"button\">Подтвердить email</a>\n" +
                "        <p class=\"footer\">Если вы не регистрировались, просто проигнорируйте это сообщение.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }


    private void sendMimeEmail(String email, String subject, String body) {

        MimeMessage msg = javaMailSender.createMimeMessage();
        try {
            msg.setFrom(fromAccount);

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);

            CompletableFuture.runAsync(()->{
                javaMailSender.send(msg);
            });

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }

}
