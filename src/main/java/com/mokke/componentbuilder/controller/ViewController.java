package com.mokke.componentbuilder.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String getMain (Model model) throws IOException, InterruptedException {
        String uniqueID = UUID.randomUUID().toString();
        model.addAttribute("sessionId",uniqueID);

        return "main-page";
    }

}
