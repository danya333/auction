package com.mrv.auction.dto;

import com.mrv.auction.model.Ad;
import com.mrv.auction.service.AdService;
import lombok.Data;

import static java.lang.Thread.sleep;

@Data
public class Bet implements Runnable {

    private int minPrice;
    private Ad ad;
    private AdService adService;

    public Bet(int minPrice, Ad ad, AdService adService) {
        this.minPrice = minPrice;
        this.ad = ad;
        this.adService = adService;
    }

    public synchronized boolean raisePrice(int minPrice) {
        if (this.minPrice < minPrice) {
            this.minPrice = minPrice;
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
        System.out.println("Final price " + minPrice + " for ad " + ad.getName());
    }


}
