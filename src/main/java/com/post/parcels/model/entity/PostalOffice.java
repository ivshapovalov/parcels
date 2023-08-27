package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "postal_offices")
@Data
@EqualsAndHashCode(of = "index")
//@ToString(exclude = "parcels")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostalOffice {

    @Id
    @Valid
    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @Column(name = "index")
    @JsonProperty("index")
    private String index;

    @Valid
    @NotBlank
    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @Valid
    @NotBlank
    @Column(name = "address", nullable = false)
    @JsonProperty("address")
    private String address;

    public PostalOffice() {

    }

    public PostalOffice(@NotNull String index) {
        this.index = index;
    }


}
