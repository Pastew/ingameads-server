package com.pastew.ingameadsimageprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageProviderController {

    private final ImageProviderService imageProviderService;

    @Autowired
    public ImageProviderController(ImageProviderService imageProviderService) {
        this.imageProviderService = imageProviderService;
    }

    @RequestMapping("/advert/{gameId}")
    public String getCurrentAdvertImageURL(@PathVariable String gameId) {
        return imageProviderService.getCurrentAdvertImageURL(gameId);
    }
}

