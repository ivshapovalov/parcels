package com.post.parcels.exceptions;

import com.post.parcels.model.entity.Parcel;

public class ParcelNotFoundAtPostalOfficeException extends BusinessException {
    public ParcelNotFoundAtPostalOfficeException(Long parcelId, String postalIndex, Parcel.Status status) {
        super(String.format("Could not find parcel with id '%s' at postal office '%s' with status '%s'", parcelId, postalIndex, status));
    }
}
