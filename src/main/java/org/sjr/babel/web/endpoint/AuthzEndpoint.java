package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.sjr.babel.entity.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthzEndpoint extends AbstractEndpoint {

	public static class SignInCommand {
		public String userName, password, realm;
	}

	@RequestMapping(path = "authz/signIn", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signIn(@RequestBody SignInCommand input) {
		String encodedPassword = DigestUtils.sha256Hex(input.password);
		if ("A".equals(input.realm)) {

			if (false) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			} else {
				Account a = null;
				Map<String, Object> info = new HashMap<>();
				return ResponseEntity.ok(info);

			}
		} else if ("O".equals(input.realm)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		} else if ("V".equals(input.realm)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		}else {
			return ResponseEntity.badRequest().build();
		}

	}

}
