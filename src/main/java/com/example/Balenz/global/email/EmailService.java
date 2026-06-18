package com.example.Balenz.global.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /** 이메일 전송 */
    public void sendEmail(String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("team.balenz@gmail.com");
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

}
