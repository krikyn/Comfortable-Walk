package com.example.demo.weather_app.DirectionBuilder.Controller;



import com.example.demo.weather_app.DirectionBuilder.Model.Coordinate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
// daba от названия класса немного вытекли глаза :)
public class Weather_appController {

    @GetMapping("/weather")
    // daba метод называется с глаголом
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
