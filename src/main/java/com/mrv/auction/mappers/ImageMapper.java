package com.mrv.auction.mappers;

import com.mrv.auction.dto.image.ImageDto;
import com.mrv.auction.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper extends Mappable<Image, ImageDto>{
}
