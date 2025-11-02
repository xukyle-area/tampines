package com.ganten.market.common.enums;

/**
 * Order action types.
 * modify order: operation is treated as DELETE for the old order and INSERT for the new order
 */
public enum Action {
    // 新增订单，改单后新增订单
    INSERT,
    // 成交，改单后撤销前面的订单，撤单
    DELETE;
}
