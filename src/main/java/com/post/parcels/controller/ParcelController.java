package com.post.parcels.controller;

import com.post.parcels.model.dto.ArrivalParcelDto;
import com.post.parcels.model.dto.DepartureParcelDto;
import com.post.parcels.model.dto.RegisterParcelDto;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.Transfer;
import com.post.parcels.service.MainService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ParcelController {

    private final MainService mainService;

    public ParcelController(MainService mainService) {
        this.mainService = mainService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/parcels",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Parcel> registerParcel(
            @NotNull @Valid @RequestBody RegisterParcelDto registerParcelDto
    ) {
        return ResponseEntity.ok(mainService.registerParcel(registerParcelDto));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/parcels/{parcel_id}/transfers",
            produces = {"application/json"}
    )
    ResponseEntity<List<Transfer>> getParcelTransfers(
            @PathVariable(value = "parcel_id") @NotNull @Valid Long parcelId
    ) {
        return ResponseEntity.ok(mainService.getParcelTransfers(parcelId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/parcels",
            produces = {"application/json"}
    )
    ResponseEntity<List<Parcel>> getParcels(    ) {
        return ResponseEntity.ok(mainService.getParcels());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/parcels/{parcel_id}/depart",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<Transfer> depart(
            @PathVariable(value = "parcel_id") @NotNull @Valid Long parcelId,
            @NotNull @Valid @RequestBody DepartureParcelDto departureParcelDto
    ) {
        return ResponseEntity.ok(mainService.departParcel(parcelId, departureParcelDto));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/parcels/{parcel_id}/arrive",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<Transfer> arrive(
            @PathVariable(value = "parcel_id") @NotNull @Valid Long parcelId,
            @NotNull @Valid @RequestBody ArrivalParcelDto arrivalParcelDto
    ) {
        return ResponseEntity.ok(mainService.arriveParcel(parcelId, arrivalParcelDto));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/parcels/{parcel_id}/receive",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<Parcel> receive(
            @PathVariable(value = "parcel_id") @NotNull @Valid Long parcelId
    ) {
        return ResponseEntity.ok(mainService.receiveParcel(parcelId));
    }

}
