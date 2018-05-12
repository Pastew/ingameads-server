package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertOfferService {

    private final AdvertOfferRepository repo;

    private final UserService userService;

    @Autowired
    public AdvertOfferService(AdvertOfferRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }


    public void addAdvertOffer(AdvertOffer advertOffer) {
        repo.save(advertOffer);
    }

    public List<AdvertOffer> getCurrentUserAdvertOffers() {
        User currentUser = userService.getLoggedUser();
        List<AdvertOffer> offers = repo.findByAdvertGameOwner(currentUser);

        return offers;
    }
}
