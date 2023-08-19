package com.post.parcels.repository;

import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByParcel(Parcel parcel);

    List<Transfer> findByParcelIsAndArrivalTimeIsNull(Parcel parcel);
}
