package com.ssg.wms.outbound.controller;

import com.ssg.wms.outbound.domain.Criteria;
import com.ssg.wms.outbound.domain.dto.OutboundOrderDTO;
import com.ssg.wms.outbound.service.OutboundOrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/outbound")
@RequiredArgsConstructor
@Log4j2
public class outboundOrderController {

    private final OutboundOrderService outboundOrderService;




    // 출고지시서 조건 조회(상태별로, 승인자별로 등등)
    //(required = false)로 설정되어 있어 값이 없을 때는 전체 조회, 값이 있을 때는 조건 조회
    @GetMapping
    public ResponseEntity<List<OutboundOrderDTO>>getOutboundInstructionList(Criteria criteria,
                                                                            @RequestParam(required = false) String filterType){
        log.info("출고지시서 목록 조회 요청 - filterType: {}", filterType);

        // search 값이 null이면 전체 조회, 값이 있으면 조건 조회로 Service에서 처리해야 합니다.
        List<OutboundOrderDTO> instructionList = outboundOrderService.getAllRequests(criteria, filterType);
        return ResponseEntity.ok(instructionList);
    }




    // 출고지시서 상세 조회 (GET /admin/instructions/{instructionId})
    @GetMapping("/{instructionId}")
    public ResponseEntity<OutboundOrderDTO> getOutboundInstructionDetail(
            @PathVariable("instructionId") Long approvedOrderId) { // PathVariable 이름과 메서드 파라미터 이름이 다를 경우 명시

        log.info("출고지시서 상세 조회 요청 - Instruction ID: {}", approvedOrderId);

        // Service를 호출하여 특정 지시서 상세 정보를 가져옵니다.
        OutboundOrderDTO detail = outboundOrderService.getRequestDetailById(approvedOrderId);

        // 데이터가 없을 경우 404 Not Found를 처리하는 로직을 Service에서 구현하는 것이 일반적입니다.
        return ResponseEntity.ok(detail);
    }



    // 출고지시서 상태 변경 (PATCH /admin/instructions/{instructionId}/status)
    @PatchMapping("/{instructionId}/status")
    public ResponseEntity<Void> updateInstructionStatus(
            @PathVariable("instructionId") Long approvedOrderId,
            @RequestBody OutboundOrderDTO outboundOrderDTO) {

        log.info("출고지시서 상태 변경 요청 - ID: {}, 새 상태: {}", approvedOrderId, outboundOrderDTO.getApprovedStatus());

        // Service를 호출하여 상태 변경 로직을 처리합니다.
        // updateDTO에는 변경할 상태 정보(예: '승인' 또는 '반려')가 담겨 있습니다.
        outboundOrderService.updateOrderStatus(outboundOrderDTO);

        // 200 OK 응답 (Body 없음)
        return ResponseEntity.ok().build();
    }

}