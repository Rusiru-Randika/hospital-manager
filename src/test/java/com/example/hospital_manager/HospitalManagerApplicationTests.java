package com.example.hospital_manager;

import com.example.hospital_manager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HospitalManagerApplicationTests {

	@Autowired
	private JwtService jwtService;

	@Test
	void testJwt() {
		Map<String, Object> extra = new HashMap<>();
		extra.put("role", "ADMIN");
		String token = jwtService.generateToken("admin", extra);
		System.out.println("GENERATED_TOKEN: " + token);
		String username = jwtService.extractUsername(token);
		System.out.println("EXTRACTED_USERNAME: " + username);
		assertEquals("admin", username);

		User userDetails = new User("admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(jwtService.isTokenValid(token, userDetails));
	}

}
