package com.post.parcels.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Data
public class ArrivalParcelDto {

    @Valid
    @NotNull
    @JsonProperty("parcel_id")
    private Long id;

    @Valid
    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @JsonProperty("receiver_index")
    private String receiverIndex;

    @Valid
    @NotNull
    @JsonProperty("arrival_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime arrivalTime;

}
