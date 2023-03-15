package cz.tul.stin.server.controller;
import cz.tul.stin.server.bank.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping("/")
    public String hello() throws Exception {
        Account a = Bank.loadAccountDataFromFile(123456789);
        return a.toString();
    }

    /*
    @RequestMapping("/time")
    public String time() {
        //return System.time
        return "";
    }
    */
}

