package com.smart.rides.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // <-- ADDED THIS FOR HTML TEMPLATES

    // This method sends a simple, plain text email
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("your_email@gmail.com"); // Set the sender email here
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        System.out.println("Plain text email sent to: " + to);
    }

    // This is the new method that sends a rich HTML email
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom("your_email@gmail.com"); // Set the sender email here
            helper.setTo(to);
            helper.setSubject(subject);

            // Process the HTML template with the given data
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true); // The 'true' flag indicates HTML content

            mailSender.send(mimeMessage);
            System.out.println("HTML email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Error sending HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- New method for accepted ride email ---
    public void sendRideAcceptedEmail(String toEmail, String passengerName, String driverName, String origin, String destination, java.time.LocalDateTime departureTime) {
        Context context = new Context();
        context.setVariable("passengerName", passengerName);
        context.setVariable("driverName", driverName);
        context.setVariable("rideOrigin", origin);
        context.setVariable("rideDestination", destination);
        context.setVariable("departureTime", departureTime);
        sendHtmlEmail(toEmail, "Your Ride Has Been Accepted!", "ride-accepted-notification", context);
    }

    // --- New method for rejected ride email ---
    public void sendRideRejectedEmail(String toEmail, String passengerName, String driverName, String origin, String destination) {
        Context context = new Context();
        context.setVariable("passengerName", passengerName);
        context.setVariable("driverName", driverName);
        context.setVariable("rideOrigin", origin);
        context.setVariable("rideDestination", destination);
        sendHtmlEmail(toEmail, "Your Ride Has Been Rejected", "ride-rejected-notification", context);
    }

    // --- New method for passenger final booking confirmation ---
    public void sendFinalConfirmationEmail(String toEmail, String passengerName, String driverName, String origin, String destination, LocalDateTime departureTime, double fare) {
        Context context = new Context();
        context.setVariable("passengerName", passengerName);
        context.setVariable("driverName", driverName);
        context.setVariable("rideOrigin", origin);
        context.setVariable("rideDestination", destination);
        context.setVariable("departureTime", departureTime);
        context.setVariable("fare", fare);
        sendHtmlEmail(toEmail, "Your Booking is Confirmed!", "final-confirmation", context);
    }

    // --- New method for driver final booking notification ---
    public void sendDriverFinalNotification(String toEmail, String driverName, String passengerName, String origin, String destination, double fare) {
        Context context = new Context();
        context.setVariable("driverName", driverName);
        context.setVariable("passengerName", passengerName);
        context.setVariable("rideOrigin", origin);
        context.setVariable("rideDestination", destination);
        context.setVariable("fare", fare);
        sendHtmlEmail(toEmail, "Payment Received! Ride Confirmed!", "driver-final-notification", context);
    }
}