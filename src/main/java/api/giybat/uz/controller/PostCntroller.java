package api.giybat.uz.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gossip")
public class PostCntroller {


    @PostMapping("/creat")
    public String creat() {
        return "Hello World!";
    }

}
