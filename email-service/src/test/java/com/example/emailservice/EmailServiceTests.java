package com.example.emailservice;

import com.example.emailservice.service.EmailService;
import com.example.orderservice.dto.CreateEmailRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender javaMailSender;

    @Test
    public void itShouldSendEmail(){
        CreateEmailRequest createEmailRequest = CreateEmailRequest.builder().
                orderId("1").userName("test").userLastName("test").email("test@gmail.com").address("test")
                .cartItems(new HashSet<>()).date(new Date()).build();

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(createEmailRequest);
    }
}
