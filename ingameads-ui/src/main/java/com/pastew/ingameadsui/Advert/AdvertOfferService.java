package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import com.pastew.ingameadsui.Payment.PaymentService;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdvertOfferService {

    private final AdvertOfferRepository repo;

    private final UserService userService;

    private final PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    public AdvertOfferService(AdvertOfferRepository repo, UserService userService, PaymentService paymentService) {
        this.repo = repo;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    public void addAdvertOffer(AdvertOffer advertOffer) {
        repo.save(advertOffer);
    }

    public List<AdvertOffer> getCurrentUserAdvertOffers() {
        User currentUser = userService.getLoggedUser();
        List<AdvertOffer> offers = repo.findByAdvertGameOwner(currentUser);

        return offers;
    }


    public List<AdvertOffer> getCurrentUserAdvertsWaitingForPayment() {
        User currentUser = userService.getLoggedUser();
        List<AdvertOffer> offers = repo.findByBuyer(currentUser);

        List<AdvertOffer> waitingForPaymentOffers = offers.stream()
                .filter(offer -> AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT.equals(offer.getState()))
                .collect(Collectors.toList());

        return waitingForPaymentOffers;
    }

    public List<AdvertOffer> getAdvertOffersWaitingForPayment() {
        User currentUser = userService.getLoggedUser();
        List<AdvertOffer> offers = repo.findByBuyer(currentUser);
        List<AdvertOffer> waitingForPaymentOffers = offers.stream()
                .filter(offer -> AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT.equals(offer.getState()))
                .collect(Collectors.toList());
        return waitingForPaymentOffers;
    }

    public void acceptOffer(long offerId) {
        AdvertOffer offer = repo.findById(offerId).get();
        offer.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
        repo.save(offer);
    }

    public String payForAdvertOffer(AdvertOffer offer) throws AdvertBuyException {
        Map paypalResponse = paymentService.pay(offer);
        String paypalUrl = (String) paypalResponse.get(PaymentService.PAYPAL_URL_KEY);

        return paypalUrl;
    }

    public void notifyImageProviderAboutNewAdvert(Long offerId) throws AdvertBuyException {
        AdvertOffer offer = repo.findById(offerId).get();
        Advert ad = offer.getAdvert();

        // Image Provider database
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<AdvertPostRequestObject> request = new HttpEntity<>(new AdvertPostRequestObject(ad), requestHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://ingameads-image-provider/" + ad.getGame().getId() + "/saveAdvert/",
                request, String.class);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new AdvertBuyException("Failed to pay for advert");

        offer.setState(AdvertOfferStates.PAYED);
        repo.save(offer);
    }

    public AdvertOffer findById(Long offerId) {
        return repo.findById(offerId).get();
    }

    private class AdvertPostRequestObject {
        public long id;
        public long endDate;
        public long startDate;
        public String imageURL;
        public String gameId;

        public AdvertPostRequestObject(Advert ad) {
            this.id = ad.getId();
            this.gameId = ad.getGame().getId();
            this.imageURL = ad.getImageURL();
            this.startDate = ad.getStartDate();
            this.endDate = ad.getEndDate();
        }
    }
}
