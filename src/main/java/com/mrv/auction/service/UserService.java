package com.mrv.auction.service;

import com.mrv.auction.model.User;

public interface UserService {

    User getCurrentUser();

    User getById(
            Long id
    );

    User getByUsername(
            String username
    );

    User update(
            Long id,
            User user
    );

    User create(
            User user
    );

    void delete( Long id);
}
