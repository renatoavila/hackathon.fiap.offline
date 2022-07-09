package com.hackathon.lep.offline.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.lep.offline.model.vo.EmailVO;
import com.hackathon.lep.offline.model.vo.PostModel;
import com.hackathon.lep.offline.utils.EmailVerifierUtil;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

	@Value("${url}")
	private String url;
	
	@Autowired
	private PostService postService;

	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EmailVerifierUtil emailVerifierUtil;

    @Autowired
    public EmailService(EmailVerifierUtil emailVerifierUtil) {
        this.emailVerifierUtil = emailVerifierUtil;
    }

    @Scheduled(cron = "*/10 * * * * *") // default: every 30 seconds
    public void getEmailsNeverSeen() throws MessagingException, IOException {
    	System.out.println("inicio");
        List<EmailVO> newMessages = emailVerifierUtil.getNewMessages();
        System.out.println("Encontrou: " + newMessages.size() + " novas mensagens(s)!");
        System.out.println("fim");
        
        for (EmailVO message : newMessages) {
        	PostModel postModel = new PostModel(message.getFrom(), message.getContent());
        	postService.PostApi(postModel);
        }
        
    }
}