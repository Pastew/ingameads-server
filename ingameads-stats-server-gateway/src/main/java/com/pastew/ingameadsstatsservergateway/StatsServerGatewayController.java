package com.pastew.ingameadsstatsservergateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class StatsServerGatewayController {

    @Autowired
    StatServerGatewayService statServerGatewayService;

    @PostMapping("/upload_stats/{gameId}")
    public void uploadStats(@PathVariable String gameId, @RequestBody AdVisibleObject[] adVisibleObjects) {
        System.out.println(gameId);

        for(AdVisibleObject ad : adVisibleObjects)
            System.out.print(ad);

        statServerGatewayService.uploadStats(gameId, adVisibleObjects);
    }
}
