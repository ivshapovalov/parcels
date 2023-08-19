package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private long id;

    @Valid
    @Column(name = "arrival_time", nullable = false)
    @JsonProperty("arrival_time")
    private OffsetDateTime arrivalTime;

    @Valid
    @NotNull
    @Column(name = "departure_time", nullable = false)
    @JsonProperty("departure_time")
    private OffsetDateTime departureTime;

    @Valid
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", referencedColumnName = "id")
    @JsonIgnore
    @JsonBackReference
    private Parcel parcel;

    @Valid
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "index_sender", referencedColumnName = "index")
    @JsonIgnore
    @JsonBackReference
    @JsonProperty("index_sender")
    private PostalOffice sender;

    @Valid
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "index_receiver", referencedColumnName = "index")
    @JsonIgnore
    @JsonBackReference
    @JsonProperty("index_receiver")
    private PostalOffice receiver;

    public Transfer() {
    }


}
