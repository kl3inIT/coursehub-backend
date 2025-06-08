package com.coursehub.components;

import com.coursehub.exceptions.auth.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class OtpUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;

    // Tạo OTP ngẫu nhiên
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // OTP 6 chữ số
        return String.valueOf(otp);
    }

    // Gửi OTP qua email
    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Mã xác thực OTP - ITCourseHub");

            String htmlTemplate = getOtpEmailTemplate(otp);
            helper.setText(htmlTemplate, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailSendingException("Failed to send OTP email", e);
        }
    }

    private String getOtpEmailTemplate(String otp) {
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Mã xác thực OTP - ITCourseHub</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        \n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background-color: #f8fafc;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .email-container {\n" +
                "            max-width: 500px;\n" +
                "            margin: 0 auto;\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 16px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);\n" +
                "        }\n" +
                "        \n" +
                "        .header {\n" +
                "            background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);\n" +
                "            padding: 32px 24px;\n" +
                "            text-align: center;\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .logo {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .subtitle {\n" +
                "            font-size: 14px;\n" +
                "            opacity: 0.9;\n" +
                "        }\n" +
                "        \n" +
                "        .content {\n" +
                "            padding: 32px 24px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        \n" +
                "        .greeting {\n" +
                "            font-size: 18px;\n" +
                "            color: #1f2937;\n" +
                "            margin-bottom: 24px;\n" +
                "        }\n" +
                "        \n" +
                "        .otp-section {\n" +
                "            background: linear-gradient(135deg, #f0f4ff 0%, #e0e7ff 100%);\n" +
                "            border-radius: 12px;\n" +
                "            padding: 24px;\n" +
                "            margin: 24px 0;\n" +
                "        }\n" +
                "        \n" +
                "        .otp-label {\n" +
                "            font-size: 14px;\n" +
                "            color: #6b7280;\n" +
                "            margin-bottom: 12px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .otp-code {\n" +
                "            font-size: 32px;\n" +
                "            font-weight: 700;\n" +
                "            color: #4f46e5;\n" +
                "            letter-spacing: 6px;\n" +
                "            margin: 12px 0;\n" +
                "            padding: 16px;\n" +
                "            background: white;\n" +
                "            border-radius: 8px;\n" +
                "            display: inline-block;\n" +
                "            min-width: 200px;\n" +
                "            box-shadow: 0 2px 8px rgba(79, 70, 229, 0.1);\n" +
                "        }\n" +
                "        \n" +
                "        .timer {\n" +
                "            font-size: 13px;\n" +
                "            color: #ef4444;\n" +
                "            font-weight: 600;\n" +
                "            margin-top: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .note {\n" +
                "            background: #fef3cd;\n" +
                "            border: 1px solid #fbbf24;\n" +
                "            border-radius: 8px;\n" +
                "            padding: 16px;\n" +
                "            margin: 20px 0;\n" +
                "            font-size: 14px;\n" +
                "            color: #92400e;\n" +
                "        }\n" +
                "        \n" +
                "        .footer {\n" +
                "            background: #f9fafb;\n" +
                "            padding: 20px 24px;\n" +
                "            text-align: center;\n" +
                "            border-top: 1px solid #e5e7eb;\n" +
                "        }\n" +
                "        \n" +
                "        .company {\n" +
                "            color: #4f46e5;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 16px;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .contact {\n" +
                "            color: #6b7280;\n" +
                "            font-size: 13px;\n" +
                "        }\n" +
                "        \n" +
                "        @media only screen and (max-width: 600px) {\n" +
                "            .email-container {\n" +
                "                margin: 10px;\n" +
                "            }\n" +
                "            \n" +
                "            .header,\n" +
                "            .content,\n" +
                "            .footer {\n" +
                "                padding: 24px 16px;\n" +
                "            }\n" +
                "            \n" +
                "            .otp-code {\n" +
                "                font-size: 28px;\n" +
                "                letter-spacing: 4px;\n" +
                "                min-width: 180px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <!-- Header -->\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">\uD83D\uDCBB ITCourseHub</div>\n" +
                "            <div class=\"subtitle\">Nền tảng học lập trình trực tuyến</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Content -->\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"greeting\">\n" +
                "                Xin chào! \uD83D\uDC4B\n" +
                "            </div>\n" +
                "            \n" +
                "            <p style=\"color: #6b7280; font-size: 15px; margin-bottom: 20px;\">\n" +
                "                Mã xác thực OTP cho tài khoản ITCourseHub của bạn:\n" +
                "            </p>\n" +
                "            \n" +
                "            <!-- OTP Section -->\n" +
                "            <div class=\"otp-section\">\n" +
                "                <div class=\"otp-label\">Mã xác thực</div>\n" +
                "                <div class=\"otp-code\">123456</div>\n" +
                "                <div class=\"timer\">⏰ Có hiệu lực trong 1 phút</div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Security Note -->\n" +
                "            <div class=\"note\">\n" +
                "                \uD83D\uDEE1\uFE0F <strong>Bảo mật:</strong> Không chia sẻ mã này với ai khác\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Footer -->\n" +
                "        <div class=\"footer\">\n" +
                "            <div class=\"company\">ITCourseHub</div>\n" +
                "            <div class=\"contact\">\n" +
                "                \uD83D\uDCE7 support@itcoursehub.com | \uD83C\uDF10 itcoursehub.com\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        return template.replace("123456", otp);
    }

}
