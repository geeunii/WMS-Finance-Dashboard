package com.ssg.wms.outbound.service;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.WaybillDTO;
import com.ssg.wms.outbound.mappers.WaybillMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class WaybillServiceImpl implements WaybillService {


    private final WaybillMapper waybillMapper;

    // 운송장 ID로 운송장 단건 조회
    @Override
    public WaybillDTO getWaybillById(String waybillNumber) {
        WaybillDTO waybillDTO = waybillMapper.getWaybillById(waybillNumber);
        return waybillDTO;
    }

    // 운송장 리스트 조회 (검색, 페이징 포함)
    @Override
    public List<WaybillDTO> getWaybillList(Criteria criteria, String search) {
        List<WaybillDTO> waybillDTOList = waybillMapper.getWaybills(criteria, search);
        return waybillDTOList;
    }

    //  운송장 정보입력
    @Override
    public WaybillDTO createWaybill(WaybillDTO waybillDTO) {
        waybillMapper.insertWaybill(waybillDTO);
        return waybillDTO;
    }


    //QR코드용 조회
    @Override
    public WaybillDTO getWaybillByNumber(String waybillNumber) {
        return null;
    }
}
