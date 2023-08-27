package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("transfer_id")
    private Long id;

    @Valid
    @NotNull
    @Column(name = "departure_date", nullable = false)
    @JsonProperty("departure_date")
    private OffsetDateTime departureDate;

    @Valid
    @Column(name = "arrival_date")
    @JsonProperty("arrival_date")
    private OffsetDateTime arrivalDate;

    @Valid
    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", referencedColumnName = "id")
    @JsonBackReference
    private Parcel parcel;

    @Valid
    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_index", referencedColumnName = "index")
    @JsonManagedReference
    @JsonProperty("departure_index")
    private PostalOffice departurePostalOffice;

    @Valid
    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_index", referencedColumnName = "index")
    @JsonManagedReference
    @JsonProperty("arrival_index")
    private PostalOffice arrivalPostalOffice;

    public Transfer() {
    }
}
