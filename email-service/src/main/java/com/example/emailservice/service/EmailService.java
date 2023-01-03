package com.example.emailservice.service;

import com.example.orderservice.dto.CreateEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @KafkaListener(topics = "emails", groupId = "email")
    public void orderListener(@Payload CreateEmailRequest createEmailRequest){
        sendEmail(createEmailRequest);
    }

    private void sendEmail(CreateEmailRequest createEmailRequest) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@ecommerce.com");
        mailMessage.setTo(createEmailRequest.getEmail());
        mailMessage.setSubject("Order from ecommerce");
        mailMessage.setText(createEmailRequest.getUserName()+" "+createEmailRequest.getUserLastName()+" "+createEmailRequest.getEmail()
                +" "+createEmailRequest.getAddress()+" "+createEmailRequest.getDate().toString()+" "+createEmailRequest.getOrderId()
                +" "+createEmailRequest.getCartItems().toString()+" "+createEmailRequest.getTotalPrice());
        emailSender.send(mailMessage);
    }
}
