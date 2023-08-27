package com.post.parcels.exceptions;

import com.post.parcels.model.entity.Parcel;

public class ParcelWithStatusNotFoundException extends BusinessException {
    public ParcelWithStatusNotFoundException(Long parcelId, Parcel.Status status) {
        super(String.format("Could not find parcel with id '%s' and status '%s'", parcelId, status));
    }
}
