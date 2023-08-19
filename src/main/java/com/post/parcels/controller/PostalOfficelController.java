package com.post.parcels.controller;

import com.post.parcels.model.dto.ArrivalParcelDto;
import com.post.parcels.model.dto.DepartureParcelDto;
import com.post.parcels.model.dto.RegisterParcelDto;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
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
public class PostalOfficelController {
    private final MainService mainService;

    public PostalOfficelController(MainService mainService) {
        this.mainService = mainService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/postalOffices",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<List<PostalOffice>> registerPostalOffices(
            @NotNull @Valid @RequestBody List<PostalOffice> postalOffices
    ) {
        return ResponseEntity.ok(mainService.registerPostalOffices(postalOffices));
    }


}
