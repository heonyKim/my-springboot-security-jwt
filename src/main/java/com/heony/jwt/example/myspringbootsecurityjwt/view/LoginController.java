package com.heony.jwt.example.myspringbootsecurityjwt.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login(HttpServletRequest request, HttpServletResponse response, Model model){
        return "login";
    }
}
