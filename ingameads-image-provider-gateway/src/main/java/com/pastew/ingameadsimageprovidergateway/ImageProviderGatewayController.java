package com.pastew.ingameadsimageprovidergateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ImageProviderGatewayController {

    private final RestTemplate restTemplate;

    public ImageProviderGatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/advert/{gameId}")
    public String AskRandomInstanceForPort(@PathVariable String gameId) {
        return restTemplate.getForObject("http://ingameads-image-provider/" + gameId, String.class);
    }
}