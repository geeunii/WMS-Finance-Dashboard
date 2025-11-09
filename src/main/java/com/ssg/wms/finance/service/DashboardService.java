package com.ssg.wms.finance.service;

import com.ssg.wms.dto.DashboardSummaryDTO;

import java.util.List;

public interface DashboardService {
    List<DashboardSummaryDTO> getNetProfitSummary(int year);
}
