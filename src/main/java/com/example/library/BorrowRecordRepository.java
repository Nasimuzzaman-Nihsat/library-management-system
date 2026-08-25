package com.example.library;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    long countByMemberIdAndReturnedFalse(Long memberId);
    void deleteByMemberId(Long memberId);

    long countByBookIdAndReturnedFalse(Long bookId);
    void deleteByBookId(Long bookId);
}