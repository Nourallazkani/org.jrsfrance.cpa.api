package org.sjr.babel.web.endpoint.admin;

import org.sjr.babel.web.helper.MailHelper;
import org.sjr.babel.web.helper.MailHelper.MailCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("tasks")
public class TaskEndpoint {

	@Autowired
	private MailHelper mailHelper;
	
	@ResponseStatus(HttpStatus.ACCEPTED)
	@RequestMapping(path = "send-mail", method = RequestMethod.POST)
	public void sendMail(@RequestBody MailCommand command){
		this.mailHelper.send(command);
	}
}