package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.sjr.babel.entity.Account;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Volunteer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthzEndpoint extends AbstractEndpoint {

	private ResponseEntity<Map<String, String>> successSignIn(String name, Account account) {
		Map<String, String> responseEntity = new HashMap<>();
		responseEntity.put("name", name);
		responseEntity.put("role", account.getRole());
		responseEntity.put("accessKey", account.getAccessKey());
		return ResponseEntity.ok(responseEntity);

	}
	
	public Volunteer volSignUp(@RequestBody Volunteer v) {
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", v.getMailAddress());
		Optional<Volunteer> vol = objectStore.findOne(Volunteer.class, "slect v from Volunteer v where v.mailAddress = :mailAddress", args);
		if(!(vol.isPresent())){
			
		}
	
		return null;
		
	}

	public static class SignInCommand {
		public String userName, password, realm;
	}
	
	
	@RequestMapping(path = "authz/signIn", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signIn(@RequestBody SignInCommand input) {
		String encodedPassword = DigestUtils.sha256Hex(input.password);
		Map<String, Object> args = new HashMap<>();
		args.put("userName", input.userName);
		
		if ("A".equals(input.realm)) {
			String hql = " select a from Administrator a where a.mailAddress = :userName";
			Optional<Administrator> _admin = objectStore.findOne(Administrator.class, hql, args);
			if (_admin.isPresent()) {
				Administrator admin = _admin.get();
				if (admin.getAccount().getPassword().equals(encodedPassword)) {
					return successSignIn(admin.getFullName(), admin.getAccount());
				}
			}
			
		} else if ("O".equals(input.realm)) {

			String hql = " select o from Organisation o where o.mailAddress = :userName";
			Optional<Organisation> _org = objectStore.findOne(Organisation.class, hql, args);
			if (_org.isPresent()) {

				Organisation org = _org.get();
				if (org.getAccount().getPassword().equals(encodedPassword)) {
					return successSignIn(org.getName(), org.getAccount());
				}
			}

		} else if ("V".equals(input.realm)) {
			String hql = "select v from Volunteer v where v.mailAddress = :mailAddress";
			Optional<Volunteer> _vol = objectStore.findOne(Volunteer.class, hql, args);
			if (_vol.isPresent()) {
				Volunteer vol = _vol.get();
				if (vol.getAccount().getPassword().equals(encodedPassword)) {
					return successSignIn(vol.getFullName(), vol.getAccount());
				}
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

}
