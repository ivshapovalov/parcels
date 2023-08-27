package com.post.parcels.service;

import com.post.parcels.exceptions.*;
import com.post.parcels.model.dto.*;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.model.entity.Transfer;
import com.post.parcels.repository.ParcelRepository;
import com.post.parcels.repository.PostalOfficeRepository;
import com.post.parcels.repository.TransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MainService {

    private final ParcelRepository parcelRepository;
    private final TransferRepository transferRepository;
    private final PostalOfficeRepository postalOfficeRepository;

    public List<Parcel> registerParcels(List<RegisterParcelDto> registerParcelDtoList) {

        List<Parcel> parcels = registerParcelDtoList.stream().map(registerParcelDto -> {
            String acceptancePostalIndex = registerParcelDto.getAcceptanceIndex();
            PostalOffice acceptancePostalOffice = findPostalOffice(acceptancePostalIndex);

            String receiverPostalIndex = registerParcelDto.getReceiverIndex();
            PostalOffice receiverPostalOffice = findPostalOffice(receiverPostalIndex);

            Parcel parcel = registerParcelDto.toParcel(acceptancePostalOffice, receiverPostalOffice);
            parcel.setAcceptanceDate(OffsetDateTime.now());
            parcel.setCurrentPostalOffice(acceptancePostalOffice);
            return parcel;
        }).collect(Collectors.toList());

        return parcelRepository.saveAll(parcels);
    }

    public Transfer departParcel(Long parcelId, DepartureParcelDto departureParcelDto) {
        PostalOffice departurePostalOffice = findPostalOffice(departureParcelDto.getDepartureIndex());
        PostalOffice arrivalPostalOffice = findPostalOffice(departureParcelDto.getArrivalIndex());
        Parcel parcel = findParcel(parcelId);
        if (departurePostalOffice.equals(parcel.getCurrentPostalOffice())) {

            //change current location
            parcel.clearCurrentLocation();

            //change transfers
            parcel.setStatus(Parcel.Status.IN_TRANSIT);
            Transfer transfer = new Transfer();
            transfer.setParcel(parcel);
            transfer.setDepartureDate(OffsetDateTime.now());
            transfer.setDeparturePostalOffice(departurePostalOffice);
            transfer.setArrivalPostalOffice(arrivalPostalOffice);
            return transferRepository.save(transfer);
        } else {
            throw new ParcelNotFoundAtPostalOfficeException(parcelId, departureParcelDto.getDepartureIndex(), Parcel.Status.AT_POSTAL_OFFICE);
        }
    }

    public Transfer arriveParcel(Long parcelId, ArrivalParcelDto arrivalParcelDto) {

        PostalOffice arrivalPostalOffice = findPostalOffice(arrivalParcelDto.getArrivalIndex());
        Parcel parcel = findParcel(parcelId);
        if (Parcel.Status.IN_TRANSIT.equals(parcel.getStatus())) {

            //change transfers
            List<Transfer> transfers = transferRepository.findByParcelIsAndArrivalDateIsNull(parcel);
            if (transfers.isEmpty()) {
                throw new ActiveTransferNotFoundException(parcelId);
            } else if (transfers.size() > 1) {
                throw new ManyActiveTransfersFoundException(parcelId);
            }
            Transfer transfer = transfers.get(0);
            OffsetDateTime arrivalTime = OffsetDateTime.now();
            if (transfer.getDepartureDate().isAfter(arrivalTime)) {
                throw new ParcelTransferTimeInvalidException(parcelId,
                        transfer.getDepartureDate(), arrivalTime);
            }

            //change current location
            parcel.setCurrentPostalOffice(arrivalPostalOffice);
            if (arrivalPostalOffice.equals(parcel.getReceiverPostalOffice())) {
                parcel.setStatus(Parcel.Status.WAITING_FOR_RECEIVING);
            } else {
                parcel.setStatus(Parcel.Status.AT_POSTAL_OFFICE);
            }
            parcelRepository.save(parcel);

            transfer.setArrivalDate(arrivalTime);
            transfer.setArrivalPostalOffice(arrivalPostalOffice);
            transferRepository.save(transfer);
            return transfer;
        } else {
            throw new ParcelWithStatusNotFoundException(parcelId, Parcel.Status.IN_TRANSIT);
        }
    }

    public Parcel receiveParcel(Long parcelId, ReceiveParcelDto receiveParcelDto) {
        PostalOffice postalOffice = findPostalOffice(receiveParcelDto.getReceiverIndex());
        Parcel parcel = findParcel(parcelId);
        if (Parcel.Status.WAITING_FOR_RECEIVING.equals(parcel.getStatus())
                && parcel.getReceiverPostalOffice().equals(postalOffice)
                && parcel.getCurrentPostalOffice().equals(postalOffice)) {
            parcel.setStatus(Parcel.Status.RECEIVED);
            parcel.setReceiveDate(OffsetDateTime.now());
            parcel.clearCurrentLocation();
            return parcelRepository.save(parcel);
        } else {
            throw new ParcelNotFoundAtPostalOfficeException(parcelId, postalOffice.getIndex(), Parcel.Status.WAITING_FOR_RECEIVING);
        }
    }

    public List<Parcel> getParcels() {
        return parcelRepository.findAll();
    }

    public List<PostalOffice> registerPostalOffices(List<PostalOffice> postalOffices) {
        return postalOfficeRepository.saveAll(postalOffices);
    }

    public ParcelHistoryDto getParcelHistory(Long parcelId) {
        Parcel parcel = findParcel(parcelId);
        ParcelHistoryDto parcelHistoryDto = new ParcelHistoryDto();
        parcelHistoryDto.setParcel(parcel);
        List<Transfer> sortedTransfers = parcel.getTransfers().stream()
                .sorted((t1, t2) -> {
                    if (t1.getArrivalDate() != null && t2.getArrivalDate() != null) {
                        return t1.getArrivalDate().compareTo(t2.getArrivalDate());
                    } else if (t1.getArrivalDate() == null) {
                        return 1;
                    } else {
                        return -1;
                    }
                }).collect(Collectors.toList());
        parcelHistoryDto.setTransfers(sortedTransfers);
        return parcelHistoryDto;
    }

    private Parcel findParcel(Long parcelId) {
        Optional<Parcel> existedParcel = parcelRepository.findById(parcelId);
        return existedParcel.orElseThrow(() -> new ParcelNotFoundException(parcelId));
    }


    private PostalOffice findPostalOffice(String postalIndex) {
        Optional<PostalOffice> existedPostalOffice = postalOfficeRepository.findById(postalIndex);
        return existedPostalOffice.orElseThrow(() -> new PostalOfficeNotFoundException(postalIndex));
    }

}
