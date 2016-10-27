package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
	/*
	public static String _sha256(String input) {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(input);
	}
 	*/	
	
	public static String sha256(String input) {
		try {
			char[] hexArray = "0123456789abcdef".toCharArray();

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(input.getBytes());

			char[] hexChars = new char[bytes.length * 2];
			for (int j = 0; j < bytes.length; j++) {
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}

			return new String(hexChars);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
