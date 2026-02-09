package com.email.client.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentMailDto {
	
	private Long uid;
	
	private String from;
	
	private String to;
	
	private String subject;
	
	private String body;
	
	private Date sentDate;
	
	private List<AttachmentDto> attachments;

}
