package com.post.parcels.model.dto;

import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParcelHistoryDto {

    Parcel parcel;

    List<Transfer> transfers;

    public ParcelHistoryDto() {
    }
}
