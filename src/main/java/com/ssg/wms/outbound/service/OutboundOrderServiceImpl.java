package com.ssg.wms.outbound.service;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import com.ssg.wms.outbound.mappers.OutboundOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboundOrderServiceImpl implements OutboundOrderService {


    private final OutboundOrderMapper outboundOrderMapper;


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
        log.info("출고지시서 상태 변경 요청: ID={}, 상태={}", outboundOrderDTO.getApprovedOrderID(), outboundOrderDTO.getApprovedStatus());
        outboundOrderMapper.updateOrderStatus(outboundOrderDTO);
    }
}