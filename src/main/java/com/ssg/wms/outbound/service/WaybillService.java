package com.ssg.wms.outbound.service;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.WaybillDTO;

import java.util.List;

public interface WaybillService {

    WaybillDTO getWaybillById(String waybillNumber);   // 운송장 조회
    List<WaybillDTO> getWaybillList(Criteria criteria, String search);  // 운송장 리스트
    WaybillDTO createWaybill(WaybillDTO waybillDTO); // 운송장 생성
    WaybillDTO getWaybillByNumber(String waybillNumber);
}
