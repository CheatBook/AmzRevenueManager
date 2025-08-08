package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sku-names")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class SkuNameController {

    private final SkuNameService skuNameService;

    @GetMapping
    public ResponseEntity<List<SkuName>> getAllSkuNames() {
        try {
            List<SkuName> skuNames = skuNameService.findAllSkuNames();
            return ResponseEntity.ok(skuNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/distinct-skus")
    public ResponseEntity<List<String>> getDistinctSkusFromSettlements() {
        try {
            List<String> distinctSkus = skuNameService.findDistinctSkusFromSettlements();
            return ResponseEntity.ok(distinctSkus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<SkuName> saveSkuName(@RequestBody SkuName skuName) {
        try {
            SkuName savedSkuName = skuNameService.saveSkuName(skuName);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSkuName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/parent-sku")
    public ResponseEntity<SkuName> createParentSkuName(@RequestBody SkuName parentSkuRequest) {
        try {
            SkuName createdParentSku = skuNameService.createParentSkuName(parentSkuRequest.getJapaneseName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdParentSku);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parent-skus")
    public ResponseEntity<List<SkuName>> getParentSkus() {
        try {
            List<SkuName> parentSkus = skuNameService.findParentSkus();
            return ResponseEntity.ok(parentSkus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}