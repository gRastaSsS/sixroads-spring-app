package com.fluffytiger.earlygamewebapp.controllers;

import com.fluffytiger.earlygamewebapp.exceptions.CustomException;
import com.fluffytiger.earlygamewebapp.payload.SignupRequest;
import com.fluffytiger.earlygamewebapp.services.SignupRequestValidator;
import com.fluffytiger.earlygamewebapp.services.UserService;
import com.fluffytiger.earlygamewebapp.services.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

public class RegistrationController {
    private final UserService users;
    private final UserSession session;
    private final SignupRequestValidator validator;

    public RegistrationController(UserService users, UserSession session, SignupRequestValidator validator) {
        this.users = users;
        this.session = session;
        this.validator = validator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String signupPage(SignupRequest signupRequest, Model model) {
        model.addAttribute("authenticated", session.isAuthenticated());
        model.addAttribute("username", session.getUsername());
        return "register_page";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String register(@Valid SignupRequest signupRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authenticated", session.isAuthenticated());
            model.addAttribute("username", session.getUsername());
            return "register_page";
        }

        try {
            this.users.signup(signupRequest);
            this.users.signin(signupRequest.getUsername(), signupRequest.getPassword());

        } catch (CustomException e) {
            return "redirect:/register";
        }

        return "redirect:/";
    }
}
