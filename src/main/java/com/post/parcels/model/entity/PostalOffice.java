package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Entity
@Table(name = "postal_offices")
@Data
@EqualsAndHashCode(of = "id")
public class PostalOffice {

    @Id
    @Valid
    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @Column(name = "index")
    @JsonProperty("index")
    private String id;

    @Valid
    @NotNull
    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @Valid
    @NotNull
    @Column(name = "address", nullable = false)
    @JsonProperty("address")
    private String address;

    public PostalOffice() {

    }
}
