package com.pastew.ingameadsimageprovider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
@Transactional
@Slf4j
public class ImageProviderService {
    private final ImageProviderRepository imageProviderRepository;

    @Autowired
    public ImageProviderService(ImageProviderRepository imageProviderRepository) {
        this.imageProviderRepository = imageProviderRepository;
    }

    public AdvertGameObject getCurrentAdvert(String gameId) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        Advert currentAdvert = imageProviderRepository.findFirstCurrentAdvertByGameId(gameId, currentTimeSeconds);
        if (null == currentAdvert) {
            return new AdvertGameObject("default", 0);
        }
        else return new AdvertGameObject(currentAdvert);
    }

    public Advert[] getAllAdverts(String gameId) {
        return imageProviderRepository.findByGameId(gameId);
    }

    public void saveAdvert(Advert advert) {
        imageProviderRepository.save(advert);
        String adId = String.valueOf(advert.getId());
        System.out.println("Added new ad with id " + adId);
    }
}
