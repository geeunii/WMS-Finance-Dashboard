package com.ssg.wms.finance.service;

import com.ssg.wms.domain.ExpenseVO;
import com.ssg.wms.dto.CategorySummaryDTO;
import com.ssg.wms.dto.ExpenseRequestDTO;
import com.ssg.wms.dto.ExpenseResponseDTO;
import com.ssg.wms.dto.ExpenseSaveDTO;

import java.util.List;

public interface ExpenseService {
    ExpenseResponseDTO getExpenses(ExpenseRequestDTO dto);

    ExpenseVO getExpense(Long id);

    Long saveExpense(ExpenseSaveDTO dto);

    void updateExpense(Long id, ExpenseSaveDTO dto);

    void deleteExpense(Long id);

    List<CategorySummaryDTO> getAnnualExpenseSummary(int year);
}
