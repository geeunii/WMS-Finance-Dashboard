package com.ssg.wms.outbound.controller;

import com.ssg.wms.common.Role;
import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundDTO;
import com.ssg.wms.outbound.service.OutboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user/outbound")
@RequiredArgsConstructor
@Log4j2
public class outboundController {

    private final OutboundService outboundService;
    private final ProductService productService;

    // 🚨 권한 검증 로직은 제거되거나 필터/인터셉터로 분리되어야 합니다.
    // 여기서는 Controller에서 최소한의 userId 검증만 유지합니다.

    // --- 1. 출고 요청 목록 조회 (View 반환) ---
    @GetMapping
    public String getUserShipmentList(
            @RequestParam Long userId, // 🚨 userId를 쿼리 파라미터로 받음
            Criteria criteria,
            @RequestParam(required = false) String status,
            Model model) {

        if (userId == null) {
            // userId가 없으면 401 대신 View 로직에 따라 처리 (예: 에러 페이지)
            return "redirect:/login";
        }

        log.info("출고 요청 목록 조회 - userId: {}, status: {}", userId, status);
        List<OutboundDTO> requestList = outboundService.allOutboundRequests(userId, criteria, status);

        model.addAttribute("requestList", requestList);
        return "outbound/member/outboundList";
    }

    // --- 2. 출고 요청 생성 (POST API) ---
    @PostMapping
    @ResponseBody
    public ResponseEntity<OutboundDTO> createOutboundRequest(
            @RequestBody OutboundDTO outboundDTO,
            @RequestParam Long userId) { // 🚨 userId를 쿼리 파라미터로 받음

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        log.info("출고 요청 생성 - userId: {}, data: {}", userId, outboundDTO);
        OutboundDTO created = outboundService.createOutboundRequest(outboundDTO, userId);
        return ResponseEntity.ok(created);
    }

    // --- 3. 출고 요청 상세 조회 (GET API) ---
    @GetMapping("/{requestId}")
    @ResponseBody
    public ResponseEntity<OutboundDTO> getUserShipmentDetail(
            @PathVariable Long requestId,
            @RequestParam Long userId) { // 🚨 userId를 쿼리 파라미터로 받음

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        log.info("출고 요청 상세 조회 - requestId: {}, userId: {}", requestId, userId);
        // Service 메서드는 userId를 사용하여 해당 사용자의 요청인지 검증해야 함.
        OutboundDTO requestDetail = outboundService.getRequestDetailById(requestId, userId);
        return ResponseEntity.ok(requestDetail);
    }

    // --- 4. 출고 요청 수정 (PUT API) ---
    @PutMapping("/{requestId}")
    @ResponseBody
    public ResponseEntity<Void> updateShipmentRequest(
            @PathVariable Long requestId,
            @RequestBody OutboundDTO dto,
            @RequestParam Long userId) { // 🚨 userId를 쿼리 파라미터로 받음

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        log.info("출고 요청 수정 - requestId: {}, userId: {}, data: {}", requestId, userId, dto);
        outboundService.updateRequest(requestId, userId, dto);
        return ResponseEntity.ok().build();
    }

    // --- 5. 출고 요청 삭제 (DELETE API) ---
    @DeleteMapping("/{requestId}")
    @ResponseBody
    public ResponseEntity<Void> deleteShipmentRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) { // 🚨 userId를 쿼리 파라미터로 받음

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        log.info("출고 요청 삭제 - requestId: {}, userId: {}", requestId, userId);
        outboundService.deleteRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }




    //http://localhost:8080/user/outbound/form?userId=1
    // --- 6. 출고 요청 생성 페이지 (View 반환) ---
    @GetMapping("/form")
    public String getUserShipmentForm(@RequestParam Long userId) { // 🚨 userId를 쿼리 파라미터로 받음

        if (userId == null) {
            return "redirect:/login";
        }

        log.info("출고 요청 생성 페이지 접근 - userId: {}", userId);
        return "outbound/member/shipmentRequestForm";
    }
}