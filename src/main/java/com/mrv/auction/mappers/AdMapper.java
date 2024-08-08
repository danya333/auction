package com.mrv.auction.mappers;

import com.mrv.auction.dto.ad.AdDto;
import com.mrv.auction.model.Ad;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdMapper extends Mappable<Ad, AdDto>{
}
