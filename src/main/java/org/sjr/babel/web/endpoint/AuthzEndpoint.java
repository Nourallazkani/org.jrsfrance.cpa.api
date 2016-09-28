package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.entity.Account;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Refugee;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.web.helper.MailHelper;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import util.EncryptionUtil;

@RestController
public class AuthzEndpoint extends AbstractEndpoint {

	@Autowired
	private MailHelper mailHelper;
	
	private ResponseEntity<Map<String, Object>> successSignIn(int id, String name, Account account) {
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("name", name);
		responseBody.put("profile", account.getAccessKey().substring(0, 1));
		responseBody.put("accessKey", account.getAccessKey());
		responseBody.put("id", id);
		return ResponseEntity.ok(responseBody);
	}


	public static class SignInCommand {
		public String mailAddress,password,realm;
		public String accessKey;
	}

	
	public boolean successfulSignIn(SignInCommand command, Account userAccount){
		if(command==null || (command.password==null && command.accessKey==null) || userAccount==null){
			return false;
		}
		else if(StringUtils.hasText(command.password)){
			String encodedPassword = EncryptionUtil.sha256(command.password);
			return encodedPassword.equals(userAccount.getPassword());
			
		}
		else if (StringUtils.hasText(command.accessKey)){
			return userAccount.getAccessKey().equals(command.accessKey);
		}
		return false;
	}
	
	private <T extends AbstractEntity> Optional<T> tryGetUser(SignInCommand input, Class<T> clazz){
		if(input.realm==null && StringUtils.hasText(input.accessKey)){
			input.realm = input.accessKey.substring(0, 1);
		}
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		args.put("accessKey", input.accessKey);
		String templateQuery = "select x from %s x where (:mailAddress is not null and x.mailAddress = :mailAddress) or (:accessKey is not null and x.account.accessKey = :accessKey)";
		return objectStore.findOne(clazz, String.format(templateQuery, clazz.getSimpleName()), args);
		
	}
	
	@RequestMapping(path = "authz/passwordRecovery", method = RequestMethod.POST)
	public ResponseEntity<?> passwordRecovery(@RequestBody SignInCommand input){
		if (!StringUtils.hasText(input.realm) || !StringUtils.hasText(input.mailAddress)) {
			Map<String, String> errors = new HashMap<>();
			if(input.mailAddress == null) errors.put("mailAddress", null);
			if(input.realm == null) errors.put("realm", null);
			return badRequest(errors);
		}
		if(input.realm.equals("R")){
			Optional<Refugee> _user = tryGetUser(input, Refugee.class);
			if(_user.isPresent()){
				Refugee refugee = _user.get();
				this.mailHelper.send(MailType.REFUGEE_RESET_PASSWORD, "fr", refugee.getMailAddress(), refugee.getAccount().getAccessKey());
				System.out.println(refugee.getAccount().getAccessKey());
			}
		}
		else if(input.realm.equals("O")){
			Optional<Organisation> _user = tryGetUser(input, Organisation.class);
			if(_user.isPresent()){
				Organisation organisation = _user.get();
				System.out.println(organisation.getAccount().getAccessKey());
			}
		}
		else if(input.realm.equals("V")){
			Optional<Volunteer> _user = tryGetUser(input, Volunteer.class);
			if(_user.isPresent()){
				Volunteer volunteer = _user.get();
				this.mailHelper.send(MailType.VOLUNTEER_RESET_PASSWORD, "fr", volunteer.getMailAddress(), volunteer.getAccount().getAccessKey());
			}
		}
		else if(input.realm.equals("A")){
			Optional<Administrator> _user = tryGetUser(input, Administrator.class);
			if(_user.isPresent()){
				Administrator admin = _user.get();
				System.out.println(admin.getAccount().getAccessKey());
			}
		}
		return ResponseEntity.accepted().build();
	}
	
	@RequestMapping(path = "authentication", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signIn(@RequestBody SignInCommand input) {
		if(input.accessKey==null && (input.mailAddress==null || input.password==null || input.realm==null)){
			Map<String, String> errors = new HashMap<>();
			if(!StringUtils.hasText(input.mailAddress)) errors.put("mailAddress", "_");
			if(!StringUtils.hasText(input.password)) errors.put("password", "_");
			if(!StringUtils.hasText(input.realm)) errors.put("realm", "_");
			return badRequest(errors);
		}
		
		if(input.realm==null && StringUtils.hasText(input.accessKey)){
			input.realm = input.accessKey.substring(0, 1);
		}

		if(input.realm.equals("R")){
			Optional<Refugee> _user = tryGetUser(input, Refugee.class);
			if(_user.isPresent()){
				Refugee refugee = _user.get();
				if (successfulSignIn(input, refugee.getAccount())) {
					return successSignIn(refugee.getId(), refugee.getFullName(), refugee.getAccount());
				}
			}
		}
		else if(input.realm.equals("O")){
			Optional<Organisation> _user = tryGetUser(input, Organisation.class);
			if(_user.isPresent()){
				Organisation organisation = _user.get();
				if (successfulSignIn(input, organisation.getAccount())) {
					return successSignIn(organisation.getId(), organisation.getName(), organisation.getAccount());
				}
			}
		}
		else if(input.realm.equals("V")){
			Optional<Volunteer> _user = tryGetUser(input, Volunteer.class);
			if(_user.isPresent()){
				Volunteer volunteer = _user.get();
				if (successfulSignIn(input, volunteer.getAccount())) {
					return successSignIn(volunteer.getId(),  volunteer.getFullName(), volunteer.getAccount());
				}
			}
		}
		else if(input.realm.equals("A")){
			Optional<Administrator> _user = tryGetUser(input, Administrator.class);
			if(_user.isPresent()){
				Administrator admin = _user.get();
				if (successfulSignIn(input, admin.getAccount())) {
					return successSignIn(admin.getId(), admin.getFullName(), admin.getAccount());
				}
			}
		}		
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
