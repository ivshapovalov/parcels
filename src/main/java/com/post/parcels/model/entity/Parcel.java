package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parcels")
@Data
@EqualsAndHashCode(of = "id")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty("parcel_id")
    private long id;

    @Valid
    @NotNull
    @Column(name = "receiver_address", nullable = false)
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @Valid
    @NotNull
    @Column(name = "receiver_name", nullable = false)
    @JsonProperty("receiver_name")
    private String receiverName;

    @NotNull
    @Column(name = "parcel_type", nullable = false)
    @JsonProperty("parcel_type")
    private Parcel.Type type;

    @Column(name = "status", nullable = false)

    @Valid
    @NotNull
    @JsonProperty("status")
    private Status status;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(name = "index", nullable = false)
    private PostalOffice receiverPostalOffice;

    @Valid
    @OneToMany(mappedBy = "parcel", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Transfer> transfers;

    public Parcel() {
        transfers=new HashSet<>();
    }

    public enum Type {
        LETTER("Письмо"),
        PACKAGE("Посылка"),
        BANDEROLE("Бандероль"),
        POSTCARD("Открытка");
        String name;

        Type(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }

    public enum Status {
        ACCEPTED("Принято в отделении связи"),
        IN_TRANSIT("В пути между пунктами"),
        AT_POSTAL_OFFICE("В почтовом офисе"),
        WAITING_FOR_RECEIVING("Ожидает в месте вручения"),
        RECEIVED("Получено");
        String name;

        Status(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }

}
