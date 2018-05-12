package com.pastew.ingameadsui.Advert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertOfferService {

    private final AdvertOfferRepository repo;

    @Autowired
    public AdvertOfferService(AdvertOfferRepository repo) {
        this.repo = repo;
    }

    public void addAdvertOffer(AdvertOffer advertOffer) {
        repo.save(advertOffer);
    }
}
