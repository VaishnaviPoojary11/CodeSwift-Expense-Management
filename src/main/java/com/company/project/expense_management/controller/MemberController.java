package com.company.project.expense_management.controller;

import com.company.project.expense_management.entity.Member;
import com.company.project.expense_management.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://code-swift-expense-management-front.vercel.app"
})
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) {
        return memberService.addMember(member);
    }

    @GetMapping("/group/{groupId}")
    public List<Member> getMembersByGroup(
            @PathVariable Long groupId) {

        return memberService.getMembersByGroup(groupId);
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}