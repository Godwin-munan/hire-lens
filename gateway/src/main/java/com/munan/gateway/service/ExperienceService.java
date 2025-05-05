package com.munan.gateway.service;

import com.munan.gateway.dto.experience.ExperienceRequest;
import com.munan.gateway.dto.experience.ExperienceResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExperienceService {

    private final RestTemplate restTemplate;
    private final String flaskUrl = "http://localhost:5000/parse";

    public ExperienceService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ExperienceResponse parseResume(String text) {
        ExperienceRequest request = new ExperienceRequest();
        request.setText(text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ExperienceRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(flaskUrl, entity, ExperienceResponse.class);
    }
}
