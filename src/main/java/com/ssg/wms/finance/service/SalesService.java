package com.ssg.wms.finance.service;

import com.ssg.wms.domain.SalesVO;
import com.ssg.wms.dto.SalesRequestDTO;
import com.ssg.wms.dto.SalesResponseDTO;
import com.ssg.wms.dto.SalesSaveDTO;

public interface SalesService {
    SalesResponseDTO getSales(SalesRequestDTO dto);

    SalesVO getSale(Long id);

    Long saveSales(SalesSaveDTO dto);

    void updateSales(Long id, SalesSaveDTO dto);

    void deleteSales(Long id);
}
