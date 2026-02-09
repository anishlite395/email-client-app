package com.email.client.jwt.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	public String encrypt(String raw) {
		return Base64.getEncoder()
				.encodeToString(raw.getBytes(StandardCharsets.UTF_8));
	}
	
	public String decrypt(String encrypted) {
		return new String(Base64.getDecoder().decode(encrypted),StandardCharsets.UTF_8);
	}
	
	
}
