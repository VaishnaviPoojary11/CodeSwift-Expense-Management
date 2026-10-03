package com.company.project.expense_management.repository;

import com.company.project.expense_management.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseParticipantRepository
        extends JpaRepository<ExpenseParticipant, Long> {

    List<ExpenseParticipant> findByExpenseId(Long expenseId);

    void deleteByExpenseId(Long expenseId);
}