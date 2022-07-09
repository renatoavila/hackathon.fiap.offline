package com.hackathon.lep.offline.service;
 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hackathon.lep.offline.model.vo.PostModel;
 

@Service
public class PostService {

	@Value("${url}")
	private String url;
	

	public void PostApi(PostModel post)
	{
		RestTemplate restTemplate = new RestTemplate(); 
	      
	    restTemplate.postForEntity(url, post, String.class);
	    
	}

}
