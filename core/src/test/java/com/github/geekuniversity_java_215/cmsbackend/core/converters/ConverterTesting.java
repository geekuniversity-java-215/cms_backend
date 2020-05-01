package com.github.geekuniversity_java_215.cmsbackend.core.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.geekuniversity_java_215.cmsbackend.core.converters.order.OrderConverter;
import com.github.geekuniversity_java_215.cmsbackend.core.data.enums.OrderStatus;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Address;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Courier;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Customer;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Order;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.base.AbstractEntity;
import com.github.geekuniversity_java_215.cmsbackend.core.services.AddressService;
import com.github.geekuniversity_java_215.cmsbackend.core.services.OrderService;
import com.github.geekuniversity_java_215.cmsbackend.core.services.PersonService;
import com.github.geekuniversity_java_215.cmsbackend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;


import javax.annotation.PostConstruct;
import java.time.Instant;

@SpringBootTest
@Slf4j
class ConverterTesting {

    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private PersonService personService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;

    Address from;
    Address to;
    Order order;

    Customer customer;
    Courier courier;

    //Заместо @BeforeAll
    @PostConstruct
    private void postConstruct() {
        from = new Address("Москва", "Улица красных тюленей", 1, 2, 3);
        to = new Address("Мухосранск", "Западная", 2, 2, 5);

        customer = new Customer("Вася", "Пупкин", "vasya@mail.ru", "1122334455");
        courier = new Courier("Сема", "Пасечкин", "sema@mail.ru", "908796786543", "блабла");

        personService.save(customer);
        personService.save(courier);

        order = new Order();
        order.setFrom(from);
        order.setTo(to);
        order.setStatus(OrderStatus.DONE);
        order.setCourier(courier);
        order.setCustomer(customer);
        orderService.save(order);
    }


    @Test
    void OrderConverterTest() {

        JsonNode orderJson = orderConverter.toDtoJson(order);
        String json = orderJson.toString();
        log.info(orderJson.toPrettyString());
        Order newWorldOrder = orderConverter.toEntity(orderJson);

//        copyTimes(newWorldOrder, order);
//        copyTimes(newWorldOrder.getFrom(), order.getFrom());
//        copyTimes(newWorldOrder.getTo(), order.getTo());

        JsonNode orderJsonNew = orderConverter.toDtoJson(order);
        String jsonNew = orderJson.toString();
        log.info(orderJsonNew.toPrettyString());

        Assert.isTrue(json.equals(jsonNew), "OrderConverter failed");
    }



    private void copyTimes(AbstractEntity entity, AbstractEntity from) {

        Utils.fieldSetter("created", entity, from.getCreated());
        Utils.fieldSetter("updated", entity, from.getUpdated());

    }
}