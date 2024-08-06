package com.mrv.auction.mappers;


import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto>{
}
