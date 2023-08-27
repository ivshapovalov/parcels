package com.post.parcels.exceptions;

public class ManyActiveTransfersFoundException extends BusinessException {
    public ManyActiveTransfersFoundException(Long parcelId) {
        super(String.format("Found many active transfers for parcel id '%s'", parcelId));
    }
}
