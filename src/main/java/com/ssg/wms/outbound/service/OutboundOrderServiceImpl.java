package com.ssg.wms.outbound.service;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.DispatchDTO;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import com.ssg.wms.outbound.mappers.DispatchMapper;
import com.ssg.wms.outbound.mappers.OutboundOrderMapper;
import com.ssg.wms.outbound.mappers.WaybillMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboundOrderServiceImpl implements OutboundOrderService {


    private final OutboundOrderMapper outboundOrderMapper;
    private final DispatchMapper dispatchMapper;
    private final WaybillMapper waybillMapper;


    //  출고지시서 전체 조회
    @Override
    public List<OutboundOrderDTO> getAllRequests(Criteria criteria, String search) {
        log.info("출고지시서 전체 조회 요청: {}", search);
        return outboundOrderMapper.getAllOrders(criteria, search);
    }

    // 출고지시서 조건 조회(상태별로, 승인자별로 등등)
    @Override
    public List<OutboundOrderDTO> getFilteredOrders(Criteria criteria, String filterType, String searchValue) {
        log.info("출고지시서 조건 조회: 필터 타입={}, 값={}", filterType, searchValue);
        return outboundOrderMapper.getFilteredOrders(criteria, filterType, searchValue);
    }


    // 출고지시서 상세조회
    @Override
    public OutboundOrderDTO getRequestDetailById(Long outboundRequestId) {
        log.info("출고지시서 상세 조회 요청: ID={}", outboundRequestId);
        OutboundOrderDTO outboundOrderDTO = outboundOrderMapper.getOrderDetailById(outboundRequestId);

        if (outboundOrderDTO == null) {
            throw new RuntimeException("출고지시서 ID를 찾을 수 없습니다: " + outboundRequestId);
        }
        return outboundOrderDTO;
    }


// 출고지시서 상태 변경 (승인/반려/취소 등)

@Override
@Transactional
public void updateOrderStatus(OutboundOrderDTO outboundOrderDTO) {
    // 1. 출고지시서 상태 업데이트
    outboundOrderMapper.updateOrderStatus(outboundOrderDTO);

    // 2. 승인된 경우에만 배차 및 운송장 처리
    if ("승인".equals(outboundOrderDTO.getOrderStatus())) {

        Long dispatchId = dispatchMapper.getDispatchIdByApprovedOrderId(outboundOrderDTO.getApprovedOrderID());

        if (dispatchId == null) {
            // 🚀 Dispatch 신규 생성
            dispatchMapper.insertDispatchInformation(outboundOrderDTO);
            dispatchId = outboundOrderDTO.getDispatchId();
        } else {
            // 🚀 Dispatch 수정 — 여기서 DispatchDTO로 변환해서 전달
            DispatchDTO dispatchDTO = DispatchDTO.builder()
                    .dispatchId(dispatchId)
                    .approvedOrderID(outboundOrderDTO.getApprovedOrderID())
                    .carId(outboundOrderDTO.getCarId())
                    .carType(outboundOrderDTO.getCarType())
                    .driverName(outboundOrderDTO.getDriverName())
                    .dispatchStatus(outboundOrderDTO.getDispatchStatus())
                    .loadedBox(outboundOrderDTO.getLoadedBox())
                    .maximumBox(outboundOrderDTO.getMaximumBOX())
                    .build();

            dispatchMapper.updateDispatchInformation(dispatchDTO);
        }

        // 🚚 운송장 생성
        outboundOrderDTO.setDispatchId(dispatchId);
        String newWaybillNumber = generateUniqueWaybillNumber();
        outboundOrderDTO.setWaybillNumber(newWaybillNumber);
        waybillMapper.insertWaybill(outboundOrderDTO);
    }
}



    // 운송장 번호 생성 도우미 메서드
    private String generateUniqueWaybillNumber() {
        return "WB-" + System.currentTimeMillis();
    }
}