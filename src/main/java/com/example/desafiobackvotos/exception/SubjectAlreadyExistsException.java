package com.example.desafiobackvotos.exception;

import org.springframework.web.client.RestClientException;

import java.io.Serial;

public class SubjectAlreadyExistsException extends RestClientException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SubjectAlreadyExistsException(String msg) {
        super(msg);
    }
}
