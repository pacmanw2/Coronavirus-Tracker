package io.gallegos.coronavirustracker.controllers;

import io.gallegos.coronavirustracker.models.LocationStats;
import io.gallegos.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController{

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        getAllConfirmedStats(model);
        return "home";
    }

    @GetMapping("/deaths")
    public String deaths(Model model) {
        getAllDeathStats(model);
        return "deaths";
    }

    @GetMapping("/recovered")
    public String recovered(Model model) {
        getAllRecoveredStats(model);
        return "recovered";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    private void getAllConfirmedStats(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllConfirmedStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDeltaFromPrevDay()).sum();
        model.addAttribute("locationStats", coronaVirusDataService.getAllConfirmedStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
    }

    private void getAllRecoveredStats(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllRecoveredStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDeltaFromPrevDay()).sum();
        model.addAttribute("locationStats", coronaVirusDataService.getAllRecoveredStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
    }

    private void getAllDeathStats(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllDeathStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDeltaFromPrevDay()).sum();
        model.addAttribute("locationStats", coronaVirusDataService.getAllDeathStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
    }
}
