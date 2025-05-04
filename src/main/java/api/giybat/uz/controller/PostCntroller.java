package api.giybat.uz.controller;

import api.giybat.uz.dto.PostDTO;
import api.giybat.uz.util.SpringSecurityUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostCntroller {


    @PostMapping("/creat")
    public String creat(@RequestBody PostDTO post) {
        System.out.println(SpringSecurityUtil.getCurrentProfile());
        System.out.println(SpringSecurityUtil.getCurrentUserId());
        return "Hello World!";
    }

}
