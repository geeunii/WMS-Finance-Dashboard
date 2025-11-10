package com.ssg.wms.outbound.mappers;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.DispatchDTO;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DispatchMapper {

    // 기사 이름으로 배차 목록 조회
    List<DispatchDTO> getDispatchNameList(@Param("criteria") Criteria criteria,
                                          @Param("driverName") String driverName);

    //배차 ID로 배차 상세 조회
    DispatchDTO getDispatchDetailById(@Param("dispatchId") Long dispatchId);


    //배차 정보 등록
    void insertDispatchInformation(@Param("outboundOrderDTO") OutboundOrderDTO outboundOrderDTO);

    // 배차 정보 수정
    void updateDispatchInformation(@Param("outboundOrderDTO") OutboundOrderDTO outboundOrderDTO);

    // 운송장 번호 등록
    void insertMinimalWaybill(@Param("dispatchId") int dispatchId,
                              @Param("waybillNumber") String waybillNumber);

}
