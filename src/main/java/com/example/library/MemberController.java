package com.example.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @GetMapping("/members")
    public String listMembers(Model model) {
        model.addAttribute("members", memberRepository.findAll());
        return "members";
    }

    @GetMapping("/members/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", new Member());
        return "add-member";
    }

    @PostMapping("/members/save")
    public String saveMember(@ModelAttribute Member member) {
        memberRepository.save(member);
        return "redirect:/members";
    }
    // Delete করা
@GetMapping("/members/delete/{id}")
public String deleteMember(@org.springframework.web.bind.annotation.PathVariable Long id,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

    long unreturnedCount = borrowRecordRepository.countByMemberIdAndReturnedFalse(id);

    if (unreturnedCount > 0) {
        redirectAttributes.addFlashAttribute("error",
            "This person has to return " + unreturnedCount + " book(s) before deletion.");
        return "redirect:/members";
    }

    // সব বই ফেরত দেওয়া হয়ে গেলে, পুরনো borrow history গুলো আগে মুছে দাও
    borrowRecordRepository.deleteByMemberId(id);

    memberRepository.deleteById(id);
    return "redirect:/members";
}

// Edit ফর্ম দেখানো (পুরনো ডাটা সহ)
@GetMapping("/members/edit/{id}")
public String showEditForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
    Member member = memberRepository.findById(id).orElseThrow();
    model.addAttribute("member", member);
    return "edit-member";
}

// Edit ফর্ম থেকে আপডেট সেভ করা
@PostMapping("/members/update")
public String updateMember(@ModelAttribute Member member) {
    memberRepository.save(member);
    return "redirect:/members";
}
}