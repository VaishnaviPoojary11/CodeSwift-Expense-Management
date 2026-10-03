package com.company.project.expense_management.service;

import com.company.project.expense_management.dto.ExpenseRequest;
import com.company.project.expense_management.entity.Expense;
import com.company.project.expense_management.entity.ExpenseParticipant;
import com.company.project.expense_management.entity.Group;
import com.company.project.expense_management.entity.Member;
import com.company.project.expense_management.repository.ExpenseParticipantRepository;
import com.company.project.expense_management.repository.ExpenseRepository;
import com.company.project.expense_management.repository.GroupRepository;
import com.company.project.expense_management.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseParticipantRepository participantRepository,
            GroupRepository groupRepository,
            MemberRepository memberRepository) {

        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }


    /* =================================================
       CREATE EXPENSE
       ================================================= */

    @Transactional
    public Expense createExpense(
            Long groupId,
            ExpenseRequest request) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new RuntimeException("Group not found"));

        Member paidBy = memberRepository.findById(
                request.getPaidById()
        ).orElseThrow(() ->
                new RuntimeException("Payer not found"));

        validateMemberBelongsToGroup(paidBy, groupId);

        List<Member> participants =
                getAndValidateParticipants(
                        request.getParticipantIds(),
                        groupId
                );

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setGroup(group);
        expense.setPaidBy(paidBy);

        Expense savedExpense =
                expenseRepository.save(expense);

        saveParticipants(
                savedExpense,
                participants,
                request.getAmount()
        );

        return savedExpense;
    }


    /* =================================================
       UPDATE EXPENSE
       ================================================= */

    @Transactional
    public Expense updateExpense(
            Long id,
            ExpenseRequest request) {

        Expense existingExpense =
                expenseRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Expense not found"));

        Group group = existingExpense.getGroup();

        Long groupId = group.getId();


        // Validate payer

        Member paidBy = memberRepository.findById(
                request.getPaidById()
        ).orElseThrow(() ->
                new RuntimeException("Payer not found"));

        validateMemberBelongsToGroup(
                paidBy,
                groupId
        );


        // Validate participants

        List<Member> participants =
                getAndValidateParticipants(
                        request.getParticipantIds(),
                        groupId
                );


        // Update expense details

        existingExpense.setTitle(
                request.getTitle()
        );

        existingExpense.setAmount(
                request.getAmount()
        );

        existingExpense.setCategory(
                request.getCategory()
        );

        existingExpense.setDate(
                request.getDate()
        );

        existingExpense.setDescription(
                request.getDescription()
        );

        existingExpense.setPaidBy(
                paidBy
        );


        Expense savedExpense =
                expenseRepository.save(
                        existingExpense
                );


        /*
         * Remove old participant records.
         * New shares will be calculated below.
         */

        participantRepository.deleteByExpenseId(
                id
        );


        /*
         * Recalculate participant shares.
         */

        saveParticipants(
                savedExpense,
                participants,
                request.getAmount()
        );


        return savedExpense;
    }


    /* =================================================
       VALIDATE MEMBER
       ================================================= */

    private void validateMemberBelongsToGroup(
            Member member,
            Long groupId) {

        if (member.getGroup() == null ||
                member.getGroup().getId() == null ||
                !member.getGroup()
                        .getId()
                        .equals(groupId)) {

            throw new RuntimeException(
                    "Member does not belong to this group"
            );
        }
    }


    /* =================================================
       GET + VALIDATE PARTICIPANTS
       ================================================= */

    private List<Member> getAndValidateParticipants(
            List<Long> participantIds,
            Long groupId) {

        if (participantIds == null ||
                participantIds.isEmpty()) {

            throw new RuntimeException(
                    "At least one participant is required"
            );
        }


        List<Member> participants =
                memberRepository.findAllById(
                        participantIds
                );


        if (participants.size() !=
                participantIds.size()) {

            throw new RuntimeException(
                    "One or more participants not found"
            );
        }


        for (Member member : participants) {

            validateMemberBelongsToGroup(
                    member,
                    groupId
            );
        }


        return participants;
    }


    /* =================================================
       SAVE PARTICIPANTS + CALCULATE SHARES
       ================================================= */

    private void saveParticipants(
            Expense expense,
            List<Member> participants,
            Double amount) {

        BigDecimal totalAmount =
                BigDecimal.valueOf(amount);


        int participantCount =
                participants.size();


        BigDecimal share =
                totalAmount.divide(
                        BigDecimal.valueOf(
                                participantCount
                        ),
                        2,
                        RoundingMode.DOWN
                );


        BigDecimal remaining =
                totalAmount.subtract(
                        share.multiply(
                                BigDecimal.valueOf(
                                        participantCount
                                )
                        )
                );


        for (Member member : participants) {

            BigDecimal participantShare =
                    share;


            /*
             * Distribute remaining paise/cents
             * one by one so the total always
             * equals the original amount.
             */

            if (remaining.compareTo(
                    BigDecimal.ZERO) > 0) {

                participantShare =
                        participantShare.add(
                                new BigDecimal("0.01")
                        );

                remaining =
                        remaining.subtract(
                                new BigDecimal("0.01")
                        );
            }


            ExpenseParticipant participant =
                    new ExpenseParticipant();

            participant.setExpense(expense);

            participant.setMember(member);

            participant.setShareAmount(
                    participantShare.doubleValue()
            );


            participantRepository.save(
                    participant
            );
        }
    }


    /* =================================================
       OTHER OPERATIONS
       ================================================= */

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }


    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }


    public List<Expense> getExpensesByGroup(
            Long groupId) {

        return expenseRepository.findByGroupId(
                groupId
        );
    }


    public Optional<Expense> getExpenseById(
            Long id) {

        return expenseRepository.findById(id);
    }


    /* =================================================
       DELETE EXPENSE
       ================================================= */

    @Transactional
    public void deleteExpense(Long id) {

        participantRepository.deleteByExpenseId(
                id
        );

        expenseRepository.deleteById(id);
    }
}