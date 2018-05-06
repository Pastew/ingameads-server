package com.pastew.ingameadsimageprovider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ImageProviderController {

    private final ImageProviderService imageProviderService;

    @Autowired
    public ImageProviderController(ImageProviderService imageProviderService) {
        this.imageProviderService = imageProviderService;
    }

    @RequestMapping("/advert/{gameId}")
    public String getCurrentAdvertImageURL(@PathVariable String gameId) {
        log.info("Somebody asked me about image for game " + gameId);
        String result =  imageProviderService.getCurrentAdvertImageURL(gameId);
        log.info("I will return: " + result);
        return result;
    }
}

