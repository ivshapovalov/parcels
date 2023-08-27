package com.post.parcels.model.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parcels")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "transfers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty("parcel_id")
    private long id;

    @Valid
    @NotBlank
    @Column(name = "receiver_address", nullable = false)
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @Valid
    @NotBlank
    @Column(name = "receiver_name", nullable = false)
    @JsonProperty("receiver_name")
    private String receiverName;

    @Valid
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "parcel_type", nullable = false)
    @JsonProperty("parcel_type")
    private Parcel.Type type;

    @Valid
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonProperty("status")
    private Status status;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(name = "acceptance_index", nullable = false)
    private PostalOffice acceptancePostalOffice;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(name = "receiver_index", nullable = false)
    private PostalOffice receiverPostalOffice;

    @Valid
    @OneToMany(mappedBy = "parcel", cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonIgnore
    private Set<Transfer> transfers;

    @Valid
    @ManyToOne
    @JoinColumn(name = "current_index", referencedColumnName = "index")
    @JsonManagedReference
    private PostalOffice currentPostalOffice;

    @Valid
    @NotNull
    @Column(name = "acceptance_date", nullable = false)
    @JsonProperty("acceptance_date")
    private OffsetDateTime acceptanceDate;

    @Valid
    @Column(name = "receive_date")
    @JsonProperty("receive_date")
    private OffsetDateTime receiveDate;

    public Parcel() {
        transfers = new HashSet<>();
    }

    public void clearCurrentLocation() {
        this.currentPostalOffice = null;
    }

    public enum Type {
        LETTER("Письмо"),
        PACKAGE("Посылка"),
        BANDEROLE("Бандероль"),
        POSTCARD("Открытка");
        private final String name;

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
        private final String name;

        Status(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
