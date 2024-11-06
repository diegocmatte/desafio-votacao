package com.example.desafiobackvotos.exception;

import org.springframework.web.client.RestClientException;

import java.io.Serial;

public class DocumentAlreadyExistsException extends RestClientException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DocumentAlreadyExistsException(String msg) {
        super(msg);
    }
}
