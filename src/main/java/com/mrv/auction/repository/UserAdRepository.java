package com.mrv.auction.repository;

import com.mrv.auction.model.UserAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdRepository extends JpaRepository<UserAd, Long> {
}
