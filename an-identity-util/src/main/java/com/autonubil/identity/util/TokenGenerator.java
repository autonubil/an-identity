package com.autonubil.identity.util;

import java.security.SecureRandom;

public class TokenGenerator {

	private SecureRandom random;

	private static TokenGenerator instance;

	// public static String goodChars =
	// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_=";
	public static String goodChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzQQ";
	public static String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final long step = -1 + (0x1 << (31-Integer.numberOfLeadingZeros(goodChars.length())));
	public static final long mask = ~(step);

	public TokenGenerator() {
		random = new SecureRandom();
	}

	private static TokenGenerator getInstance() {
		if (instance == null) {
			instance = new TokenGenerator();
		}
		return instance;
	}

	private String getToken0(int length) {
		String out = "";
		while (out.length() < length) {
			out = out + pack(random.nextLong(), 20);
		}
		return out.substring(0, length);
	}

	public static String getToken() {
		return getInstance().getToken0(12);
	}

	public static String getToken(int length) {
		return getInstance().getToken0(length);
	}

	private static String pack(long in, int maxLength) {
		String s = "";
		long remainder = Math.abs(in);
		int b = 63;
		while (true) {
			int a = (int) (remainder & step);
			remainder = remainder >> step;
			b = (int)(b-step);
			s = goodChars.charAt(a) + s;
			if (s.length() >= maxLength || (b < step)) {
				break;
			}
		}
		return s;
	}
	
}