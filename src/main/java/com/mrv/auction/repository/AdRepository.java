package com.mrv.auction.repository;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    Optional<Page<Ad>> findAllByStatus(Status status, Pageable pageable);
}
