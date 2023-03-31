package cz.tul.stin.server.controller;
import cz.tul.stin.server.model.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
public class LoginController {
    @Autowired
    private EmailSenderService service;

    @PostMapping("/")
    public String login(@RequestBody String clientId) throws Exception {
        clientId = clientId.replace("=","");
        User client = User.getUserData(Integer.parseInt(clientId));
        String code = Bank.generateRandomCode();
        String msg = String.format("Váš kód pro přilášení je: %s",code);
        service.sendSimpleEmail(client.getEmail(), Const.EMAIL_SUBJECT,msg);
        return code;
    }

}



