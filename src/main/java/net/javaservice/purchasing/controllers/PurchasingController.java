package net.javaservice.purchasing.controllers;

import net.javaservice.purchasing.excel.ExcelReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchasing")
public class PurchasingController {

    /* http://127.0.0.1:8080/api/purchasing  */
    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ok", true);
        response.put("area", "purchasing");
        response.put("mensaje", "Modulo de compras activo");
        response.put("time", Instant.now().toString());
        return response;
    }

    @GetMapping("/read-excel")
    public Map<String, Object> readExcel(@RequestParam String path) throws Exception {
        ExcelReader excelReader = new ExcelReader();

        List<List<String>> rows = excelReader.readFirstSheet(Path.of(path));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ok", true);
        response.put("file", path);
        response.put("rows_count", rows.size());
        response.put("rows", rows);
        response.put("time", Instant.now().toString());

        return response;
    }
}