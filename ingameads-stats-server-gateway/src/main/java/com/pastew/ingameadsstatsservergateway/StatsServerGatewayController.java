package com.pastew.ingameadsstatsservergateway;

import com.pastew.ingameadsstatsservergateway.model.AdVisibleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/adVisibleObjects/{gameId}")
    public List<AdVisibleObject> adVisibleObjects(@PathVariable String gameId){
        return statServerGatewayService.getAdVisibleObjects(gameId);
    }
}
