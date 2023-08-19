package com.post.parcels.service;

import com.post.parcels.exceptions.ActiveTransferNotFoundException;
import com.post.parcels.exceptions.ManyActiveTransfersFoundException;
import com.post.parcels.exceptions.ParcelNotFoundException;
import com.post.parcels.exceptions.PostalOfficeNotFoundException;
import com.post.parcels.model.dto.ArrivalParcelDto;
import com.post.parcels.model.dto.DepartureParcelDto;
import com.post.parcels.model.dto.RegisterParcelDto;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.model.entity.Transfer;
import com.post.parcels.repository.ParcelRepository;
import com.post.parcels.repository.PostalOfficeRepository;
import com.post.parcels.repository.TransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MainService {

    private final ParcelRepository parcelRepository;
    private final TransferRepository transferRepository;
    private final PostalOfficeRepository postalOfficeRepository;

    public Parcel registerParcel(RegisterParcelDto registerParcelDto) {
        String postalIndex = registerParcelDto.getReceiverIndex();
        PostalOffice postalOffice = findPostalOffice(postalIndex);
        Parcel parcel = registerParcelDto.toParcel(postalOffice);
        return parcelRepository.save(parcel);
    }

    public List<Transfer> getParcelTransfers(Long parcelId) {
        Parcel parcel = findParcel(parcelId);
        return transferRepository.findByParcel(parcel);
    }

    public Transfer departParcel(Long parcelId, DepartureParcelDto departureParcelDto) {
        Parcel parcel = findParcel(parcelId);
        parcel.setStatus(Parcel.Status.IN_TRANSIT);
        Transfer transfer = new Transfer();
        transfer.setParcel(parcel);
        transfer.setDepartureTime(departureParcelDto.getDepartureTime());
        transfer.setSender(findPostalOffice(departureParcelDto.getSenderIndex()));
        transfer.setReceiver(findPostalOffice(departureParcelDto.getReceiverIndex()));
        return transferRepository.save(transfer);
    }

    public Transfer arriveParcel(Long parcelId, ArrivalParcelDto arrivalParcelDto) {
        PostalOffice receiverPostalOffice = findPostalOffice(arrivalParcelDto.getReceiverIndex());
        Parcel parcel = findParcel(parcelId);
        if (parcel.getReceiverPostalOffice().equals(receiverPostalOffice)) {
            parcel.setStatus(Parcel.Status.WAITING_FOR_RECEIVING);
        } else {
            parcel.setStatus(Parcel.Status.AT_POSTAL_OFFICE);
        }
        parcelRepository.save(parcel);
        List<Transfer> transfers = transferRepository.findByParcelIsAndArrivalTimeIsNull(parcel);
        if (transfers.isEmpty()) {
            throw new ActiveTransferNotFoundException(parcelId);
        } else if (transfers.size() > 1) {
            throw new ManyActiveTransfersFoundException(parcelId);
        }

        Transfer transfer = transfers.get(0);
        transfer.setArrivalTime(arrivalParcelDto.getArrivalTime());
        transfer.setReceiver(receiverPostalOffice);
        transferRepository.save(transfer);
        return transfer;
    }

    public Parcel receiveParcel(Long parcelId) {
        Parcel parcel = findParcel(parcelId);
        parcel.setStatus(Parcel.Status.RECEIVED);
        return parcel;
    }

    private Parcel findParcel(Long parcelId) {
        Optional<Parcel> existedParcel = parcelRepository.findById(parcelId);
        return existedParcel.orElseThrow(() -> new ParcelNotFoundException(parcelId));
    }

    private PostalOffice findPostalOffice(String postalIndex) {
        Optional<PostalOffice> existedPostalOffice = postalOfficeRepository.findById(postalIndex);
        return existedPostalOffice.orElseThrow(() -> new PostalOfficeNotFoundException(postalIndex));
    }


    public List<Parcel> getParcels() {
        return parcelRepository.findAll();
    }

    public List<PostalOffice> registerPostalOffices(List<PostalOffice> postalOffices) {
        return postalOfficeRepository.saveAll(postalOffices);
    }
}
