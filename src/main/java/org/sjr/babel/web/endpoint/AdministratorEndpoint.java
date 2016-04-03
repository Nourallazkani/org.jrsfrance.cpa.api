package org.sjr.babel.web.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorEndpoint extends AbstractEndpoint {

	class AdministratorSummary {
		public int id;
		public String role, civility, firstName, lastName, mailAddress, phoneNumber;

		public AdministratorSummary(Administrator ad) {
			this.id = ad.getId();
			this.role = ad.getAccount().getRole();
			this.civility = ad.getCivility().getName();
			this.firstName = ad.getFirstName();
			this.lastName = ad.getLastName();
			this.mailAddress = ad.getMailAddress();
			this.phoneNumber = ad.getPhoneNumber();
		}
	}

	@RequestMapping(path = "/administrators", method = RequestMethod.GET)
	@Transactional
	public List<AdministratorSummary> getAdminsSummary() {
		return objectStore.find(Administrator.class, "select a from Administrator a").stream()
				.map(x -> AdministratorSummary(x)).collector(Collectors.toList());

	}

}
