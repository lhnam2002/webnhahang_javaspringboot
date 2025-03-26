package spring.dacn.mercury.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home() {
        return "home/index";
    }
//    @GetMapping("/home")
//    public String showMenuPage() {
//        return "home/index"; // Đường dẫn tới file templates/home/index.html
//    }

}
