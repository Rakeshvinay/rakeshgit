package com.brihathi.Multi_Tenant.serviceimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.brihathi.Multi_Tenant.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    @Value("${authkey.api.key}")
    private String authKey;

    @Value("${authkey.sender.id}")
    private String senderId;

    @Value("${authkey.template.id}")
    private String templateId;

    @Value("${authkey.company.name}")
    private String companyName;

    @Value("${authkey.api.url}")
    private String authUrl;

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    public OtpServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(9000) + 1000); // 4-digit OTP
    }

    @Override
    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        String url = String.format(
            "%s?authkey=%s&mobile=%s&country_code=91&sid=%s&otp=%s",
            authUrl, authKey, phoneNumber, senderId, otp
        );

        // Send OTP via SMS
        restTemplate.getForObject(url, String.class);

        // Store OTP in Redis with TTL of 1 minutes
        redisTemplate.opsForValue().set("OTP:" + phoneNumber, otp, 1, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {
        String key = "OTP:" + phoneNumber;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            redisTemplate.delete(key); // optional: invalidate OTP after use
            return true;
        }
        return false;
    }
}
