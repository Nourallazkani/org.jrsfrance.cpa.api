package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.entity.Account;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Refugee;
import org.sjr.babel.entity.Volunteer;
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

	private ResponseEntity<Map<String, Object>> successSignIn(int id, String name, Account account) {
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("name", name);
		responseBody.put("profile", account.getAccessKey().substring(0, 1));
		responseBody.put("accessKey", account.getAccessKey());
		responseBody.put("id", id);
		return ResponseEntity.accepted().body(responseBody);
	}
	
	public static class SignUpCommand{
		public String firstName, lastName;
		public String profile;
		public String mailAddress;
		public String password;
	}
	
	@RequestMapping(path = "authz/signUp", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signUp(@RequestBody SignUpCommand input) throws IOException {
		if(!"V".equals(input.profile) && !"R".equals(input.profile)){
			return ResponseEntity.badRequest().build();
		}
		Class<? extends AbstractEntity> targetClass = input.profile=="V" ? Volunteer.class : Refugee.class;
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		long n = objectStore.count(Volunteer.class, "select count(x) from "+targetClass.getName()+" x where x.mailAddress = :mailAddress", args);
		if (n > 0) {
			return ResponseEntity.badRequest().body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
		}
		
		
		Account account = new Account();
		account.setPassword(EncryptionUtil.sha256(input.password));
		account.setAccessKey(input.profile + "-" + UUID.randomUUID().toString());
		
		if(input.profile.equals("V")){
			Volunteer volunteer = new Volunteer();
			volunteer.setFirstName(input.firstName);
			volunteer.setLastName(input.lastName);
			volunteer.setMailAddress(input.mailAddress);
			volunteer.setAccount(account);
			this.objectStore.save(volunteer);
			return successSignIn(volunteer.getId(), volunteer.getFullName(), volunteer.getAccount());
		}
		else{
			Refugee refugee = new Refugee();
			refugee.setFirstName(input.firstName);
			refugee.setLastName(input.lastName);
			refugee.setMailAddress(input.mailAddress);
			refugee.setAccount(account);
			this.objectStore.save(refugee);
			return  successSignIn(refugee.getId(), refugee.getFullName(), refugee.getAccount());
		}
	}

	public static class SignInCommand {
		public String mailAddress, password, accessKey, realm;
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
			return ResponseEntity.badRequest().build();
		}
		if(input.realm.equals("R")){
			Optional<Refugee> _user = tryGetUser(input, Refugee.class);
			if(_user.isPresent()){
				Refugee refugee = _user.get();
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
				System.out.println(volunteer.getAccount().getAccessKey());
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
	
	@RequestMapping(path = "authz/signIn", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signIn(@RequestBody SignInCommand input) {
		
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
