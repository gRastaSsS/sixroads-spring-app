package com.fluffytiger.earlygamewebapp.controllers;

import com.fluffytiger.earlygamewebapp.model.JarMeta;
import com.fluffytiger.earlygamewebapp.model.OS;
import com.fluffytiger.earlygamewebapp.services.JarStorageService;
import com.fluffytiger.earlygamewebapp.services.UserSession;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("")
public class MainController {
    private final UserSession session;
    private final JarStorageService jars;
    private final JarStorageService files;

    public MainController(UserSession session, JarStorageService jars, JarStorageService files) {
        this.session = session;
        this.jars = jars;
        this.files = files;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(Model model) {
        final Optional<String> latestVersion = this.jars.findLatestVersion();

        if (latestVersion.isPresent()) {
            for (JarMeta jar : this.jars.findJarsWithVersion(latestVersion.get())) {
                switch (jar.getOsName()) {
                    case Linux: model.addAttribute("linuxJar", jar); continue;
                    case macOs: model.addAttribute("macOsJar", jar); continue;
                    case Windows: model.addAttribute("windowsJar", jar); continue;
                }
            }
        }

        return "main_page";
    }

    @RequestMapping(value = "download/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable long id) {
        try {
            final Resource resource = this.files.getFile(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sixroads.jar")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .build();
        }
    }

    @RequestMapping(value = "eula", method = RequestMethod.GET)
    public String eula(Model model) {
        return "eula_page";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginPage(Model model) {
        model.addAttribute("authenticated", session.isAuthenticated());
        model.addAttribute("username", session.getUsername());
        return "login_page";
    }
}
