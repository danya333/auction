package com.mrv.auction.repository;

import com.mrv.auction.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<List<Image>> findAllByAdId(Long adId);
}
