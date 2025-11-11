package com.ssg.wms.outbound.service;


import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundDTO;
import com.ssg.wms.outbound.domain.dto.OutboundItemDTO;
import com.ssg.wms.outbound.mappers.OutboundItemMapper;
import com.ssg.wms.outbound.mappers.OutboundMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboundServiceImpl implements OutboundService {


    private final OutboundMapper outboundMapper;
    private final OutboundItemMapper outboundItemMapper;


    
    /**
     * 출고요청 생성
     */
    @Override
    @Transactional
    public OutboundDTO createOutboundRequest(OutboundDTO dto, Long userId) {
        // 1. 사용자 ID 등 자동 설정
        dto.setMemberId(userId);
        dto.setOutboundDate(LocalDate.now());
        dto.setApprovedStatus("승인대기");

        // 2. 출고요청 등록
        outboundMapper.insertOutboundRequest(dto);

        // 3. 품목 등록
        for (OutboundItemDTO item : dto.getShipmentItems()) {
            item.setOutboundRequestId(dto.getOutboundRequestId());
            outboundItemMapper.insertOutboundItem(item);
        }

        return dto;
    }

    /**
     * 특정 사용자 출고 요청 목록 조회
     */
    @Override
    public List<OutboundDTO> getRequestsByUserId(Long userId, Criteria criteria, String status) {
        return outboundMapper.selectOutboundRequestByUserId(userId, criteria, status);
    }

    /**
     * 전체 출고 요청 목록 조회
     */
    @Override
    public List<OutboundDTO> allOutboundRequests(Long userId, Criteria criteria, String status) {
        return outboundMapper.selectAllShipment(userId, criteria, status);
    }

    /**
     * 출고 요청 상세 조회
     */
    @Override
    public OutboundDTO getRequestDetailById(Long outboundRequestId, Long userId) {
        return outboundMapper.getRequestDetailByUser(outboundRequestId, userId);
    }

    /**
     * 출고 요청 수정
     */
    @Override
    @Transactional
    public void updateRequest(Long outboundRequestId, Long userId, OutboundDTO dto) {

        // 0. 승인 상태 확인
        String approveStatus = outboundMapper.getOutboundOrderStatusByRequestId(outboundRequestId);

        if ("승인".equalsIgnoreCase(approveStatus)) {
            throw new IllegalStateException("이미 승인된 출고요청은 수정할 수 없습니다.");
        }

        // 1. 메인 출고요청 수정
        outboundMapper.updateOutboundRequestMain(outboundRequestId, userId, dto);

        // 2. 품목 수정
        if (dto.getShipmentItems() != null) {
            for (OutboundItemDTO item : dto.getShipmentItems()) {
                outboundMapper.updateOutboundItem(item, outboundRequestId);
            }
        }
    }

    /**
     * 출고 요청 삭제
     */
    @Override
    @Transactional
    public boolean deleteRequest(Long outboundRequestId, Long userId) {

        String approveStatus = outboundMapper.getOutboundOrderStatusByRequestId(outboundRequestId);

        if ("승인".equalsIgnoreCase(approveStatus)) {
            throw new IllegalStateException("이미 승인된 출고요청은 삭제할 수 없습니다.");
        }

        // 1. 품목 삭제
        outboundMapper.deleteOutboundItemsByRequestId(outboundRequestId);

        // 2. 출고지시서 삭제
        outboundMapper.deleteShipmentOrder(outboundRequestId);

        // 3. 메인 요청 삭제
        int result = outboundMapper.deleteRequest(outboundRequestId, userId);

        return result > 0;
    }
}