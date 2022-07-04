package com.hackathon.lep.offline.utils;

import com.hackathon.lep.offline.configuration.EmailCredentials;
import com.hackathon.lep.offline.model.vo.EmailVO;
import com.sun.mail.imap.IMAPStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;

@Service
public class EmailVerifierUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String FOLDER_INBOX = "INBOX";
	private static final String HOST = "outlook.office365.com";
	private static final int PORT = 993;
	private static final String IMAP_PROTOCOL = "imaps";

	private EmailCredentials emailCredentials;
	private IMAPStore imapStore;

	@Autowired
	public EmailVerifierUtil(EmailCredentials emailCredentials) {
		this.emailCredentials = emailCredentials;
	}

	public List<EmailVO> getNewMessages() throws MessagingException, IOException {
		System.out.println("Verificando novas mensagens.");
		Folder inbox = null;
		try {
			connectEmail();
			inbox = getFolder(FOLDER_INBOX);
			List<EmailVO> emailVOS = readMessagesFromFolder(inbox);
			System.out.println("Sucesso na verificação de novas mensagens");
			return emailVOS;
		} catch (MessagingException | IOException exception) {
			System.out.println("Erro na verificação de novas mensagens: " + exception.getMessage());
			throw exception;
		} finally {
			closeFolder(inbox);
			closeImapStore();
		}
	}

	private void connectEmail() throws MessagingException {
		Session emailSession = Session.getDefaultInstance(new Properties());

		imapStore = (IMAPStore) emailSession.getStore(IMAP_PROTOCOL);
		imapStore.connect(HOST, PORT, emailCredentials.getUsername(), emailCredentials.getPassword());
	}

	private Folder getFolder(String folderName) throws MessagingException {
		Folder inbox = imapStore.getFolder(folderName);

		if (inbox != null)
			inbox.open(Folder.READ_WRITE);

		return inbox;
	}

	private List<EmailVO> readMessagesFromFolder(Folder folder) throws MessagingException, IOException {
		if (folder == null) {
			logger.info("No folder found.");
			return null;
		}

		List<EmailVO> emails = new ArrayList<>();
		Message[] messages = getUnseenMessages(folder);

		if (messages.length == 0) {
			System.out.println("Nenhuma mensagem encontrada.");
		} else {
			
			Integer i = 0;
			for (Message message : messages) {
				Address[] from = message.getFrom();

				
				emails.add(EmailVO.builder().from(((InternetAddress) from[0]).getAddress())
						.subject(message.getSubject()).content(getTextFromMessage(message)).build());

				System.out.println("Email: ");
				System.out.println("- - - - - - - ");
				System.out.println(emails.get(i).toString());
				System.out.println("- - - - - - - ");

				//message.setFlag(Flags.Flag.SEEN, true);
			}
		}

		return emails;
	}
	
	  private String getTextFromMessage(Message message)  {
		  String body = "";
		  try {
			  Object content = message.getContent();
			  if (content instanceof String)
			  {
			        body = (String)content;
			      
			  }
			  else if (content instanceof Multipart)
			  {
				  MimeMultipart mp = (MimeMultipart)content;
			      body = getTextFromMimeMultipart(mp);
			  }
		} catch (Exception e) {
			// TODO: handle exception
		}
		  
		
		 
		  return body;

		}
	  
	   
		private String getTextFromMimeMultipart(
		        MimeMultipart mimeMultipart)   {
			
		    String result = "";
		    try {
			    int count = mimeMultipart.getCount();
			    for (int i = 0; i < count; i++) {
			        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			        if (bodyPart.isMimeType("text/plain")) {
			            result = result + "n" + bodyPart.getContent();
			            break; // without break same text appears twice in my tests
			        } else if (bodyPart.isMimeType("text/html")) {
			            String html = (String) bodyPart.getContent();
			            result = result + "n" + org.jsoup.Jsoup.parse(html).text();
			        } else if (bodyPart.getContent() instanceof MimeMultipart){
			            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
			        }
			    }
				
			} catch (Exception e) {
				// TODO: handle exception
			}
	
		    return result;
		}


	private Message[] getUnseenMessages(Folder folder) throws MessagingException {
		Flags seen = new Flags(Flags.Flag.SEEN);
		FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

		SearchTerm subjectSearchTerm = new SearchTerm() {
			@Override
			public boolean match(Message message) {
				try {
					return message.getSubject().toLowerCase().contains(emailCredentials.getSubject().toLowerCase());
				} catch (MessagingException ex) {
					ex.printStackTrace();
				}
				return false;
			}
		};

		final SearchTerm[] filters = { unseenFlagTerm, subjectSearchTerm };
		final SearchTerm searchTerm = new AndTerm(filters);
		Message[] messages = folder.search(searchTerm);
		return messages;
	}

	private void closeFolder(Folder inbox) throws MessagingException {
		if (inbox != null) {
			inbox.close(false);
		}
	}

	private void closeImapStore() throws MessagingException {
		if (imapStore != null) {
			imapStore.close();
		}
	}
}
