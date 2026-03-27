package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

import static org.example.acs_v2.constants.ViewConstants.INDEX;

/**
 * Главный контроллер приложения
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ModelAttributeHelper modelAttributeHelper;

    /**
     * Главная страница
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    /**
     * Главная страница
     */
    @GetMapping("/index")
    public String index(Principal principal, Model model) {
        if (principal == null) {
            log.warn("Principal is null on index page");
        } else {
            log.debug("User {} accessed index page", principal.getName());
        }

        modelAttributeHelper.addUserAttributes(model, principal);
        return INDEX;
    }
}
