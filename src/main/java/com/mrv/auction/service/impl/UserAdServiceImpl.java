package com.mrv.auction.service.impl;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.User;
import com.mrv.auction.model.UserAd;
import com.mrv.auction.repository.UserAdRepository;
import com.mrv.auction.service.UserAdService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserAdServiceImpl implements UserAdService {

    private final UserAdRepository userAdRepository;


    @Override
    @Transactional
    public UserAd createUserAd(User user, Ad ad, int price) {
        UserAd userAd = new UserAd();
        userAd.setUser(user);
        userAd.setAd(ad);
        userAd.setMinPrice(price);
        userAdRepository.save(userAd);
        return userAd;
    }

    @Override
    public UserAd getUserAd(Ad ad) {
        return userAdRepository.findByAd(ad)
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));
    }

    @Override
    public List<UserAd> getUserAdsByAd(Ad ad) {
        return userAdRepository.findAllByAd(ad)
                .orElseThrow(() -> new NoSuchElementException("Ads not found"));
    }
}
