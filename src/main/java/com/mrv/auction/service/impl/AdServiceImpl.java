package com.mrv.auction.service.impl;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import com.mrv.auction.model.User;
import com.mrv.auction.repository.AdRepository;
import com.mrv.auction.service.AdService;
import com.mrv.auction.service.ImageService;
import com.mrv.auction.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Override
    public Ad getAd(Long id) {
        return adRepository.findById(id).orElseThrow(()-> new NoSuchElementException("No ad found with id: " + id));
    }

    @Override
    public List<Ad> getAllAds() {
        return adRepository.findAllByStatus(Status.ACTIVE).orElseThrow(()-> new NoSuchElementException("No ads found"));
    }

    @Override
    public List<Ad> getUserAds() {
        User user = userService.getCurrentUser();
        return adRepository.findAllByUserAndStatus(user, Status.ACTIVE).orElseThrow(
                ()-> new NoSuchElementException("No ads found"));
    }

    @Override
    @Transactional
    public Ad create(Ad ad, List<MultipartFile> images) {
        ad.setCreationDate(LocalDateTime.now());
        ad.setStatus(Status.ACTIVE);
        ad.setUser(userService.getCurrentUser());
        ad = adRepository.save(ad);
        for (MultipartFile image : images) {
            ad.getImages().add(imageService.create(ad, image));
        }
        return adRepository.save(ad);
    }

    @Override
    @Transactional
    public Ad update(Ad ad, List<MultipartFile> images) {
        Ad existingAd = this.getAd(ad.getId());
        existingAd.setName(ad.getName());
        existingAd.setDescription(ad.getDescription());
        existingAd.setStartPrice(ad.getStartPrice());
        existingAd.setTimer(ad.getTimer());
        adRepository.save(existingAd);
        return existingAd;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(this.getAd(id) != null) {
            adRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("No ad found with id: " + id);
        }
    }
}
