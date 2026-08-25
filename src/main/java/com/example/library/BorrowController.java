package com.example.library;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BorrowController {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    // সব Borrow Record এর লিস্ট দেখানো
    @GetMapping("/borrow")
    public String listBorrowRecords(Model model) {
        model.addAttribute("records", borrowRecordRepository.findAll());
        return "borrow-list";
    }

    // নতুন করে বই ধার দেওয়ার ফর্ম দেখানো
    @GetMapping("/borrow/add")
    public String showBorrowForm(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("members", memberRepository.findAll());
        return "add-borrow";
    }

    // ফর্ম থেকে ডাটা এসে বই ধার দেওয়া সেভ হওয়া
    @PostMapping("/borrow/save")
    public String saveBorrow(@RequestParam Long bookId,
                              @RequestParam Long memberId,
                              @RequestParam int days) {

        Book book = bookRepository.findById(bookId).orElseThrow();
        Member member = memberRepository.findById(memberId).orElseThrow();

        if (book.getQuantity() <= 0) {
            return "redirect:/borrow/add?error=nostock";
        }

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setMember(member);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(days));
        record.setReturned(false);

        borrowRecordRepository.save(record);

        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        return "redirect:/borrow";
    }

    // বই ফেরত দেওয়া (Return বাটনে ক্লিক করলে)
    @GetMapping("/borrow/return/{id}")
    public String returnBook(@PathVariable Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id).orElseThrow();
        record.setReturned(true);
        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);

        Book book = record.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        return "redirect:/borrow";
    }
}