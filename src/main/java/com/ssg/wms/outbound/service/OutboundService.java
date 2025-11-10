package com.ssg.wms.outbound.service;


//출고 요청 생성
//출고 요청 목록 조회
//출고 요청 상세 조회
//출고 요청 수정
//출고 요청 삭제



import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundDTO;

import java.util.List;

public interface OutboundService {

    OutboundDTO createOutboundRequest(OutboundDTO outboundDTO, Long userId);  // 출고 요청 생성

    List<OutboundDTO> allOutboundRequests(Long userId, Criteria criteria, String status); // 출고 요청 전체 조회

    List<OutboundDTO> getRequestsByUserId(Long userId, Criteria criteria, String status); // 출고 요청 목록 조회

    OutboundDTO getRequestDetailById(Long outboundRequestId, Long userId); // 출고 요청 상세 조회

    void updateRequest(Long outboundRequestId, Long userId, OutboundDTO outboundDTO); // 출고 요청 수정

    boolean deleteRequest(Long outboundRequestId, Long userId); // 출고 요청 삭제
}
