package com.post.parcels.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterParcelDto {

    @Valid
    @NotNull
    @JsonProperty("parcel_type")
    private Parcel.Type type;

    @Valid
    @NotEmpty
    @NotNull
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @Valid
    @NotEmpty
    @NotNull
    @JsonProperty("receiver_name")
    private String receiverName;

    @Valid
    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @JsonProperty("receiver_index")
    private String receiverIndex;

    public Parcel toParcel(PostalOffice postalOffice) {
        Parcel parcel = new Parcel();
        parcel.setType(this.type);
        parcel.setStatus(Parcel.Status.ACCEPTED);
        parcel.setReceiverAddress(this.receiverAddress);
        parcel.setReceiverName(this.receiverName);
        parcel.setReceiverPostalOffice(postalOffice);
        return parcel;
    }
}
