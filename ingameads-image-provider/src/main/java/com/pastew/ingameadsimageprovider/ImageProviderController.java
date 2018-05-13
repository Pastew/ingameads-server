package com.pastew.ingameadsimageprovider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ImageProviderController {

    private final ImageProviderService imageProviderService;

    @Autowired
    public ImageProviderController(ImageProviderService imageProviderService) {
        this.imageProviderService = imageProviderService;
    }

    @GetMapping("/advert/{gameId}")
    public AdvertGameObject getCurrentAdvertImageURL(@PathVariable String gameId) {
        log.info("Somebody asked me about image for game " + gameId);
        AdvertGameObject result = imageProviderService.getCurrentAdvert(gameId);
        log.info("I will return: " + result);
        return result;
    }

    @GetMapping("/allAdverts/{gameId}")
    public Advert[] getAllAdverts(@PathVariable String gameId) {
        log.info("Somebody asked me about all adverts for game " + gameId);
        Advert[] result = imageProviderService.getAllAdverts(gameId);
        log.info("I will return: " + result);
        return result;
    }

    @PostMapping("/{gameId}/saveAdvert")
    public ResponseEntity addAdvert(@RequestBody Advert advert) {
        log.info("Somebody asked me to add advert for game" + advert.getGameId());
        try {
            imageProviderService.saveAdvert(advert);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

