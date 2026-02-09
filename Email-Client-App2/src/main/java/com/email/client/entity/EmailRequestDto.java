package com.email.client.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto {
	
	private Long uid;
	
	private String from;
	
	private String subject;
	
	private Date sentDate;
	
	private boolean read;
	
	private String body;
	

}
