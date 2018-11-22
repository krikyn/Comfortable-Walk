package com.netcracker.controller;

import com.netcracker.data.model.bean.Path;
import com.netcracker.util.CheckedItemsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class MainPageController {

    private final CheckedItemsUtil checkedItemsUtil;

    @PostMapping("/loginSuccess")
    public String postParams(@ModelAttribute Path path) {
        path.setCheckedItems(checkedItemsUtil.getItems(path.getPlaceName().toUpperCase()));
        System.out.println("From point: " + path.getFromPoint() + " To Point: " + path.getToPoint()
                + " Wanna best weather? " + path.getIsBestWeather() + " Checked items: " + path.getCheckedItems());
        return "redirect:/loginSuccess";
    }
}
