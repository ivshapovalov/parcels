package com.post.parcels.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterParcelDto {

    @Valid
    @NotNull
    @JsonProperty("parcel_type")
    @Enumerated(EnumType.STRING)
    private Parcel.Type type;

    @Valid
    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    @JsonProperty("acceptance_index")
    private String acceptanceIndex;

    @Valid
    @NotBlank
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @Valid
    @NotBlank
    @JsonProperty("receiver_name")
    private String receiverName;

    @Valid
    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    @JsonProperty("receiver_index")
    private String receiverIndex;

    public Parcel toParcel(PostalOffice acceptancePostalOffice, PostalOffice receiverPostalOffice) {
        Parcel parcel = new Parcel();
        parcel.setType(this.type);
        parcel.setAcceptancePostalOffice(acceptancePostalOffice);
        parcel.setStatus(Parcel.Status.ACCEPTED);
        parcel.setReceiverAddress(this.receiverAddress);
        parcel.setReceiverName(this.receiverName);
        parcel.setReceiverPostalOffice(receiverPostalOffice);
        return parcel;
    }
}
