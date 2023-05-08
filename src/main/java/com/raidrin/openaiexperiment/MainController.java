package com.raidrin.openaiexperiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired
    private CountryFactsService countryFactsService;

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @PostMapping("/country")
    public String country(@RequestParam("country") String countryName, Model model) {
        CountryInfo countryInfo = null;
        try {
            countryInfo = countryFactsService.getFact(countryName);
        } catch (NoCountryFoundException e) {
            return "noCountryFound";
        } catch (JsonProcessingException e) {
            return "errorCountryInfo";
        }
        model.addAttribute("countryInfo", countryInfo);
        return "country";
    }
}
