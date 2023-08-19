package com.post.parcels.exceptions;

public class ParcelNotFoundException extends BusinessException {
    public ParcelNotFoundException(Long parcelId) {
        super(String.format("Could not find parcel with id '%s'", parcelId));
    }
}
