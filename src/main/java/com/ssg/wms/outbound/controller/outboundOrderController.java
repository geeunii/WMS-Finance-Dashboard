package com.ssg.wms.outbound.controller;

import com.ssg.wms.common.Role;
import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import com.ssg.wms.outbound.service.OutboundOrderService;
import com.ssg.wms.warehouse.dto.WarehouseListDTO;
import com.ssg.wms.warehouse.dto.WarehouseSearchDTO;
import com.ssg.wms.warehouse.service.WarehouseAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/outbound")
@RequiredArgsConstructor
@Log4j2
public class outboundOrderController {

    private final OutboundOrderService outboundOrderService;
    private final WarehouseAdminService warehouseAdminService; // ⭐ final 추가


    /** ADMIN 권한 체크 */
    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && role.equals(Role.ADMIN);
    }

    /** 세션에서 로그인했는지 체크 */
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginId") != null;
    }

    /**
     * 로그인 안 됨 → /member/login
     * 로그인됨 + ADMIN 아님 → /error/403
     */
    private String validateAdminAccess(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/error/403";
        }
        return null;
    }


    // =======================
    // 출고 지시서 리스트 조회
    // =======================
    @GetMapping
    public String getOutboundOrderList(Criteria criteria,
                                       @RequestParam(required = false) String filterType,
                                       HttpSession session,
                                       Model model) {

        String auth = validateAdminAccess(session);
        if (auth != null) return auth;

        List<OutboundOrderDTO> list = outboundOrderService.getAllRequests(criteria, filterType);
        model.addAttribute("outboundOrders", list);

        return "/outbound/admin/outboundOrderList";
    }


    // =======================
    // 배차/요청상태 등록 폼
    // =======================
    @GetMapping("/{instructionId}/dispatch-form")
    public String getDispatchForm(@PathVariable("instructionId") Long instructionId,
                                  Model model,
                                  HttpSession session) {

        String auth = validateAdminAccess(session);
        if (auth != null) return auth;

        OutboundOrderDTO detail = outboundOrderService.getRequestDetailById(instructionId);
        model.addAttribute("dispatch", detail);

        return "outbound/admin/dispatchForm";
    }


    // =======================
    // 배차 등록 (JSON)
    // =======================
    @PostMapping("/{instructionId}/register")
    @ResponseBody
    public ResponseEntity<String> registerDispatch(
            @PathVariable("instructionId") Long instructionId,
            @RequestBody OutboundOrderDTO dto,
            HttpSession session) {

        if (!isLoggedIn(session))
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        if (!isAdmin(session))
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");

        log.info("배차 등록 요청: instructionId={}, dto={}", instructionId, dto);

        try {
            OutboundOrderDTO existingOrder = outboundOrderService.getRequestDetailById(instructionId);

            if ("승인".equals(existingOrder.getApprovedStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 승인된 건입니다.");
            }


            if ("완료".equals(existingOrder.getDispatchStatus())) {
                return ResponseEntity.status(409)
                        .body("이미 배차가 완료된 건입니다.");
            }

            if (dto.getLoadedBox() > dto.getMaximumBOX()) {
                return ResponseEntity.badRequest()
                        .body("출고 박스 수가 최대 적재량을 초과했습니다.");
            }

            dto.setApprovedOrderID(instructionId);
            outboundOrderService.updateOrderStatus(dto);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            log.error("배차 등록 실패", e);
            return ResponseEntity.status(500)
                    .body("error: " + e.getMessage());
        }
    }


    // =======================
    // 승인 상태 조회
    // =======================
    @GetMapping("/{instructionId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkApprovalStatus(
            @PathVariable("instructionId") Long instructionId,
            HttpSession session) {

        if (!isLoggedIn(session))
            return ResponseEntity.status(401).build();
        if (!isAdmin(session))
            return ResponseEntity.status(403).build();

        try {
            OutboundOrderDTO order = outboundOrderService.getRequestDetailById(instructionId);

            Map<String, String> response = new HashMap<>();
            response.put("approvedStatus", order.getApprovedStatus());
            response.put("approvedOrderId", String.valueOf(instructionId));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", "상태 조회 실패"));
        }
    }


    // =======================
    // ⭐ 창고 목록 조회 (배차 등록용)
    // =======================
    @GetMapping("/dispatches/warehouses")
    @ResponseBody
    public ResponseEntity<List<WarehouseListDTO>> getWarehouseList() {
        log.info("📦 배차 등록용 창고 목록 조회 요청");

        try {
            // null 대신 빈 검색 조건으로 전체 창고 조회
            WarehouseSearchDTO searchDTO = new WarehouseSearchDTO();
            List<WarehouseListDTO> list = warehouseAdminService.findWarehouses(searchDTO);

            log.info("창고 목록 조회 결과: {} 건", list.size());

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("창고 목록 조회 실패", e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}