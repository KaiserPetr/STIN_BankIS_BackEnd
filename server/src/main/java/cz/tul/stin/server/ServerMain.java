package cz.tul.stin.server;

import cz.tul.stin.server.bank.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class ServerMain {

	/*
	public static void main(String[] args) {

		SpringApplication.run(ServerMain.class, args);
	}
	*/
	public static void main(String[] args) throws Exception {
		Account a = Bank.loadAccountDataFromFile(123456789);
		System.out.println(a.toString());
		String msg = a.provideTransaction(new Transaction('-',new Currency("CZK",100)));
		System.out.println(msg);
		System.out.println(a.toString());
	}

}
