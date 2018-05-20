package com.pastew.ingameadsui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index(){
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(){
        return "login";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public String register(){
        return "register";
    }
}
