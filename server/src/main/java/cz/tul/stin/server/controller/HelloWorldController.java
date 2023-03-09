package cz.tul.stin.server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping("/")
    public String hello() {
        return "Hello world";
    }

    /*
    @RequestMapping("/time")
    public String time() {
        //return System.time
        return "";
    }
    */
}

