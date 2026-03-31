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
import io.github.cheatbook.amzrevenuemanager.application.service.MessageLocalizationService;
import io.github.cheatbook.amzrevenuemanager.application.service.SkuNameApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKU名に関するコントローラークラスです。
 */
@RestController
@RequestMapping("/api/sku-names")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class SkuNameController {

    /**
     * SKU名アプリケーションサービス
     */
    private final SkuNameApplicationService skuNameApplicationService;

    /**
     * メッセージローカライズサービス
     */
    private final MessageLocalizationService messageLocalizationService;

    /**
     * すべてのSKU名を取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping
    public ResponseEntity<List<SkuName>> getAllSkuNames() {
        List<SkuName> skuNames = skuNameApplicationService.getAllSkuNames();
        return ResponseEntity.ok(skuNames);
    }

    /**
     * 決済情報から重複しないSKUのリストを取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping("/distinct-skus")
    public ResponseEntity<List<String>> getDistinctSkusFromSettlements() {
        List<String> distinctSkus = skuNameApplicationService.findDistinctSkusFromSettlements();
        return ResponseEntity.ok(distinctSkus);
    }

    /**
     * SKU名を保存します。
     *
     * @param skuName SKU名
     * @return レスポンスエンティティ
     */
    @PostMapping
    public ResponseEntity<SkuName> saveSkuName(@RequestBody SkuName skuName) {
        SkuName savedSkuName = skuNameApplicationService.saveSkuName(skuName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkuName);
    }

    /**
     * 親SKU名を作成します。
     *
     * @param parentSkuRequest 親SKUリクエスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/parent-sku")
    public ResponseEntity<SkuName> createParentSkuName(@RequestBody SkuName parentSkuRequest) {
        // このロジックはアプリケーションサービスで処理されるべきです
        SkuName parentSku = new SkuName();
        parentSku.setJapaneseName(parentSkuRequest.getJapaneseName());
        SkuName createdParentSku = skuNameApplicationService.saveSkuName(parentSku);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdParentSku);
    }

    /**
     * 親SKUのリストを取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping("/parent-skus")
    public ResponseEntity<List<SkuName>> getParentSkus() {
        List<SkuName> parentSkus = skuNameApplicationService.findParentSkus();
        return ResponseEntity.ok(parentSkus);
    }
}