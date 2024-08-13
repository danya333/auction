package com.mrv.auction.service.impl;

import com.mrv.auction.service.Bet;
import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import com.mrv.auction.model.User;
import com.mrv.auction.repository.AdRepository;
import com.mrv.auction.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final UserAdService userAdService;

    private final Map<Long, Bet> adsInUse = new HashMap<>();

    @Override
    public String raiseThePrice(Ad ad, Integer price) {
        if(!adsInUse.containsKey(ad.getId())) {
            Bet bet = new Bet(price, ad, this, adsInUse, userService.getCurrentUser());
            Thread t1 = new Thread(bet);
            t1.start();
            log.info("Auction for ad with id: {} is started with the price {}", ad.getId(), price);
            adsInUse.put(ad.getId(), bet);
            userAdService.createUserAd(userService.getCurrentUser(), ad, price);
            return "The price has been raised to " + price;
        } else{
            boolean raiseResult = adsInUse.get(ad.getId()).raisePrice(price, userService.getCurrentUser());
            if(raiseResult) {
                this.changePrice(ad, price);
                userAdService.createUserAd(userService.getCurrentUser(), ad, price);
                return "The price of ad " + ad.getName() + " is raised to " + price;
            } else {
                return "The price of ad " + ad.getName() + " must be greater than " +
                        adsInUse.get(ad.getId()).getMinPrice();
            }
        }
    }

    @Override
    public void changeStatus(Ad ad) {
        ad.setStatus(Status.FINISHED);
        adRepository.save(ad);
    }

    @Override
    public void changePrice(Ad ad, int minPrice) {
        ad.setMinPrice(minPrice);
        adRepository.save(ad);
    }

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
        ad.setMinPrice(ad.getStartPrice());
        ad = adRepository.save(ad);
        for (MultipartFile image : images) {
            ad.getImages().add(imageService.create(ad, image));
        }
        adRepository.save(ad);
        log.info("Created ad with id: {}", ad.getId());
        return ad;
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
        log.info("Updated ad with id: {}", ad.getId());
        return existingAd;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(this.getAd(id) != null) {
            adRepository.deleteById(id);
            log.info("Deleted ad with id: {}", id);
        } else {
            throw new NoSuchElementException("No ad found with id: " + id);
        }
    }
}
