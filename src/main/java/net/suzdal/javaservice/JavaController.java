package net.suzdal.javaservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/java")
public class JavaController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ok", true);
        response.put("service", "java-service x");
        response.put("status", "UP");
        response.put("time", Instant.now().toString());
        return response;
    }

    @GetMapping("/recalcular-costes")
    public Map<String, Object> recalcularCostes(@RequestParam(required = false) String articulo) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ok", true);
        response.put("service", "java-service y");
        response.put("articulo", articulo);
        response.put("mensaje", "Aqui ira el calculo pesado de costes en Java Spring Boot");
        response.put("time", Instant.now().toString());
        return response;
    }
}
