package com.company.project.expense_management.service;

import com.company.project.expense_management.entity.Group;
import com.company.project.expense_management.entity.Member;
import com.company.project.expense_management.repository.GroupRepository;
import com.company.project.expense_management.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;


    public MemberService(
            MemberRepository memberRepository,
            GroupRepository groupRepository) {

        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }


    /* =================================================
       ADD MEMBER
       ================================================= */

    public Member addMember(Member member) {

        if (member.getGroup() == null ||
            member.getGroup().getId() == null) {

            throw new RuntimeException(
                    "Group is required for member"
            );
        }


        // Get the actual group from MySQL
        Group group = groupRepository
                .findById(member.getGroup().getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Group not found"
                        )
                );


        // Connect member to the real database group
        member.setGroup(group);


        return memberRepository.save(member);
    }


    /* =================================================
       GET MEMBERS BY GROUP
       ================================================= */

    public List<Member> getMembersByGroup(Long groupId) {

        return memberRepository.findByGroupId(groupId);
    }


    /* =================================================
       DELETE MEMBER
       ================================================= */

    public void deleteMember(Long id) {

        memberRepository.deleteById(id);
    }
}