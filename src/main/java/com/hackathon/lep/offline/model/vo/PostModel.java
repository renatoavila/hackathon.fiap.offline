package com.hackathon.lep.offline.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PostModel {

	private String email;
	private String texto;
	
	public PostModel(String email, String texto) {
		super();
		this.email = email.trim();
		this.texto = texto.trim();
	}
	
	
}
