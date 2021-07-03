package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ SpringExtension.class })
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void upload66KBFile() {
		testUploadFile(66000);
	}

	private void testUploadFile(int fileSize) {
		TextFileResource textFileResource = new TextFileResource("SingleFile.txt", fileSize);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		HttpHeaders partsHeader = new HttpHeaders();
		partsHeader.setContentType(textFileResource.getMediaType());
		HttpEntity<Resource> part = new HttpEntity<Resource>(textFileResource, partsHeader);
		body.add("file", part);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		RequestCallback requestCallback = restTemplate.httpEntityCallback(new HttpEntity(body, headers));

		ResponseEntity<Map<String, String>> s = testRestTemplate.execute(URI.create("/v1/demo/upload"), HttpMethod.POST, requestCallback, restTemplate.responseEntityExtractor(Map.class));
		Map<String, String> responseBody = s.getBody();

		assertEquals(Integer.toString(fileSize), responseBody.get("uploadedFileSize"));
	}

	private static class TextFileResource extends AbstractResource {
		private String fileName;
		private byte[] fileBytes;

		public TextFileResource(String fileName, int fileSize) {
			this.fileName = fileName;
			this.fileBytes = "A".repeat(fileSize).getBytes();
		}

		public MediaType getMediaType() {
			return MediaType.TEXT_PLAIN;
		}

		@Override
		public String getFilename() {
			return this.fileName;
		}

		@Override
		public String getDescription() {
			return "Test File Resource";
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayResource(fileBytes).getInputStream();
		}
	}
}
