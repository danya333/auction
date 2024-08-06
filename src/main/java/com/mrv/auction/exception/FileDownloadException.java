package com.mrv.auction.exception;

public class FileDownloadException extends RuntimeException {

    public FileDownloadException(
            final String message
    ) {
        super(message);
    }

}