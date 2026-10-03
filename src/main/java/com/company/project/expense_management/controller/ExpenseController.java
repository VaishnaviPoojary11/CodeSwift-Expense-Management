package com.company.project.expense_management.controller;

import com.company.project.expense_management.dto.ExpenseRequest;
import com.company.project.expense_management.entity.Expense;
import com.company.project.expense_management.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }


    // Create expense
    @PostMapping("/group/{groupId}")
    public Expense createExpense(
            @PathVariable Long groupId,
            @RequestBody ExpenseRequest request) {

        return expenseService.createExpense(groupId, request);
    }


    // Get all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {

        return expenseService.getAllExpenses();
    }


    // Get expenses by group
    @GetMapping("/group/{groupId}")
    public List<Expense> getExpensesByGroup(
            @PathVariable Long groupId) {

        return expenseService.getExpensesByGroup(groupId);
    }


    // Get one expense
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(
            @PathVariable Long id) {

        return expenseService.getExpenseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Edit expense
    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseRequest request) {

        return expenseService.updateExpense(id, request);
    }


    // Delete expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }
}