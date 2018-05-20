package com.pastew.ingameadsui.Payment;

import com.pastew.ingameadsui.Advert.AdvertOfferService;
import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AdvertOfferService advertOfferService;

    @GetMapping("/offers/{offerId}/finishPayment/{paymentId}")
    public String finishPayment(Model model, @PathVariable long offerId, @PathVariable long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (!paymentOptional.isPresent()) {
            model.addAttribute("message", "Coś poszło nie tak, nie odnaleziono takiej płatności.");
        } else if (paymentOptional.get().getPaymentStatus() == PaymentStatus.COMPLETE) {
            model.addAttribute("message", "Płatność już była potwierdzona.");
        } else {
            try {
                Payment payment = paymentOptional.get();
                payment.setPaymentStatus(PaymentStatus.COMPLETE);
                paymentRepository.save(payment);

                advertOfferService.notifyImageProviderAboutNewAdvert(offerId);
                model.addAttribute("message", "Płatność zakończona sukcesem!");

            } catch (AdvertBuyException e) {
                e.printStackTrace();
                model.addAttribute("message", "Coś poszło nie tak, nie udał się zapisać transakcji.");
            }
        }

        return "finishPayment";
    }

    @GetMapping("/offers/{offerId}/cancelledPayment")
    public String cancelledPayment() {
        return "cancelledPayment";
    }
}
