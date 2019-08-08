package com.email.phisher.email;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EmailController {
		
	@Autowired
	public EmailServiceImpl emailService;
	@Value("C:\\Users\\andrew.d.brown\\Pictures\\Screenshots\\screenshot(1).png")
	private String attachmentPath;
	@Autowired
	public SimpleMailMessage template;
	
	@RequestMapping("/")
	public String index(){
		return "index";
	}
	@RequestMapping("/home")
	public String getHome() {
		return "home";
	}
    @RequestMapping(method = RequestMethod.GET)
    public String showEmailsPage() {
        return "emails";
    }
    //Each of the following methods has both a get and a post controller.
    @RequestMapping(value = {"/send", "/sendTemplate", "/sendAttachment"}, method = RequestMethod.GET)
    public String createMail(Model model,
                             HttpServletRequest request) {
        String action = request.getRequestURL().substring(
                request.getRequestURL().lastIndexOf("/") + 1
        );
        Map<String, String> props = labels.get(action);
        Set<String> keys = props.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            model.addAttribute(key, props.get(key));
        }

        model.addAttribute("mailObject", new EmailObject());
        return "mail/send";
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String createMail(Model model,
                             @ModelAttribute("mailObject") @Valid EmailObject mailObject,
                             Errors errors) {
        if (errors.hasErrors()) {
            return "mail/send";
        }
        emailService.sendSimpleMessage(mailObject.getTo(),
                mailObject.getSubject(), mailObject.getText());

        return "redirect:/home";
    }

    @RequestMapping(value = "/sendTemplate", method = RequestMethod.POST)
    public String createMailWithTemplate(Model model,
                             @ModelAttribute("mailObject") @Valid EmailObject emailObject,
                             Errors errors) {
        if (errors.hasErrors()) {
            return "mail/send";
        }
        emailService.sendSimpleMessageUsingTemplate(emailObject.getTo(),
                emailObject.getSubject(),
                template,
                emailObject.getText());

        return "redirect:/home";
    }

    @RequestMapping(value = "/sendAttachment", method = RequestMethod.POST)
    public String createMailWithAttachment(Model model,
                             @ModelAttribute("mailObject") @Valid EmailObject emailObject,
                             Errors errors) {
        if (errors.hasErrors()) {
            return "mail/send";
        }
        emailService.sendMessageWithAttachment(
                emailObject.getTo(),
                emailObject.getSubject(),
                emailObject.getText(),
                attachmentPath
        );

        return "redirect:/home";
    }
	private static final Map<String, Map<String,String>> labels;
	 static {
		 //compose standard email statically.
	        labels = new HashMap<>();

	        //Simple email
	        Map<String, String> props = new HashMap<>();
	        props.put("headerText", "Send Simple Email");
	        props.put("messageLabel", "Message");
	        props.put("additionalInfo", "");
	        labels.put("send", props);

	        //Email with template
	        props = new HashMap<>();
	        props.put("headerText", "Send Email Using Template");
	        props.put("messageLabel", "Template Parameter");
	        props.put("additionalInfo",
	                "The parameter value will be added to the following message template:<br>" +
	                        "<b>This is the test email template for your email:<br>'Template Parameter'</b>"
	        );
	        labels.put("sendTemplate", props);

	        //Email with attachment
	        props = new HashMap<>();
	        props.put("headerText", "Send Email With Attachment");
	        props.put("messageLabel", "Message");
	        props.put("additionalInfo", "To make sure that you send an attachment with this email, change the value for the 'attachment.invoice' in the application.properties file to the path to the attachment.");
	        labels.put("sendAttachment", props);
	    }
}
