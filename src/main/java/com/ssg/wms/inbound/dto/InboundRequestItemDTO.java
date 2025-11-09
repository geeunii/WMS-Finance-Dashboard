package com.ssg.wms.inbound.dto;

import lombok.Data;

@Data
public class InboundRequestItemDTO {
    private int inboundItemId;
    private int inboundId;
    private int productId; // 필수
    private int amount;  // 필수
}
