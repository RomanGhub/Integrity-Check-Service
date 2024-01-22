package org.kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ResultController {

    @Autowired
    private ResultMonitoringService resultMonitoringService;


    @GetMapping("/results")
    public String showResults(){
        return "results";
    }




}
