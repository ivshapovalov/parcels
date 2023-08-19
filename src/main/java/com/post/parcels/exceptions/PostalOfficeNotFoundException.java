package com.post.parcels.exceptions;

import static java.lang.String.format;

public class PostalOfficeNotFoundException extends BusinessException {
    public PostalOfficeNotFoundException(String postalIndex) {
        super(format("Could not find postal office with index '%s'", postalIndex));
    }
}
