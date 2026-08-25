package com.example.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {

    @Autowired
private BookRepository bookRepository;

@Autowired
private BorrowRecordRepository borrowRecordRepository;

    // সব বই দেখানো
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "books"; // books.html খুঁজবে
    }

    // নতুন বই যোগ করার ফর্ম দেখানো
    @GetMapping("/books/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book"; // add-book.html খুঁজবে
    }

    // ফর্ম থেকে ডাটা এসে সেভ হওয়া
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book) {
        bookRepository.save(book);
        return "redirect:/books"; // সেভ করার পর লিস্ট পেইজে ফিরে যাবে
    }
    @GetMapping("/books/delete/{id}")
public String deleteBook(@org.springframework.web.bind.annotation.PathVariable Long id,
                          org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

    long unreturnedCount = borrowRecordRepository.countByBookIdAndReturnedFalse(id);

    if (unreturnedCount > 0) {
        redirectAttributes.addFlashAttribute("error",
            unreturnedCount + " copy(ies) of this book are still borrowed and must be returned before deletion.");
        return "redirect:/books";
    }

    borrowRecordRepository.deleteByBookId(id);
    bookRepository.deleteById(id);
    return "redirect:/books";
}
}