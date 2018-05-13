package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdvertOfferController {

    @Autowired
    private AdvertOfferService advertOfferService;

    @Autowired
    private UserService userService;

    @GetMapping("/offers")
    public String getCurrentUserAdvertOffers(Model model) {
        List<AdvertOffer> offers = advertOfferService.getCurrentUserAdvertOffers();
        List<AdvertOffer> offersWaitingForAcceptance = offers.stream()
                .filter(offer -> AdvertOfferStates.WAITING_FOR_GAME_OWNER_ACCEPTANCE.equals(offer.getState()))
                .collect(Collectors.toList());

        List<AdvertOffer> waitingForPaymentOffers = offers.stream()
                .filter(offer -> AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT.equals(offer.getState()))
                .collect(Collectors.toList());

        List<AdvertOffer> waitingForPaymentOffersForCurrentUser = advertOfferService.getAdvertOffersWaitingForPayment();

        model.addAttribute("offers", offersWaitingForAcceptance);
        model.addAttribute("waitingForPaymentOffers", waitingForPaymentOffers);
        model.addAttribute("waitingForPaymentOffersForCurrentUser", waitingForPaymentOffersForCurrentUser);
        return "offers";
    }

    @PostMapping("/offers/{offerId}/accept")
    public String acceptOffer(@PathVariable Long offerId,
                              RedirectAttributes redirectAttributes) {

        try {
            advertOfferService.acceptOffer(offerId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message", "Couldn't accept an advert: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("flash.message", "Accepted advert!");
        return "redirect:/offers";
    }

    @PostMapping("/offers/{offerId}/pay")
    public String payForOffer(@PathVariable Long offerId,
                              RedirectAttributes redirectAttributes) {

        try {
            advertOfferService.payForAdvert(offerId);
            redirectAttributes.addFlashAttribute("flash.message", "Payed advert!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message", "Couldn't pay for advert: " + e.getMessage());
            return "redirect:/offers";
        }
        return "redirect:/offers";
    }
}
