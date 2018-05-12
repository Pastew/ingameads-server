package com.pastew.ingameadsui.Advert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdvertOfferController {

    @Autowired
    private AdvertOfferService advertOfferService;

    @GetMapping("/offers")
    public String getCurrentUserAdvertOffers(Model model){
        List<AdvertOffer> advertOffer = advertOfferService.getCurrentUserAdvertOffers();
        model.addAttribute("offers", advertOffer);
        return "offers";
    }
}
