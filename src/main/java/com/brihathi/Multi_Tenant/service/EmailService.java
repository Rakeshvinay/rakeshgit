package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;


    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@brihathi.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("Click the link below to reset your password:\n" + resetLink);
            logger.info("Attempting to send password reset email to: {}", toEmail);
            mailSender.send(message);
            logger.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MailException e) {
            logger.error("Failed to send password reset email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
        }
    }

    // public void sendPasswordResetEmail(String userEmail, String resetLink) {
 
    //     try {
    
    //         // 1. Fetch user → get tenantId
    //         User user = userRepository.findByEmail(userEmail)
    //            .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
    //         Long tenantId = user.getTenantId();
     
    //         // 2. Fetch tenant → get tenant-specific email & name
    //         Tenant tenant = tenantRepository.findById(tenantId)
    //                 .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));
    //         String tenantEmail = tenant.getTenantEmail();          // from tenant table    
    //         String tenantName  = tenant.getTenantName();     // replace 'jeeswan'
     
    //         // 3. Prepare email    
    //         SimpleMailMessage message = new SimpleMailMessage();    
    //         message.setFrom(tenantEmail); // tenant's email instead of static mail    
    //         message.setTo(userEmail);    
    //         message.setSubject(tenantName + " - Password Reset Request");     
    //         String emailBody =    
    //                 "Dear user,\n\n" +    
    //                 "You have requested to reset your password for your " + tenantName + " account.\n\n" +    
    //                 "Click the link below to reset your password:\n" + resetLink + "\n\n" +    
    //                 "Thanks,\n" +   
    //                 tenantName + " Support Team";     
    //         message.setText(emailBody);     
    //         logger.info("Attempting to send password reset email from tenant: {} to user: {}", tenantEmail, userEmail);
    //         mailSender.send(message);     
    //         logger.info("Password reset email sent successfully to: {}", userEmail);    
    //     } catch (MailException e) {
    //         logger.error("Failed sending password reset email to: {} Error: {}", userEmail, e.getMessage());
    //         throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
    
    //     }
    
    // }
    
     

    public void sendVerificationEmail(String toEmail, String userName, String verificationLink) {
        try {
            logger.info("Preparing to send verification email to: {}", toEmail);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("noreply@brihathi.com");
            helper.setTo(toEmail);
            helper.setSubject("Welcome to jee swan, " + userName + "! 🎉");

            String htmlContent = """
                <div style='font-family: Arial, sans-serif; color: #222;'>
                  <div style='text-align:center; margin-bottom: 24px;'>
                    <img src='data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD…1R0dMZ+YgvdeOVEalT1dsZ+QkmIwx5RFrQIgwqjAE9RE1r//Z' alt='jeeswan logo' style='height:48px;'/>
                  </div>
                  <h2>Hi %s,</h2>
                  <p>Welcome to <b style='color:#22c55e;'>jeeswan</b> – we're excited to have you on board!</p>
                  <p>Your account has been successfully created, and you're all set to start exploring everything we have to offer.</p>
                  <p style='margin: 32px 0;'>
                    <a href='%s' style='background: #22c55e; color: #fff; padding: 12px 28px; border-radius: 6px; text-decoration: none; font-weight: bold; font-size: 16px;'>Verify your email</a>
                  </p>
                  <p>If you have any questions, feel free to reach out to our support team at any time. We're here to help!</p>
                  <p>Thanks again for joining us.<br>We look forward to being part of your journey.</p>
                  <br>
                  <p>Cheers,<br><b style='color:#22c55e;'>The jeeswan Team</b></p>
                </div>
            """.formatted(userName, verificationLink);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("Verification email sent successfully to: {}", toEmail);
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send verification email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending verification email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Unexpected error while sending verification email: " + e.getMessage(), e);
        }
    }

    // public void sendVerificationEmail(String userEmail, String userName, String verificationLink) {
    //     try {
    //         logger.info("Preparing to send verification email to: {}", userEmail);    
    //         // 1. Fetch User to get tenantId
    
    //         User user = userRepository.findByEmail(userEmail)    
    //                 .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));     
    //         Long tenantId = user.getTenantId();
     
    //         // 2. Fetch Tenant details    
    //         Tenant tenant = tenantRepository.findById(tenantId)    
    //                 .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));     
    //         String tenantEmail = tenant.getTenantEmail();          // sending email    
    //         String tenantName  = tenant.getTenantName();           // brand name (ex: Jeeswan)
    //         String primaryColor = tenant.getPrimaryColor();        // ex: #22c55e
    //         String tenantLogo = tenant.getTenantLogo();            // base64 string stored in DB
     
    //         // 3. Prepare MIME mail    
    //         MimeMessage mimeMessage = mailSender.createMimeMessage();    
    //         MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");     
    //         helper.setFrom(tenantEmail);       // tenant-specific FROM email    
    //         helper.setTo(userEmail);    
    //         helper.setSubject("Welcome to " + tenantName + ", " + userName + "! 🎉");
     
    //         // 4. HTML Email with Tenant Customization    
    //         String htmlContent = """
    // <div style='font-family: Arial, sans-serif; color: #222;'>
    // <div style='text-align:center; margin-bottom: 24px;'>
    // <img src='data:image/png;base64,%s' alt='%s logo' style='height:48px;'/>
    // </div>
    // <h2>Hi %s,</h2>
    // <p>Welcome to <b style='color:%s;'>%s</b> – we're excited to have you on board!</p>
    // <p>Your account has been successfully created and you're all set to explore our platform.</p>
     
    //                   <p style='margin: 32px 0;'>
    // <a href='%s'
    
    //                        style='background: %s; color: #fff; padding: 12px 28px; border-radius: 6px; 
    
    //                        text-decoration: none; font-weight: bold; font-size: 16px;'>
    
    //                        Verify your email
    // </a>
    // </p>
     
    //                   <p>If you have any questions, feel free to reach our support team anytime.</p>
    // <p>Thanks again for joining us.<br>We look forward to being part of your journey.</p>
    // <br>
    // <p>Cheers,<br>
    // <b style='color:%s;'>The %s Team</b>
    // </p>
    // </div>
    
    //                 """.formatted(    
    //                         tenantLogo,    
    //                         tenantName,    
    //                         userName,    
    //                         primaryColor,    
    //                         tenantName,            // brand intro    
    //                         verificationLink,    
    //                         primaryColor,          // button color    
    //                         primaryColor,          // signature color    
    //                         tenantName
    
    //                 );
     
    //         helper.setText(htmlContent, true);     
    //         mailSender.send(mimeMessage);     
    //         logger.info("Verification email sent successfully to: {}", userEmail);     
    //     } catch (Exception e) {    
    //         logger.error("Failed to send tenant-based verification mail: {}", e.getMessage(), e);    
    //         throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
    
    //     }
    
    // }   

}

