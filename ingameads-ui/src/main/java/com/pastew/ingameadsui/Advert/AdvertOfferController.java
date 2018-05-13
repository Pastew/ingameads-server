package com.pastew.ingameadsui.Advert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/offers/{offerId}/accept")
    public String acceptOffer(@PathVariable Long offerId,
                              RedirectAttributes redirectAttributes){

        try {
            advertOfferService.acceptOffer(offerId);
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("flash.message", "Couldn't accept an advert: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("flash.message", "Accepted advert!");
        return "redirect:/offers";
    }
}
