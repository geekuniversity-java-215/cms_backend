package com.github.geekuniversity_java_215.cmsbackend.payment.services;

import com.github.geekuniversity_java_215.cmsbackend.utils.Junit5Extension;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith({Junit5Extension.class})
@Slf4j
class PayPalServiceTest {

    private static String clientId;
    private static Integer tax;
    private final PayPalGetPaymentService payPalGetPaymentService;

    @BeforeAll
    public static void beforeAll() {
        clientId="16";
        tax=137;
    }

    @Autowired
    public PayPalServiceTest(PayPalGetPaymentService payPalGetPaymentService) {
        this.payPalGetPaymentService = payPalGetPaymentService;
    }

    @Test
    void authorizePayment() {

        try {
            payPalGetPaymentService.authorizePayment(clientId,tax);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getPaymentDetails() {
        //log.info("Мир Труд Май");
    }

    @Test
    void executePayment() {
    }

}