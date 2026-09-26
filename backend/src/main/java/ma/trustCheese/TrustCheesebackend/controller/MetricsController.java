package ma.trustCheese.TrustCheesebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.metrics.MetricsResponse;
import ma.trustCheese.TrustCheesebackend.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping
    public ResponseEntity<MetricsResponse> getMetrics() {
        return ResponseEntity.ok(
                metricsService.getMetrics()
        );
    }
}