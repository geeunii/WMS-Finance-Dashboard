package com.ssg.wms.finance.mappers;

import com.ssg.wms.domain.ExpenseVO;
import com.ssg.wms.dto.CategorySummaryDTO;
import com.ssg.wms.dto.DashboardSummaryDTO;
import com.ssg.wms.dto.ExpenseRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExpenseMapper {
    List<ExpenseVO> findAll(ExpenseRequestDTO dto);

    int count(ExpenseRequestDTO dto);

    Optional<ExpenseVO> findById(Long id);

    void save(ExpenseVO expenseVO);

    void update(ExpenseVO expenseVO);

    void delete(Long id);

    List<CategorySummaryDTO> findSummaryByCategory(int year);

    List<DashboardSummaryDTO> findMonthlySummary(int year);
}
