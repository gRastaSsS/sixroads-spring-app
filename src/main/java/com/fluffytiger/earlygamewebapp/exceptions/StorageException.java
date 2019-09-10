package com.fluffytiger.earlygamewebapp.exceptions;

public class StorageException extends RuntimeException {
    public StorageException(String s) {
        super(s);
    }

    public StorageException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
