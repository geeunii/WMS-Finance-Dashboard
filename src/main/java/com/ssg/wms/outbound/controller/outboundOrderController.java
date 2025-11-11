package com.ssg.wms.outbound.controller;

import com.ssg.wms.common.Role;
import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import com.ssg.wms.outbound.service.OutboundOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/admin/outbound")
@RequiredArgsConstructor
@Log4j2
public class outboundOrderController {

    private final OutboundOrderService outboundOrderService;


    /**
     * ✅ 관리자 권한 체크 공통 메서드
     */
    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && role.equals(Role.ADMIN);
    }

    // 1️⃣ 출고지시서 조건 조회 + 전체 조회
    @GetMapping
    public ResponseEntity<List<OutboundOrderDTO>> getOutboundInstructionList(
            HttpSession session,
            Criteria criteria,
            @RequestParam(required = false) String filterType) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build(); // 관리자만 접근 가능
        }

        log.info("출고지시서 목록 조회 요청 - filterType: {}", filterType);
        List<OutboundOrderDTO> instructionList = outboundOrderService.getAllRequests(criteria, filterType);
        return ResponseEntity.ok(instructionList);
    }

    // 2️⃣ 출고지시서 상세 조회
    @GetMapping("/{instructionId}")
    public ResponseEntity<OutboundOrderDTO> getOutboundInstructionDetail(
            @PathVariable("instructionId") Long approvedOrderId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        log.info("출고지시서 상세 조회 요청 - ID: {}", approvedOrderId);
        OutboundOrderDTO detail = outboundOrderService.getRequestDetailById(approvedOrderId);

        return ResponseEntity.ok(detail);
    }

    // 3️⃣ 출고지시서 상태 변경
    @PostMapping("/{instructionId}/status")
    public ResponseEntity<Void> updateInstructionStatus(
            @PathVariable("instructionId") Long approvedOrderId,
            @RequestBody OutboundOrderDTO outboundOrderDTO,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        outboundOrderDTO.setApprovedOrderID(approvedOrderId);
        log.info("출고지시서 상태 변경 요청 - ID: {}, 상태: {}", approvedOrderId, outboundOrderDTO.getApprovedStatus());

        outboundOrderService.updateOrderStatus(outboundOrderDTO);
        return ResponseEntity.ok().build();
    }
}