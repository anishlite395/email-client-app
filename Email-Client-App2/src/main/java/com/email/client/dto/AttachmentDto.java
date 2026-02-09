package com.email.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttachmentDto {
	
	private String fileName;
	private byte[] content;
	private String mimeType;

}
