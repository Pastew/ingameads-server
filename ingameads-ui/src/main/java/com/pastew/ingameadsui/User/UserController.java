package com.pastew.ingameadsui.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String passwordConfirmation,
                           @RequestParam String email,
                           RedirectAttributes redirectAttributes) {

        if (!password.equals(passwordConfirmation)) {
            redirectAttributes.addFlashAttribute("flash.message", "Hasła się różnią");
            return "redirect:/register";
        }

        userRepository.save(new User(username, password,
                email, "ROLE_USER"));

        return "redirect:/login";
    }
}
