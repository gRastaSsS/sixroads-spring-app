package com.fluffytiger.earlygamewebapp.controllers;

import com.fluffytiger.earlygamewebapp.exceptions.StorageException;
import com.fluffytiger.earlygamewebapp.model.OS;
import com.fluffytiger.earlygamewebapp.model.User;
import com.fluffytiger.earlygamewebapp.services.JarStorageService;
import com.fluffytiger.earlygamewebapp.services.UserService;
import com.fluffytiger.earlygamewebapp.services.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("admin")
public class AdminController {
    private final UserSession session;
    private final JarStorageService files;
    private final UserService users;

    public AdminController(UserSession session, JarStorageService files, UserService users) {
        this.session = session;
        this.files = files;
        this.users = users;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String adminPage(Model model) {
        model.addAttribute("authenticated", session.isAuthenticated());
        model.addAttribute("username", session.getUsername());
        model.addAttribute("files", files.list());
        model.addAttribute("users", users.list());
        model.addAttribute("data", new User());
        return "admin_page";
    }

    @RequestMapping(value = "files/{id}", method = RequestMethod.DELETE)
    public String deleteFile(@PathVariable Long id) {
        try {
            this.files.delete(id);

            return "redirect:/admin";

        } catch (IOException e) {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "files", method = RequestMethod.POST)
    public String uploadFile(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "os") OS os,
            @RequestParam(value = "version") String version) {
        try {
            this.files.store(file, os, version);

            return "redirect:/admin";

        } catch (StorageException e) {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long id) {
        this.users.delete(id);
        return "redirect:/admin";
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public String createUser(@ModelAttribute User data) {
        this.users.add(data);
        return "redirect:/admin";
    }
}
