package com.mrv.auction.repository;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import com.mrv.auction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    Optional<List<Ad>> findAllByStatus(Status status);
    Optional<List<Ad>> findAllByUserAndStatus(User user, Status status);
}
