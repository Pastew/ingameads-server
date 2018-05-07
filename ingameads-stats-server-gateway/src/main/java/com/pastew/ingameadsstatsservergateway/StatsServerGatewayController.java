package com.pastew.ingameadsstatsservergateway;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class StatsServerGatewayController {

    private final RestTemplate restTemplate;

    public StatsServerGatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/upload_stats/{gameId}")
    public void uploadStats(@PathVariable String gameId, @RequestBody AdVisibleObject[] adVisibleObject) {
        //return restTemplate.getForObject("http://ingameads-stats-server/upload_stats/" + gameId, String.class);
        System.out.println(gameId);
        for(AdVisibleObject ad : adVisibleObject)
            System.out.print(ad);
    }
}
