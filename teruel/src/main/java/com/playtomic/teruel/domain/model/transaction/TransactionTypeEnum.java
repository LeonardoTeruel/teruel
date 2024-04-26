package com.playtomic.teruel.domain.model.transaction;

import lombok.Getter;

@Getter
public enum TransactionTypeEnum {
    TOP_UP(1, "Top_Up"),
    PURCHASE(2, "Purchase"),
    REFUND(3, "Refund");

    private final int typeId;
    private final String name;

    TransactionTypeEnum(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }



}
