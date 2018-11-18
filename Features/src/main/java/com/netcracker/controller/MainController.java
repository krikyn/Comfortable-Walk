package com.netcracker.controller;

import com.netcracker.model.Path;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @PostMapping("/loginSuccess")
    public void postParams(@ModelAttribute Path path) {
        System.out.println("From point: " + path.getFromPoint() + " To Point: " + path.getToPoint());
    }
}
