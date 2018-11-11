package com.example.demo.weather_app.DirectionBuilder.Controller;



import com.example.demo.weather_app.DirectionBuilder.Model.Coordinate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Weather_appController {

    @GetMapping("/weather")
    public String weatherappForm(Model model) {
        model.addAttribute("direction", new Coordinate());
        return "weather";
    }

    @PostMapping("/weather")
    public String weatherSubmit(@ModelAttribute Coordinate direction) {
        direction.MakeDirection();
        return "result";
    }

}
