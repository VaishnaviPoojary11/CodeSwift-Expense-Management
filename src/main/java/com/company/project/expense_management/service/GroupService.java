package com.company.project.expense_management.service;

import com.company.project.expense_management.entity.Group;
import com.company.project.expense_management.entity.Member;
import com.company.project.expense_management.repository.GroupRepository;
import com.company.project.expense_management.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public GroupService(
            GroupRepository groupRepository,
            MemberRepository memberRepository) {

        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }


    /* =================================================
       CREATE GROUP + MEMBERS
       ================================================= */

    public Group createGroup(Group group) {

        // First save the group
        Group savedGroup = groupRepository.save(group);


        // Save every member separately
        if (group.getMembers() != null) {

            for (Member member : group.getMembers()) {

                member.setId(null);

                // Connect member to the saved group
                member.setGroup(savedGroup);

                memberRepository.save(member);
            }
        }


        // Return the group with its members
        savedGroup.setMembers(
                memberRepository.findByGroupId(savedGroup.getId())
        );

        return savedGroup;
    }


    /* =================================================
       GET ALL GROUPS
       ================================================= */

    public List<Group> getAllGroups() {

        List<Group> groups = groupRepository.findAll();

        for (Group group : groups) {

            group.setMembers(
                    memberRepository.findByGroupId(group.getId())
            );
        }

        return groups;
    }


    /* =================================================
       GET GROUP BY ID
       ================================================= */

    public Optional<Group> getGroupById(Long id) {

        Optional<Group> groupOptional =
                groupRepository.findById(id);

        if (groupOptional.isEmpty()) {
            return Optional.empty();
        }

        Group group = groupOptional.get();

        group.setMembers(
                memberRepository.findByGroupId(id)
        );

        return Optional.of(group);
    }
}