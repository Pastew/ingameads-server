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

    public String getCurrentAdvertImageURL(String gameId) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        Advert currentAdvert = imageProviderRepository.findFirstCurrentAdvertByGameId(gameId, currentTimeSeconds);

        if (null == currentAdvert)
            return "default";

        return currentAdvert.getImageURL();
    }

    public Advert[] getAllAdverts(String gameId) {
        return imageProviderRepository.findByGameId(gameId);
    }

    public void saveAdvert(Advert advert) {
        Advert ad = new Advert(advert.getGameId(), advert.getImageURL(), advert.getStartDate(), advert.getEndDate());
        imageProviderRepository.save(ad);
        String adId = String.valueOf(ad.getId());
        System.out.println("Added new ad with id " + adId);
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageProviderRepository imageProviderRepository) {
        return args -> {
            imageProviderRepository.save(new Advert("com.pastew.example_game_1", "http://i.dailymail.co.uk/i/pix/2013/08/31/article-2407890-1B8CE809000005DC-88_634x374.jpg", 1525549164, 1526067561));
            imageProviderRepository.save(new Advert("com.pastew.example_game_1", "http://www.mcspotlight.org/campaigns/current/subverts/subvert_pix/balance.jpg", 1526067562, 152624036));
            imageProviderRepository.save(new Advert("com.pastew.example_game_2", "https://www.hellomagazine.com/imagenes/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/0-213-643/kfc-ad-t.jpg", 1526067561, 1526240361));
        };
    }
}
