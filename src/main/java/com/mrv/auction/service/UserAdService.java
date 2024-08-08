package com.mrv.auction.service;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.User;
import com.mrv.auction.model.UserAd;

import java.util.List;

public interface UserAdService {

    UserAd createUserAd(User user, Ad ad, int price);

    UserAd getUserAd(Ad ad);

    List<UserAd> getUserAdsByAd(Ad ad);
}
