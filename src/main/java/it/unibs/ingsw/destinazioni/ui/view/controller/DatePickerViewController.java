package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class DatePickerViewController {

    @GetMapping("/date-picker")
    public String showDatePickerForm(@RequestParam(defaultValue = "blocked") String mode,
                                     Model model) {
        model.addAttribute("mode", mode); // "blocked" o "available"
        return "date-picker";
    }
}