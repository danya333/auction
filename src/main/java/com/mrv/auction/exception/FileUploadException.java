package com.mrv.auction.exception;

public class FileUploadException extends RuntimeException {

    public FileUploadException(
            final String message
    ) {
        super(message);
    }

}