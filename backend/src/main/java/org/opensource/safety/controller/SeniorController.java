package org.opensource.safety.controller;

import java.util.List;
import org.opensource.safety.domain.Senior;
import org.opensource.safety.dto.SeniorRequest;
import org.opensource.safety.service.SeniorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seniors")
public class SeniorController {
    private final SeniorService seniorService;

    public SeniorController(SeniorService seniorService) {
        this.seniorService = seniorService;
    }

    @GetMapping
    public List<Senior> list() {
        return seniorService.findAll();
    }

    @PostMapping
    public Senior create(@RequestBody SeniorRequest request) {
        return seniorService.create(request);
    }
}
