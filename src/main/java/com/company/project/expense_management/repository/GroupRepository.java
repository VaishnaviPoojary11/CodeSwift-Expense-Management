package com.company.project.expense_management.repository;

import com.company.project.expense_management.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}