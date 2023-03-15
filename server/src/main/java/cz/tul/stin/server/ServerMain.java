package cz.tul.stin.server;

import cz.tul.stin.server.bank.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.controller.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Objects;

@SpringBootApplication
public class ServerMain {

	@Autowired
	private EmailSenderService service;
	public static void main(String[] args) {

		SpringApplication.run(ServerMain.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void triggerMail() throws Exception {
		Account a = Bank.loadAccountDataFromFile(123456789);
		String code = Bank.generateRandomCode();
		String msg = String.format("Váš kód pro přilášení je: %s",code);
		service.sendSimpleEmail(a.getEmail(),Const.EMAIL_SUBJECT,msg);
	}

	/*
	public static void main(String[] args) throws Exception {

		// TODO toto volat jen, kdyz je MD kazdou minutu mezi 14:20-14:50
		Bank.downloadExchangeRates();

		Account a = Bank.loadAccountDataFromFile(123456789);

		/*
		System.out.println(a.toString());
		//String msg = a.provideTransaction(new Transaction('-',new Currency("EUR",100)));
		String msg = a.provideTransaction(a.generateRandomTransaction());
		System.out.println(msg);
		System.out.println(a.toString());

	}
	*/

}
