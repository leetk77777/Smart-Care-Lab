package org.opensource.safety.controller;

import java.util.Map;
import org.opensource.safety.service.DemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping("/{seniorId}/{scenario}")
    public Map<String, String> run(@PathVariable Long seniorId, @PathVariable String scenario) {
        demoService.runScenario(seniorId, scenario);
        return Map.of("message", "demo scenario completed", "scenario", scenario);
    }
}
