package util;

import org.apache.commons.codec.digest.DigestUtils;

public class EncryptionUtil {
	
	public static String sha256(String input){
		return DigestUtils.sha256Hex(input);
	}

}
