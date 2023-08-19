package com.post.parcels.exceptions;

public class ActiveTransferNotFoundException extends BusinessException {
    public ActiveTransferNotFoundException(Long parcelId) {
        super(String.format("Could not find active transfer for parcel id '%s'", parcelId));
    }
}
