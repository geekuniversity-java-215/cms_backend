package com.github.geekuniversity_java_215.cmsbackend.cmsapplication;

import com.github.geekuniversity_java_215.cmsbackend.cmsapplication.entities.TestEntity;
import com.github.geekuniversity_java_215.cmsbackend.cmsapplication.repositories.TestRepository;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Account;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.Customer;
import com.github.geekuniversity_java_215.cmsbackend.core.services.PersonService;
import com.github.geekuniversity_java_215.cmsbackend.mail.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CmsAppStartupRunner implements ApplicationRunner {

    private final PersonService personService;
    private final TestRepository testRepository;
    private final MailService mailService;

    @Autowired
    public CmsAppStartupRunner(PersonService personService, TestRepository testRepository, MailService mailService) {
        this.personService = personService;
        this.testRepository = testRepository;
        this.mailService = mailService;
    }

    @Override
    public void run(ApplicationArguments args) {

        testRepository.save(new TestEntity("Вася test"));
        log.info("TestEntity saved");

        Account account = new Account();
        Customer customer = new Customer("Вася", "Пупкин", "vasya@mail.ru", "1122334455");
        customer.setAccount(account);
        personService.save(customer);

        customer = (Customer) personService.findById(1L).get();
        log.info("Customer: {}", customer);
        
        //mailService.sendMessage("cmsbackendgeek@gmail.com", "Дарова", "Дарова чувачелло, как жизяя?");
    }
}
