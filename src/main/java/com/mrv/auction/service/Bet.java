package com.mrv.auction.service;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static java.lang.Thread.sleep;

@Data
@Slf4j
public class Bet implements Runnable {

    private int minPrice;
    private Ad ad;
    private AdService adService;
    private Map<Long, Bet> adsInUse;
    private User user;

    public Bet(int minPrice, Ad ad, AdService adService, Map<Long, Bet> adsInUse, User user) {
        this.minPrice = minPrice;
        this.ad = ad;
        this.adService = adService;
        this.adsInUse = adsInUse;
        this.user = user;
    }

    public synchronized boolean raisePrice(int minPrice, User user) {
        if (this.minPrice < minPrice) {
            log.info("Пользователь {} перебил ставку {} пользователя {}. Минимальная ставка {}",
                    user.getId(), this.minPrice, this.user.getId(), minPrice );
            this.minPrice = minPrice;
            this.user = user;
            adService.changePrice(ad, minPrice);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        try {
            sleep(ad.getTimer());
        } catch (InterruptedException e) {
            e.getMessage();
        }
        adService.changeStatus(ad);
        adsInUse.remove(ad.getId());
        log.info("Аукцион товара с id {} завершён. Победила ставка {} пользователя с id {}. ({} -> {})",
                ad.getId(), ad.getMinPrice(),  user.getId(), ad.getStartPrice(), ad.getMinPrice());
    }

}
