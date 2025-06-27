package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.OtpEntity;
import org.OwlsGame.backend.dao.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender emailSender;

    // 生成随机OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    // 创建并发送OTP
    @Transactional
    public void createAndSendOtp(String email) {
        // 先删除该邮箱的旧OTP
        otpRepository.deleteByEmail(email);

        // 生成新OTP
        String otpValue = generateOtp();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(OTP_EXPIRY_MINUTES);

        // 保存OTP到数据库
        OtpEntity otpEntity = new OtpEntity(email, otpValue, now, expiryTime);
        otpRepository.save(otpEntity);

        // 发送OTP到邮箱
        sendOtpEmail(email, otpValue);
    }

    // 发送OTP到邮箱
    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");

        emailSender.send(message);
    }

    // 验证OTP
    @Transactional
    public boolean validateOtp(String email, String otp) {
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp);

        if (otpEntityOpt.isPresent()) {
            OtpEntity otpEntity = otpEntityOpt.get();

            // 检查OTP是否已过期
            if (otpEntity.isExpired()) {
                return false;
            }

            // 标记OTP为已使用
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);

            return true;
        }

        return false;
    }
}