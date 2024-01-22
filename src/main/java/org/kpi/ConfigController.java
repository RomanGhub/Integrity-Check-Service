package org.kpi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class ConfigController {

    @Value("./src/main/resources/application.properties")
    private String configFilePath;

    @GetMapping("/config")
    public String showConfig(Model model) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(configFilePath)));
        model.addAttribute("configContent", content);
        return "config";
    }

    @PostMapping("/updateConfig")
    public String updateConfig(@RequestParam String configContent, Model model) throws IOException {
        Files.write(Paths.get(configFilePath), configContent.getBytes());
        model.addAttribute("configContent", configContent);
        return "config";
    }

    @GetMapping("/contract")
    public String showContract() {
        return "contract";
    }
}
