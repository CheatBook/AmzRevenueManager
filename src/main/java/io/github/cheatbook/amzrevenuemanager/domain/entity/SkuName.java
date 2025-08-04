package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuName {

    @Id
    private String sku;

    private String japaneseName;

    private String parentSku;
}