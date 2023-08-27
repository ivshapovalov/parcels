package com.post.parcels.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReceiveParcelDto {

    @Valid
    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    @JsonProperty("receiver_index")
    private String receiverIndex;

}
