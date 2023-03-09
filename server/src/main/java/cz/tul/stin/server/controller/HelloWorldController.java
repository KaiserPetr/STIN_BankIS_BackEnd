package cz.tul.stin.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @RequestMapping("/")
    public String hello() {
        return "Hello javaTpoint";
    }

    @RequestMapping("/time")
    public String time() {
        //return System.time
        return "";
    }
}

