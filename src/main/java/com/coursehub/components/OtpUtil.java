package com.coursehub.components;

import com.coursehub.exceptions.auth.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;

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
    public void sendInvoiceToEmail(Map<String, String> invoiceData, String downloadLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(invoiceData.get("email")); // Customer email
            helper.setSubject("CourseHub Payment Receipt #" + invoiceData.get("orderId"));
            helper.setText("Thank you for your purchase at ITCourseHub! Click the link to download your invoice: <a href='" + downloadLink + "'>Download PDF</a>", true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailSendingException("Failed to send invoice email", e);
        }
    }

    public void sendPasswordToManager(String email, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Tài khoản ITCourseHub - Mật khẩu đăng nhập tạm thời");
            String htmlContent = "<p>Chào quản lý,</p>" +
                    "<p>Tài khoản của bạn trên <b>ITCourseHub</b> đã được tạo.</p>" +
                    "<p><b>Mật khẩu đăng nhập tạm thời:</b> <span style='color:blue;'>" + tempPassword + "</span></p>" +
                    "<p>Vui lòng đăng nhập và đổi mật khẩu mới để đảm bảo an toàn cho tài khoản.</p>" +
                    "<br/><p>Trân trọng,<br/>ITCourseHub Team</p>";
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailSendingException("Failed to send password email", e);
        }
    }

    private String getInvoiceEmailTemplate(Map<String, String> invoiceData){
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Payment Confirmation - ITCourseHub</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        \n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #2c3e50;\n" +
                "            background: #f8fafc;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background: white;\n" +
                "            border-radius: 20px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 10px 30px rgba(0,0,0,0.1);\n" +
                "        }\n" +
                "        \n" +
                "        .header {\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            padding: 40px 30px;\n" +
                "            text-align: center;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        \n" +
                "        .header::after {\n" +
                "            content: '';\n" +
                "            position: absolute;\n" +
                "            bottom: 0;\n" +
                "            left: 0;\n" +
                "            right: 0;\n" +
                "            height: 20px;\n" +
                "            background: white;\n" +
                "            border-radius: 20px 20px 0 0;\n" +
                "            transform: translateY(10px);\n" +
                "        }\n" +
                "        \n" +
                "        .logo {\n" +
                "            font-size: 2.2em;\n" +
                "            font-weight: 700;\n" +
                "            color: white;\n" +
                "            margin-bottom: 15px;\n" +
                "            letter-spacing: -1px;\n" +
                "        }\n" +
                "        \n" +
                "        .success-badge {\n" +
                "            background: rgba(255,255,255,0.2);\n" +
                "            color: white;\n" +
                "            padding: 8px 20px;\n" +
                "            border-radius: 25px;\n" +
                "            font-size: 0.9em;\n" +
                "            font-weight: 500;\n" +
                "            display: inline-block;\n" +
                "            backdrop-filter: blur(10px);\n" +
                "        }\n" +
                "        \n" +
                "        .content {\n" +
                "            padding: 50px 40px;\n" +
                "        }\n" +
                "        \n" +
                "        .success-icon {\n" +
                "            width: 60px;\n" +
                "            height: 60px;\n" +
                "            background: linear-gradient(135deg, #10b981, #059669);\n" +
                "            border-radius: 50%;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            margin: 0 auto 30px;\n" +
                "            box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);\n" +
                "        }\n" +
                "        \n" +
                "        .success-icon::before {\n" +
                "            content: '✓';\n" +
                "            color: white;\n" +
                "            font-size: 1.8em;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        \n" +
                "        .title {\n" +
                "            font-size: 1.8em;\n" +
                "            color: #1e293b;\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 15px;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        \n" +
                "        .subtitle {\n" +
                "            color: #64748b;\n" +
                "            text-align: center;\n" +
                "            font-size: 1.05em;\n" +
                "            margin-bottom: 35px;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        \n" +
                "        .order-card {\n" +
                "            background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);\n" +
                "            border-radius: 15px;\n" +
                "            padding: 30px;\n" +
                "            margin-bottom: 30px;\n" +
                "            border: 1px solid #e2e8f0;\n" +
                "        }\n" +
                "        \n" +
                "        .order-header {\n" +
                "            font-size: 1.2em;\n" +
                "            color: #1e293b;\n" +
                "            font-weight: 600;\n" +
                "            margin-bottom: 20px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .order-header::before {\n" +
                "            content: '\uD83D\uDCCB';\n" +
                "            font-size: 1.1em;\n" +
                "        }\n" +
                "        \n" +
                "        .order-row {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: center;\n" +
                "            padding: 12px 0;\n" +
                "            border-bottom: 1px solid #e2e8f0;\n" +
                "        }\n" +
                "        \n" +
                "        .order-row:last-child {\n" +
                "            border-bottom: none;\n" +
                "            padding-top: 20px;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 1.1em;\n" +
                "        }\n" +
                "        \n" +
                "        .order-label {\n" +
                "            color: #64748b;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .order-value {\n" +
                "            color: #1e293b;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .course-card {\n" +
                "            background: white;\n" +
                "            border: 2px solid #667eea;\n" +
                "            border-radius: 15px;\n" +
                "            padding: 30px;\n" +
                "            margin-bottom: 30px;\n" +
                "            position: relative;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        \n" +
                "        .course-card::before {\n" +
                "            content: '';\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "            right: 0;\n" +
                "            height: 4px;\n" +
                "            background: linear-gradient(90deg, #667eea, #764ba2);\n" +
                "        }\n" +
                "        \n" +
                "        .course-title {\n" +
                "            font-size: 1.4em;\n" +
                "            color: #667eea;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 15px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .course-title::before {\n" +
                "            content: '\uD83C\uDF93';\n" +
                "            font-size: 1.2em;\n" +
                "        }\n" +
                "        \n" +
                "        .course-description {\n" +
                "            color: #64748b;\n" +
                "            margin-bottom: 25px;\n" +
                "            line-height: 1.7;\n" +
                "        }\n" +
                "        \n" +
                "        .course-features {\n" +
                "            display: none;\n" +
                "        }\n" +
                "        \n" +
                "        .next-steps {\n" +
                "            background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);\n" +
                "            border-radius: 15px;\n" +
                "            padding: 25px;\n" +
                "            margin-bottom: 30px;\n" +
                "            border-left: 4px solid #10b981;\n" +
                "        }\n" +
                "        \n" +
                "        .next-steps h3 {\n" +
                "            color: #065f46;\n" +
                "            margin-bottom: 15px;\n" +
                "            font-size: 1.1em;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .next-steps h3::before {\n" +
                "            content: '\uD83D\uDE80';\n" +
                "        }\n" +
                "        \n" +
                "        .next-steps p {\n" +
                "            color: #047857;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        \n" +
                "        .footer {\n" +
                "            background: #1e293b;\n" +
                "            color: #94a3b8;\n" +
                "            text-align: center;\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "        \n" +
                "        .footer-logo {\n" +
                "            color: #667eea;\n" +
                "            font-size: 1.3em;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .footer-text {\n" +
                "            font-size: 0.9em;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        \n" +
                "        .contact-info {\n" +
                "            margin-top: 20px;\n" +
                "            padding-top: 20px;\n" +
                "            border-top: 1px solid #334155;\n" +
                "        }\n" +
                "        \n" +
                "        .contact-item {\n" +
                "            display: inline-block;\n" +
                "            margin: 0 15px;\n" +
                "            color: #667eea;\n" +
                "            font-size: 0.9em;\n" +
                "        }\n" +
                "        \n" +
                "        @media (max-width: 600px) {\n" +
                "            body {\n" +
                "                padding: 10px;\n" +
                "            }\n" +
                "            \n" +
                "            .email-container {\n" +
                "                border-radius: 15px;\n" +
                "            }\n" +
                "            \n" +
                "            .content {\n" +
                "                padding: 30px 25px;\n" +
                "            }\n" +
                "            \n" +
                "            .header {\n" +
                "                padding: 30px 20px;\n" +
                "            }\n" +
                "            \n" +
                "            .logo {\n" +
                "                font-size: 1.8em;\n" +
                "            }\n" +
                "            \n" +
                "            .title {\n" +
                "                font-size: 1.5em;\n" +
                "            }\n" +
                "            \n" +
                "            .course-features {\n" +
                "                grid-template-columns: 1fr;\n" +
                "                gap: 12px;\n" +
                "            }\n" +
                "            \n" +
                "            .order-row {\n" +
                "                flex-direction: column;\n" +
                "                align-items: flex-start;\n" +
                "                gap: 5px;\n" +
                "            }\n" +
                "            \n" +
                "            .contact-item {\n" +
                "                display: block;\n" +
                "                margin: 5px 0;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">ITCourseHub</div>\n" +
                "            <div class=\"success-badge\">Payment Successful</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"success-icon\"></div>\n" +
                "            \n" +
                "            <h1 class=\"title\">Thank you for your purchase!</h1>\n" +
                "            <p class=\"subtitle\">We have received your payment. Below are the details of your order and the course you just purchased.</p>\n" +
                "            \n" +
                "            <div class=\"order-card\">\n" +
                "                <div class=\"order-header\">Order Information</div>\n" +
                "                <div class=\"order-row\">\n" +
                "                    <span class=\"order-label\">Course Name</span>\n" +
                "                    <span class=\"order-value\">Programming Course</span>\n" +
                "                </div>\n" +
                "                <div class=\"order-row\">\n" +
                "                    <span class=\"order-label\">Order ID</span>\n" +
                "                    <span class=\"order-value\">#ICH-2024-001234</span>\n" +
                "                </div>\n" +
                "                <div class=\"order-row\">\n" +
                "                    <span class=\"order-label\">Purchase Date</span>\n" +
                "                    <span class=\"order-value\">June 05, 2025</span>\n" +
                "                </div>\n" +
                "                <div class=\"order-row\">\n" +
                "                    <span class=\"order-label\">Payment Method</span>\n" +
                "                    <span class=\"order-value\">Credit Card</span>\n" +
                "                </div>\n" +
                "                <div class=\"order-row\">\n" +
                "                    <span class=\"order-label\">Total Amount</span>\n" +
                "                    <span class=\"order-value\">$99.00</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"course-card\">\n" +
                "                <h2 class=\"course-title\">Programming Course</h2>\n" +
                "                <p class=\"course-description\">\n" +
                "                    Thank you for trusting and purchasing a course at ITCourseHub. We are committed to providing you with quality knowledge and the best support.\n" +
                "                </p>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"next-steps\">\n" +
                "                <h3>Next Steps</h3>\n" +
                "                <p>Course access information will be sent to your email within 15 minutes.</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <div class=\"footer-logo\">ITCourseHub</div>\n" +
                "            <p class=\"footer-text\">Leading Online Programming Learning Platform</p>\n" +
                "            <div class=\"contact-info\">\n" +
                "                <div class=\"contact-item\">\uD83D\uDCE7 support@itcoursehub.com</div>\n" +
                "            </div>\n" +
                "            <p style=\"margin-top: 15px; font-size: 0.8em;\">\n" +
                "                © 2025 ITCourseHub. This email was sent automatically.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        return template
                .replace("Programming Course",
                        StringEscapeUtils.escapeHtml4(invoiceData.get("courseName")))
                .replace("#ICH-2024-001234",
                        StringEscapeUtils.escapeHtml4(invoiceData.get("orderId")))
                .replace("June 05, 2025",
                        StringEscapeUtils.escapeHtml4(invoiceData.get("purchaseDate")))
                .replace("$99.00",
                        StringEscapeUtils.escapeHtml4(invoiceData.get("totalAmount") + " VND"))
                .replace("Credit Card",
                        StringEscapeUtils.escapeHtml4(invoiceData.get("paymentMethod")));

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
