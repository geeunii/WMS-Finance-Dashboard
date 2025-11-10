package com.ssg.wms.outbound.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundDTO {
    private Long outboundRequestId; // 	출고요청 고유번호

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate outboundDate; // 요청일자

    private String approvedStatus;
    private String outboundAddress;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate requestedDeliveryDate; // 출고 희망일

    private Long memberId;
    private Long staffId;
    private Long warehouseId;

    private List<OutboundItemDTO> shipmentItems;


}
