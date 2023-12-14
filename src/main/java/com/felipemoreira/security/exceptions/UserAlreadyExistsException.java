package com.felipemoreira.security.exceptions;

import jakarta.persistence.EntityExistsException;

public class UserAlreadyExistsException extends EntityExistsException {

    public UserAlreadyExistsException(String username) {
        super(String.format("User with username %s already exists !", username));
    }
}
