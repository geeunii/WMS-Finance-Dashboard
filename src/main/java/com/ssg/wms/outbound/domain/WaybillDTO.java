package com.ssg.wms.outbound.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaybillDTO {
    private Long waybillId;
    private String waybillNumber;
    private LocalDateTime waybillDate;
    private String waybillStatus;
    private Long dispatchId;
    private String departureAddress;
    private String arrivalAddress;
    private String senderName;
    private String receiverName;
}
