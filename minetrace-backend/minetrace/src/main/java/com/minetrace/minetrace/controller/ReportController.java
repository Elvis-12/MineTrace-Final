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
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam(required = false) String district) {
        return ResponseEntity.ok(reportService.getSummary(district));
    }

    @GetMapping("/mine-production")
    public ResponseEntity<Map<String, Object>> getMineProduction(
            @RequestParam(required = false) String district) {
        return ResponseEntity.ok(reportService.getMineProduction(district));
    }

    @GetMapping("/mineral-distribution")
    public ResponseEntity<Map<String, Object>> getMineralDistribution(
            @RequestParam(required = false) String district) {
        return ResponseEntity.ok(reportService.getMineralDistribution(district));
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

    @GetMapping("/stock-by-location")
    public ResponseEntity<Map<String, Object>> getStockByLocation() {
        return ResponseEntity.ok(reportService.getStockByLocation());
    }
}
