package com.pastew.ingameadsui.Payment;

import com.pastew.ingameadsui.Advert.AdvertOffer;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.common.RequestEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Help: https://github.com/paypal/adaptivepayments-sdk-java/blob/fac801284dd4d479eecde3bae58f61af25f12827/adaptivepaymentssample/src/main/java/com/sample/usecase/ChainedPaymentServlet.java
@Service
public class PaymentService {

    public static final String PAYPAL_URL_KEY = "paypalUrl";

    private static final String INGAMEADS_PAYPAY_EMAIL = "ingameads@qwe.com";
    private static final String PAY_PAY_APP_USER_NAME = "ingameads_api1.abc.com";
    private static final String PAY_PAY_APP_PASSWORD = "S72DPDGXKRAYWFWC";
    private static final String PAY_PAL_APP_SIGNATURE = "ACtFxnHuuFD7lG8CcJ2kkb2sBEbmAR-VOtJAF5UmfBKGpB3zbL99AixV";
    private static final String PAY_PAY_APP_ID = "APP-80W284485P519543T";
    private static final String CURRENCY_CODE = "USD";
    private static final double GAME_DEVELOPER_MONEY_MULTIPLIER = 0.9;
    private static final double IN_GAME_ADS_MONEY_MULTIPLIER = 0.1;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map pay(AdvertOffer offer) {
        Payment payment = new Payment(offer.getId());
        paymentRepository.save(payment);
        double moneyTotal = offer.getAdvert().calculateCost();

        String successUrl = "http://localhost:8080/offers/" + offer.getId() + "/finishPayment/" + payment.getId();
        String cancelUrl = "http://localhost:8080/offers/" + offer.getId() + "/cancelledPayment";

        // Game Developer
        Receiver gameDeveloperReceiver = new Receiver();
        gameDeveloperReceiver.setAmount(moneyTotal * GAME_DEVELOPER_MONEY_MULTIPLIER);
        gameDeveloperReceiver.setEmail(offer.getGameOwner().getEmail());

        // In Game Ads
        Receiver ingameadsReceiver = new Receiver();
        ingameadsReceiver.setAmount(moneyTotal * IN_GAME_ADS_MONEY_MULTIPLIER);
        ingameadsReceiver.setEmail(INGAMEADS_PAYPAY_EMAIL);

        ReceiverList receiverList = new ReceiverList(Arrays.asList(ingameadsReceiver, gameDeveloperReceiver));

        PayRequest req = new PayRequest();
        req.setReceiverList(receiverList);
        RequestEnvelope env = new RequestEnvelope("pl_PL");
        req.setRequestEnvelope(env);
        req.setActionType("PAY");
        req.setCancelUrl(cancelUrl);
        req.setCurrencyCode(CURRENCY_CODE);
        req.setReturnUrl(successUrl);

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("mode", "sandbox");
        configMap.put("acct1.UserName", PAY_PAY_APP_USER_NAME);
        configMap.put("acct1.Password", PAY_PAY_APP_PASSWORD);
        configMap.put("acct1.Signature", PAY_PAL_APP_SIGNATURE);
        configMap.put("acct1.AppId", PAY_PAY_APP_ID);

        AdaptivePaymentsService service = new AdaptivePaymentsService(configMap);

        Map<Object, Object> map = new LinkedHashMap<Object, Object>();

        try {
            PayResponse resp = service.pay(req);
            if (resp != null) {
                if (resp.getResponseEnvelope().getAck().toString()
                        .equalsIgnoreCase("SUCCESS")) {
                    map.put("Ack", resp.getResponseEnvelope().getAck());
                    map.put("Correlation ID", resp.getResponseEnvelope().getCorrelationId());
                    map.put("Time Stamp", resp.getResponseEnvelope().getTimestamp());
                    map.put("Pay Key", resp.getPayKey());
                    map.put("Payment Execution Status", resp.getPaymentExecStatus());

                    if (resp.getDefaultFundingPlan() != null) {
                        /* Default funding plan. */
                        map.put("Default Funding Plan", resp.getDefaultFundingPlan().getFundingPlanId());
                    }
                    // Skipping for Implicit Payments
                    if (!resp.getPaymentExecStatus().equalsIgnoreCase(
                            "Completed")) {
                        map.put(PAYPAL_URL_KEY,
                                "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey="
                                        + resp.getPayKey()
                                        + ">https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey="
                                        + resp.getPayKey());
                    }

                } else {
                    map.put("error", resp.getError().get(0).getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", e.getMessage());
        }

        return map;
    }

}
