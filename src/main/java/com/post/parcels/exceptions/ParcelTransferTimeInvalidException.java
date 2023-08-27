package com.post.parcels.exceptions;

import java.time.OffsetDateTime;

public class ParcelTransferTimeInvalidException extends BusinessException {
    public ParcelTransferTimeInvalidException(Long parcelId, OffsetDateTime before, OffsetDateTime after) {
        super(String.format("Parcel '%s' transfer time '%s' greater than '%s'", parcelId, before, after));
    }
}
