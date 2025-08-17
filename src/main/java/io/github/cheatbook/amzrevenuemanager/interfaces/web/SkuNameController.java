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
import io.github.cheatbook.amzrevenuemanager.application.service.SkuNameApplicationService;
import lombok.RequiredArgsConstructor;

/**
 * SKU名に関するコントローラークラスです。
 */
@RestController
@RequestMapping("/api/sku-names")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class SkuNameController {

    /**
     * SKU名アプリケーションサービス
     */
    private final SkuNameApplicationService skuNameApplicationService;

    /**
     * すべてのSKU名を取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping
    public ResponseEntity<List<SkuName>> getAllSkuNames() {
        try {
            List<SkuName> skuNames = skuNameApplicationService.getAllSkuNames();
            return ResponseEntity.ok(skuNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 決済情報から重複しないSKUのリストを取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping("/distinct-skus")
    public ResponseEntity<List<String>> getDistinctSkusFromSettlements() {
        try {
            // このエンドポイントはアプリケーション内でSKU名が処理されるようになったため、不要になりました。
            // 削除するか、新しい要件に適応させることを検討してください。
            // 現時点ではコメントアウトしておきます。
            // List<String> distinctSkus = skuNameService.findDistinctSkusFromSettlements();
            List<String> distinctSkus = new java.util.ArrayList<>();
            return ResponseEntity.ok(distinctSkus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * SKU名を保存します。
     *
     * @param skuName SKU名
     * @return レスポンスエンティティ
     */
    @PostMapping
    public ResponseEntity<SkuName> saveSkuName(@RequestBody SkuName skuName) {
        try {
            SkuName savedSkuName = skuNameApplicationService.saveSkuName(skuName);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSkuName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 親SKU名を作成します。
     *
     * @param parentSkuRequest 親SKUリクエスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/parent-sku")
    public ResponseEntity<SkuName> createParentSkuName(@RequestBody SkuName parentSkuRequest) {
        try {
            // このロジックはアプリケーションサービスで処理されるべきです
            SkuName parentSku = new SkuName();
            parentSku.setJapaneseName(parentSkuRequest.getJapaneseName());
            SkuName createdParentSku = skuNameApplicationService.saveSkuName(parentSku);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdParentSku);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 親SKUのリストを取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping("/parent-skus")
    public ResponseEntity<List<SkuName>> getParentSkus() {
        try {
            List<SkuName> parentSkus = skuNameApplicationService.findParentSkus();
            return ResponseEntity.ok(parentSkus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}