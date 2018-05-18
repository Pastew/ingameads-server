package com.pastew.ingameadsui.Payment;

import com.pastew.ingameadsui.User.User;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.common.RequestEnvelope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Help: https://github.com/paypal/adaptivepayments-sdk-java/blob/fac801284dd4d479eecde3bae58f61af25f12827/adaptivepaymentssample/src/main/java/com/sample/usecase/ChainedPaymentServlet.java
@Controller
public class PaymentService {

    @GetMapping(value = "/pay")
    public String pay(Model model, HttpServletRequest httpSession) {

        Receiver gameDeveloperReceiver = new Receiver();
        gameDeveloperReceiver.setAmount(9.);
        gameDeveloperReceiver.setEmail("bob@developer.com");

        Receiver ingameadsReceiver = new Receiver();
        ingameadsReceiver.setAmount(1.0);
        ingameadsReceiver.setEmail("ingameads@abc.com");

        ReceiverList receiverList = new ReceiverList(Arrays.asList(ingameadsReceiver, gameDeveloperReceiver));

        PayRequest req = new PayRequest();
        req.setReceiverList(receiverList);
        RequestEnvelope env = new RequestEnvelope("pl_PL");
        req.setRequestEnvelope(env);
        req.setActionType("PAY");
        String returnUrl = httpSession.getRequestURL().toString();
        req.setCancelUrl(returnUrl);
        req.setCurrencyCode("USD");
        req.setReturnUrl(returnUrl);

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("mode", "sandbox");
        configMap.put("acct1.UserName", "ingameads_api1.abc.com");
        configMap.put("acct1.Password", "S72DPDGXKRAYWFWC");
        configMap.put("acct1.Signature", "ACtFxnHuuFD7lG8CcJ2kkb2sBEbmAR-VOtJAF5UmfBKGpB3zbL99AixV");
        configMap.put("acct1.AppId", "APP-80W284485P519543T");

        AdaptivePaymentsService service = new AdaptivePaymentsService(configMap);

        //HttpSession session = httpSession.getSession();

        try {
            PayResponse resp = service.pay(req);
            if (resp != null) {
//                session.setAttribute("RESPONSE_OBJECT", resp);
//                session.setAttribute("lastReq", service.getLastRequest());
//                session.setAttribute("lastResp", service.getLastResponse());
                if (resp.getResponseEnvelope().getAck().toString()
                        .equalsIgnoreCase("SUCCESS")) {
                    Map<Object, Object> map = new LinkedHashMap<Object, Object>();
                    map.put("Ack", resp.getResponseEnvelope().getAck());

                    /**
                     * Correlation identifier. It is a 13-character,
                     * alphanumeric string (for example, db87c705a910e) that is
                     * used only by PayPal Merchant Technical Support. Note: You
                     * must log and store this data for every response you
                     * receive. PayPal Technical Support uses the information to
                     * assist with reported issues.
                     */
                    map.put("Correlation ID", resp.getResponseEnvelope()
                            .getCorrelationId());

                    /**
                     * Date on which the response was sent, for example:
                     * 2012-04-02T22:33:35.774-07:00 Note: You must log and
                     * store this data for every response you receive. PayPal
                     * Technical Support uses the information to assist with
                     * reported issues.
                     */
                    map.put("Time Stamp", resp.getResponseEnvelope()
                            .getTimestamp());

                    /**
                     * The pay key, which is a token you use in other Adaptive
                     * Payment APIs (such as the Refund Method) to identify this
                     * payment. The pay key is valid for 3 hours; the payment
                     * must be approved while the pay key is valid.
                     */
                    map.put("Pay Key", resp.getPayKey());

                    /**
                     * The status of the payment. Possible values are: CREATED –
                     * The payment request was received; funds will be
                     * transferred once the payment is approved COMPLETED – The
                     * payment was successful INCOMPLETE – Some transfers
                     * succeeded and some failed for a parallel payment or, for
                     * a delayed chained payment, secondary receivers have not
                     * been paid ERROR – The payment failed and all attempted
                     * transfers failed or all completed transfers were
                     * successfully reversed REVERSALERROR – One or more
                     * transfers failed when attempting to reverse a payment
                     * PROCESSING – The payment is in progress PENDING – The
                     * payment is awaiting processing
                     */
                    map.put("Payment Execution Status",
                            resp.getPaymentExecStatus());
                    if (resp.getDefaultFundingPlan() != null) {
                        /* Default funding plan. */
                        map.put("Default Funding Plan", resp
                                .getDefaultFundingPlan().getFundingPlanId());
                    }
                    // Skipping for Implicit Payments
                    if (!resp.getPaymentExecStatus().equalsIgnoreCase(
                            "Completed")) {
                        map.put("Redirect URL",
                                "<a href=https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey="
                                        + resp.getPayKey()
                                        + ">https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey="
                                        + resp.getPayKey() + "</a>");
                    }
                    model.addAttribute("map", map);
                    return "paypalCheckout";
                } else {
                    model.addAttribute("message", resp.getError().get(0).getMessage());
                    return "error";
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "/";
    }
}
