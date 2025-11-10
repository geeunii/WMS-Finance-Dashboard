package com.ssg.wms.outbound.controller;

import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundDTO;
import com.ssg.wms.outbound.service.OutboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/outbound")
@RequiredArgsConstructor
@Log4j2
public class outboundController {

    private final OutboundService outboundService;


    // http://localhost:8080/user/outbound?userId=1
    // 1. 출고 요청 목록 조회 (GET /user/outbound)
    @GetMapping // 목록 조회는 기본 경로를 사용합니다.
    public ResponseEntity<List<OutboundDTO>> getUserShipmentList(
            @RequestParam Long userId,
            Criteria criteria,
            @RequestParam(required = false) String status) {
        log.info("출고 요청 목록 조회 - userId: {}, status: {}", userId, status);

        // 현재는 'allOutboundRequests'를 사용하셨던 그대로 유지합니다.
        List<OutboundDTO> requestList = outboundService.allOutboundRequests(userId, criteria, status);
        return ResponseEntity.ok(requestList);
    }


    
    // 2. 출고 요청 생성 (POST /user/outbound)
    @PostMapping // 생성은 기본 경로를 사용합니다.
    public ResponseEntity<OutboundDTO> createOutboundRequest(
            @RequestBody OutboundDTO outboundDTO,
            @RequestParam Long userId) {
        return ResponseEntity.ok(outboundService.createOutboundRequest(outboundDTO, userId));
    }

    // 3. 출고 요청 상세 조회 (GET /user/outbound/{requestId})
    @GetMapping("/{requestId}")
    public ResponseEntity<OutboundDTO> getUserShipmentDetail(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        log.info("출고 요청 상세 조회 - requestId: {}, userId: {}", requestId, userId);
        OutboundDTO requestDetail = outboundService.getRequestDetailById(requestId, userId);
        return ResponseEntity.ok(requestDetail);
    }

    // 4. 출고 요청 수정 (PUT /user/outbound/{requestId})
    @PutMapping("/{requestId}")
    public ResponseEntity<Void> updateShipmentRequest(
            @PathVariable Long requestId,
            @RequestBody OutboundDTO dto,
            @RequestParam Long userId) {
        log.info("출고 요청 수정 - requestId: {}, userId: {}, data: {}", requestId, userId, dto);
        outboundService.updateRequest(requestId, userId, dto);
        return ResponseEntity.ok().build();
    }

    // 5. 출고 요청 삭제 (DELETE /user/outbound/{requestId})
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteShipmentRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        log.info("출고 요청 삭제 - requestId: {}, userId: {}", requestId, userId);
        outboundService.deleteRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }

    // 6. [페이지 반환] 출고 요청 생성 페이지 (GET /user/outbound/form) - 기존 GET /request를 /form으로 변경
    @GetMapping("/form") // API 엔드포인트와 충돌을 피하기 위해 경로를 변경합니다.
    public String getUserShipmentForm() {
        log.info("출고 요청 생성 페이지 접근");
        return "user/shipmentRequestForm";
    }
}