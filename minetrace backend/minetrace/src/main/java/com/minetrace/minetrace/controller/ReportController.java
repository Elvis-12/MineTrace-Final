package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @GetMapping("/mine-production")
    public ResponseEntity<Map<String, Object>> getMineProduction() {
        return ResponseEntity.ok(reportService.getMineProduction());
    }

    @GetMapping("/mineral-distribution")
    public ResponseEntity<Map<String, Object>> getMineralDistribution() {
        return ResponseEntity.ok(reportService.getMineralDistribution());
    }

    @GetMapping("/production")
    public ResponseEntity<Map<String, Object>> getProduction(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getProductionReport(startDate, endDate));
    }

    @GetMapping("/movement")
    public ResponseEntity<Map<String, Object>> getMovement(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getMovementReport(startDate, endDate));
    }

    @GetMapping("/compliance")
    public ResponseEntity<Map<String, Object>> getCompliance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getComplianceReport(startDate, endDate));
    }

    @GetMapping("/risk")
    public ResponseEntity<Map<String, Object>> getRisk(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getRiskReport(startDate, endDate));
    }
}
