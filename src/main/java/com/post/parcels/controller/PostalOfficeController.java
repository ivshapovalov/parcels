package com.post.parcels.controller;

import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.service.MainService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class PostalOfficeController {
    private final MainService mainService;

    public PostalOfficeController(MainService mainService) {
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
