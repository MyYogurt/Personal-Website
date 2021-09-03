package org.moisiadis.website.util;

public enum ResponseType {
    /**
     * Invalid Request. Error code 418
     */
    INVALID_REQUEST,
    /**
     * File not found. Error code 404
     */
    FILE_NOT_FOUND,
    /**
     * Too many requests. Error code 429
     */
    TOO_MANY_REQUESTS;
}
