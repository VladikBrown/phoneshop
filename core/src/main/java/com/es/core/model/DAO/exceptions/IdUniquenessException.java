package com.es.core.model.DAO.exceptions;

public class IdUniquenessException extends RuntimeException {

    public IdUniquenessException(Number key, int size) {
        super("Id: " + key + " isn't unique." + size + " matches found");
    }

    public IdUniquenessException(String key, int size) {
        super("Id: " + key + " isn't unique." + size + " matches found");
    }
}
