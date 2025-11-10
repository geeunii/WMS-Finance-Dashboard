package com.ssg.wms.outbound.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchDTO {
    private Long dispatchId;
    private Long carId;
    private String cartype;
    private String driverName;
    private LocalDateTime assignedDate;
    private String dispatchStatus;
    private int loadedBox;
    private int maximumBox;
    private Long shipmentOrderId;
}
