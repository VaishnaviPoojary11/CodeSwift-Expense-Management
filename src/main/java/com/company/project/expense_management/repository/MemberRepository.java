package com.company.project.expense_management.repository;

import com.company.project.expense_management.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByGroupId(Long groupId);
}