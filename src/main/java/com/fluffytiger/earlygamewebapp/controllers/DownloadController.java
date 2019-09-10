package com.fluffytiger.earlygamewebapp.controllers;

import com.fluffytiger.earlygamewebapp.services.JarStorageService;
import com.fluffytiger.earlygamewebapp.services.UserSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class DownloadController {
    private final UserSession session;
    private final JarStorageService files;

    public DownloadController(UserSession session, JarStorageService files) {
        this.session = session;
        this.files = files;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String downloadPage(Model model) {
        model.addAttribute("authenticated", session.isAuthenticated());
        model.addAttribute("username", session.getUsername());
        model.addAttribute("files", files.list());
        return "download_page";
    }
}
