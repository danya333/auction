package com.mrv.auction.repository;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.UserAd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAdRepository extends JpaRepository<UserAd, Long> {

    Optional<List<UserAd>> findAllByAd(Ad ad);
    Optional<UserAd> findByAd(Ad ad);
}
