package com.ssg.wms.finance.controller;

import com.ssg.wms.dto.CategorySummaryDTO;
import com.ssg.wms.dto.DashboardSummaryDTO;
import com.ssg.wms.service.DashboardService;
import com.ssg.wms.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Log4j2
public class DashboardController {

    private final DashboardService dashboardService;
    private final ExpenseService expenseService;

    // JSP 렌더링
    @GetMapping
    public String dashboard(Model model, @RequestParam(required = false, defaultValue = "0") int year) {
        if (year == 0) {
            year = Year.now().getValue();
        }

        List<DashboardSummaryDTO> netProfitSummary = dashboardService.getNetProfitSummary(year);
        List<CategorySummaryDTO> expenseSummary = expenseService.getAnnualExpenseSummary(year);

        model.addAttribute("netProfitSummary", netProfitSummary);
        model.addAttribute("expenseSummary", expenseSummary);
        model.addAttribute("currentYear", year);

        return "dashboard/index";
    }


    // 대시보드 데이터 통합 조회
    // REST API (차트 데이터 갱신용)
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardDataApi(
            @RequestParam(required = false, defaultValue = "0") int year) {

        if (year == 0) {
            year = Year.now().getValue();
        }

        List<DashboardSummaryDTO> netProfitSummary = dashboardService.getNetProfitSummary(year);
        List<CategorySummaryDTO> expenseSummary = expenseService.getAnnualExpenseSummary(year);

        Map<String, Object> response = new HashMap<>();
        response.put("netProfitSummary", netProfitSummary);
        response.put("expenseSummary", expenseSummary);
        response.put("currentYear", year);

        return ResponseEntity.ok(response);
    }
    // (axios.get("/api/dashboard?year=2025")로 호출)
    /*@GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(required = false, defaultValue = "0") int year) {

        if (year == 0) {
            year = Year.now().getValue();
        }

        // 1. 총정산 내역 (월별 순수익)
        List<DashboardSummaryDTO> netProfitSummary = dashboardService.getNetProfitSummary(year);

        // 2. 연간 지출 내역 (카테고리별)
        List<CategorySummaryDTO> expenseSummary = expenseService.getAnnualExpenseSummary(year);

        // 3. 데이터를 Map에 담아 JSON으로 한 번에 반환
        Map<String, Object> response = new HashMap<>();
        response.put("netProfitSummary", netProfitSummary);
        response.put("expenseSummary", expenseSummary);
        response.put("currentYear", year);

        return ResponseEntity.ok(response);
    }*/
}