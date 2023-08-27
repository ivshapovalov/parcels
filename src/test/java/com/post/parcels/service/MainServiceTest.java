package com.post.parcels.service;

import com.post.parcels.CommonTest;
import com.post.parcels.exceptions.*;
import com.post.parcels.model.dto.*;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.model.entity.Transfer;
import com.post.parcels.repository.ParcelRepository;
import com.post.parcels.repository.PostalOfficeRepository;
import com.post.parcels.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
public class MainServiceTest extends CommonTest {

    @Autowired
    @InjectMocks
    private MainService mainService;

    @MockBean
    private TransferRepository transferRepository;

    @MockBean
    private ParcelRepository parcelRepository;

    @MockBean
    private PostalOfficeRepository postalOfficeRepository;

    @Test
    public void registerParcelsWhenOneParcelOk() {
        String acceptanceIndex = "000001";
        String receiverIndex = "000002";
        RegisterParcelDto registerParcelDto = new RegisterParcelDto();
        registerParcelDto.setType(Parcel.Type.BANDEROLE);
        registerParcelDto.setAcceptanceIndex(acceptanceIndex);
        registerParcelDto.setReceiverIndex(receiverIndex);
        registerParcelDto.setReceiverAddress("Moscow 000002");
        registerParcelDto.setReceiverName("Name");

        PostalOffice acceptancePostalOffice = new PostalOffice(acceptanceIndex);
        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);

        Parcel parcel = registerParcelDto.toParcel(acceptancePostalOffice, receiverPostalOffice);
        parcel.setCurrentPostalOffice(receiverPostalOffice);
        List<Parcel> expected = List.of(parcel);
        when(postalOfficeRepository.findById(acceptanceIndex)).thenReturn(Optional.of(acceptancePostalOffice));
        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));

        when(parcelRepository.saveAll(any(List.class))).thenReturn(expected);

        List<Parcel> actual = mainService.registerParcels(List.of(registerParcelDto));

        verify(postalOfficeRepository).findById(acceptanceIndex);
        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).saveAll(any(List.class));
        ArgumentCaptor<List<Parcel>> captor = ArgumentCaptor.forClass(List.class);
        verify(parcelRepository).saveAll(captor.capture());
        assertIterableEquals(expected, captor.getValue());
        assertIterableEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void registerParcelsWhenTwoParcelsOk() {
        String postalIndex1 = "000001";
        String postalIndex2 = "000002";
        String postalIndex3 = "000003";
        RegisterParcelDto registerParcelDto1 = new RegisterParcelDto();
        registerParcelDto1.setType(Parcel.Type.BANDEROLE);
        registerParcelDto1.setAcceptanceIndex(postalIndex1);
        registerParcelDto1.setReceiverIndex(postalIndex2);
        registerParcelDto1.setReceiverAddress("Moscow 000002");
        registerParcelDto1.setReceiverName("Name1");

        RegisterParcelDto registerParcelDto2 = new RegisterParcelDto();
        registerParcelDto2.setType(Parcel.Type.LETTER);
        registerParcelDto2.setAcceptanceIndex(postalIndex2);
        registerParcelDto2.setReceiverIndex(postalIndex3);
        registerParcelDto2.setReceiverAddress("Moscow 000003");
        registerParcelDto2.setReceiverName("Name2");

        PostalOffice postalOffice1 = new PostalOffice(postalIndex1);
        PostalOffice postalOffice2 = new PostalOffice(postalIndex2);
        PostalOffice postalOffice3 = new PostalOffice(postalIndex3);

        Parcel parcel1 = registerParcelDto1.toParcel(postalOffice1, postalOffice2);
        parcel1.setCurrentPostalOffice(postalOffice1);
        Parcel parcel2 = registerParcelDto2.toParcel(postalOffice2, postalOffice3);
        parcel2.setCurrentPostalOffice(postalOffice2);

        List<Parcel> expected = Arrays.asList(parcel1, parcel2);
        when(postalOfficeRepository.findById(postalIndex1)).thenReturn(Optional.of(postalOffice1));
        when(postalOfficeRepository.findById(postalIndex2)).thenReturn(Optional.of(postalOffice2));
        when(postalOfficeRepository.findById(postalIndex3)).thenReturn(Optional.of(postalOffice3));

        when(parcelRepository.saveAll(any(List.class))).thenReturn(expected);

        List<Parcel> actual = mainService.registerParcels(List.of(registerParcelDto1, registerParcelDto2));

        verify(postalOfficeRepository).findById(postalIndex1);
        verify(postalOfficeRepository, times(2)).findById(postalIndex2);
        verify(postalOfficeRepository).findById(postalIndex3);
        verify(parcelRepository).saveAll(any(List.class));
        ArgumentCaptor<List<Parcel>> captor = ArgumentCaptor.forClass(List.class);
        verify(parcelRepository).saveAll(captor.capture());
        assertIterableEquals(expected, captor.getValue());
        assertIterableEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void registerParcelsWhenAcceptancePostalOfficeNotExistsThrowsException() {
        String postalIndex1 = "000001";
        String postalIndex2 = "000002";
        RegisterParcelDto registerParcelDto1 = new RegisterParcelDto();
        registerParcelDto1.setType(Parcel.Type.BANDEROLE);
        registerParcelDto1.setAcceptanceIndex(postalIndex1);
        registerParcelDto1.setReceiverIndex(postalIndex2);
        registerParcelDto1.setReceiverAddress("Moscow 000002");
        registerParcelDto1.setReceiverName("Name1");

        when(postalOfficeRepository.findById(postalIndex1)).thenReturn(Optional.empty());

        assertThrows(PostalOfficeNotFoundException.class, () -> {
            mainService.registerParcels(List.of(registerParcelDto1));
        });

        verify(postalOfficeRepository).findById(postalIndex1);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void registerParcelsWhenReceiverPostalOfficeNotExistsThrowsException() {
        String postalIndex1 = "000001";
        String postalIndex2 = "000002";
        RegisterParcelDto registerParcelDto1 = new RegisterParcelDto();
        registerParcelDto1.setType(Parcel.Type.BANDEROLE);
        registerParcelDto1.setAcceptanceIndex(postalIndex1);
        registerParcelDto1.setReceiverIndex(postalIndex2);
        registerParcelDto1.setReceiverAddress("Moscow 000002");
        registerParcelDto1.setReceiverName("Name1");

        PostalOffice postalOffice1 = new PostalOffice(postalIndex1);

        when(postalOfficeRepository.findById(postalIndex1)).thenReturn(Optional.of(postalOffice1));
        when(postalOfficeRepository.findById(postalIndex2)).thenReturn(Optional.empty());

        assertThrows(PostalOfficeNotFoundException.class, () -> mainService.registerParcels(List.of(registerParcelDto1)));

        verify(postalOfficeRepository).findById(postalIndex1);
        verify(postalOfficeRepository).findById(postalIndex2);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void departParcelWhenOneParcelOk() {
        String departureIndex = "000001";
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        DepartureParcelDto departureParcelDto = new DepartureParcelDto();
        departureParcelDto.setDepartureIndex(departureIndex);
        departureParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice departurePostalOffice = new PostalOffice(departureIndex);
        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);
        Parcel parcel = new Parcel();
        parcel.setCurrentPostalOffice(departurePostalOffice);

        Transfer expected = new Transfer();
        expected.setParcel(parcel);
        expected.setDeparturePostalOffice(departurePostalOffice);
        expected.setArrivalPostalOffice(arrivalPostalOffice);

        when(postalOfficeRepository.findById(departureIndex)).thenReturn(Optional.of(departurePostalOffice));
        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        when(transferRepository.save(any(Transfer.class))).thenReturn(expected);

        Transfer actual = mainService.departParcel(parcelId, departureParcelDto);

        verify(postalOfficeRepository).findById(departureIndex);
        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(captor.capture());
        assertEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void departureParcelWhenDeparturePostalOfficeNotExistsThrowsException() {
        String departureIndex = "000001";
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        DepartureParcelDto departureParcelDto = new DepartureParcelDto();
        departureParcelDto.setDepartureIndex(departureIndex);
        departureParcelDto.setArrivalIndex(arrivalIndex);

        when(postalOfficeRepository.findById(departureIndex)).thenReturn(Optional.empty());

        assertThrows(PostalOfficeNotFoundException.class, () -> mainService.departParcel(parcelId, departureParcelDto));

        verify(postalOfficeRepository).findById(departureIndex);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void departParcelWhenArrivalPostalOfficeNotExistsThrowsException() {
        String departureIndex = "000001";
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        DepartureParcelDto departureParcelDto = new DepartureParcelDto();
        departureParcelDto.setDepartureIndex(departureIndex);
        departureParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice departurePostalOffice = new PostalOffice(departureIndex);

        when(postalOfficeRepository.findById(departureIndex)).thenReturn(Optional.of(departurePostalOffice));
        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.empty());


        assertThrows(PostalOfficeNotFoundException.class, () -> mainService.departParcel(parcelId, departureParcelDto));

        verify(postalOfficeRepository).findById(departureIndex);
        verify(postalOfficeRepository).findById(arrivalIndex);
        verifyAllMocksNoMoreInteractions();

    }

    @Test
    public void departParcelsWhenParcelNotExistsThrowsException() {
        String departureIndex = "000001";
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        DepartureParcelDto departureParcelDto = new DepartureParcelDto();
        departureParcelDto.setDepartureIndex(departureIndex);
        departureParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice departurePostalOffice = new PostalOffice(departureIndex);
        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        when(postalOfficeRepository.findById(departureIndex)).thenReturn(Optional.of(departurePostalOffice));
        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.empty());

        assertThrows(ParcelNotFoundException.class, () -> mainService.departParcel(parcelId, departureParcelDto));

        verify(postalOfficeRepository).findById(departureIndex);
        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void departParcelWhenParcelNotAtPostalOfficeThrowsException() {
        String departureIndex = "000001";
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        DepartureParcelDto departureParcelDto = new DepartureParcelDto();
        departureParcelDto.setDepartureIndex(departureIndex);
        departureParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice departurePostalOffice = new PostalOffice(departureIndex);
        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setCurrentPostalOffice(arrivalPostalOffice);

        when(postalOfficeRepository.findById(departureIndex)).thenReturn(Optional.of(departurePostalOffice));
        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        assertThrows(ParcelNotFoundAtPostalOfficeException.class, () -> mainService.departParcel(parcelId, departureParcelDto));

        verify(postalOfficeRepository).findById(departureIndex);
        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenArrivalPostalOfficeNotExistsThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.empty());

        assertThrows(PostalOfficeNotFoundException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenParcelNotExistsThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.empty());

        assertThrows(ParcelNotFoundException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenParcelNotInTransitThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.AT_POSTAL_OFFICE);

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        assertThrows(ParcelWithStatusNotFoundException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenTransfersNotFoundThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(transferRepository.findByParcelIsAndArrivalDateIsNull(parcel)).thenReturn(new ArrayList<>());

        assertThrows(ActiveTransferNotFoundException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verify(transferRepository).findByParcelIsAndArrivalDateIsNull(parcel);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenManyActiveTransfersFoundThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);

        Transfer transfer1 = new Transfer();
        transfer1.setParcel(parcel);
        transfer1.setId(1L);

        Transfer transfer2 = new Transfer();
        transfer2.setParcel(parcel);
        transfer2.setId(2L);

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(transferRepository.findByParcelIsAndArrivalDateIsNull(parcel)).thenReturn(List.of(transfer1, transfer2));

        assertThrows(ManyActiveTransfersFoundException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verify(transferRepository).findByParcelIsAndArrivalDateIsNull(parcel);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelWhenTransferDepartureTimeGreaterThanArrivalTimeThrowsException() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);

        Transfer transfer1 = new Transfer();
        transfer1.setParcel(parcel);
        transfer1.setId(1L);
        transfer1.setDepartureDate(OffsetDateTime.now().plusDays(1));

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(transferRepository.findByParcelIsAndArrivalDateIsNull(parcel)).thenReturn(List.of(transfer1));

        assertThrows(ParcelTransferTimeInvalidException.class, () -> mainService.arriveParcel(parcelId, arrivalParcelDto));

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verify(transferRepository).findByParcelIsAndArrivalDateIsNull(parcel);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelAtTransitionalPostalOfficeOk() {
        String arrivalIndex = "000002";
        String receiverIndex = "000003";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);
        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);
        parcel.setReceiverPostalOffice(receiverPostalOffice);

        Transfer expected = new Transfer();
        expected.setParcel(parcel);
        expected.setId(1L);
        expected.setDepartureDate(OffsetDateTime.now());

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(transferRepository.findByParcelIsAndArrivalDateIsNull(parcel)).thenReturn(List.of(expected));
        when(parcelRepository.save(parcel)).thenReturn(parcel);
        when(transferRepository.save(expected)).thenReturn(expected);

        Transfer actual = mainService.arriveParcel(parcelId, arrivalParcelDto);

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verify(transferRepository).findByParcelIsAndArrivalDateIsNull(parcel);
        verify(parcelRepository).save(parcel);
        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(captor.capture());
        assertEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void arriveParcelAtReceiverPostalOfficeOk() {
        String arrivalIndex = "000002";
        Long parcelId = 10L;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex(arrivalIndex);

        PostalOffice arrivalPostalOffice = new PostalOffice(arrivalIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);
        parcel.setReceiverPostalOffice(arrivalPostalOffice);

        Transfer expected = new Transfer();
        expected.setParcel(parcel);
        expected.setId(1L);
        expected.setDepartureDate(OffsetDateTime.now());

        when(postalOfficeRepository.findById(arrivalIndex)).thenReturn(Optional.of(arrivalPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(transferRepository.findByParcelIsAndArrivalDateIsNull(parcel)).thenReturn(List.of(expected));
        when(parcelRepository.save(parcel)).thenReturn(parcel);
        when(transferRepository.save(expected)).thenReturn(expected);

        Transfer actual = mainService.arriveParcel(parcelId, arrivalParcelDto);

        verify(postalOfficeRepository).findById(arrivalIndex);
        verify(parcelRepository).findById(parcelId);
        verify(transferRepository).findByParcelIsAndArrivalDateIsNull(parcel);
        verify(parcelRepository).save(parcel);
        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(captor.capture());
        assertEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelWhenReceiverPostalOfficeNotExistsThrowsException() {
        String receiverIndex = "000002";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.empty());

        assertThrows(PostalOfficeNotFoundException.class, () -> mainService.receiveParcel(parcelId, receiveParcelDto));

        verify(postalOfficeRepository).findById(receiverIndex);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelWhenParcelNotExistsThrowsException() {
        String receiverIndex = "000002";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.empty());

        assertThrows(ParcelNotFoundException.class, () -> mainService.receiveParcel(parcelId, receiveParcelDto));

        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelWhenParcelStatusNotWaitingForReceivingThrowsException() {
        String receiverIndex = "000002";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.IN_TRANSIT);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        assertThrows(ParcelNotFoundAtPostalOfficeException.class, () -> mainService.receiveParcel(parcelId, receiveParcelDto));

        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelWhenParcelArrivalNotEqualsReceiverPostalOfficeThrowsException() {
        String receiverIndex = "000002";
        String parcelReceiverIndex = "000003";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);
        PostalOffice parcelReceiverPostalOffice = new PostalOffice(parcelReceiverIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.WAITING_FOR_RECEIVING);
        parcel.setReceiverPostalOffice(parcelReceiverPostalOffice);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        assertThrows(ParcelNotFoundAtPostalOfficeException.class, () -> mainService.receiveParcel(parcelId, receiveParcelDto));

        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelWhenParcelCurrentPostalOfficeNotEqualsReceiverPostalOfficeThrowsException() {
        String receiverIndex = "000002";
        String descurentIndex = "000003";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);
        PostalOffice currentPostalOffice = new PostalOffice(descurentIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.WAITING_FOR_RECEIVING);
        parcel.setReceiverPostalOffice(receiverPostalOffice);
        parcel.setCurrentPostalOffice(currentPostalOffice);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        assertThrows(ParcelNotFoundAtPostalOfficeException.class, () -> mainService.receiveParcel(parcelId, receiveParcelDto));

        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void receiveParcelOk() {
        String receiverIndex = "000002";
        Long parcelId = 10L;
        ReceiveParcelDto receiveParcelDto = new ReceiveParcelDto();
        receiveParcelDto.setReceiverIndex(receiverIndex);

        PostalOffice receiverPostalOffice = new PostalOffice(receiverIndex);

        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.WAITING_FOR_RECEIVING);
        parcel.setReceiverPostalOffice(receiverPostalOffice);
        parcel.setCurrentPostalOffice(receiverPostalOffice);

        when(postalOfficeRepository.findById(receiverIndex)).thenReturn(Optional.of(receiverPostalOffice));
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
        when(parcelRepository.save(parcel)).thenReturn(parcel);

        Parcel actual = mainService.receiveParcel(parcelId, receiveParcelDto);

        verify(postalOfficeRepository).findById(receiverIndex);
        verify(parcelRepository).findById(parcelId);
        verify(parcelRepository).save(parcel);
        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        assertEquals(parcel, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void getParcelsWhenNoOneParcelsExistsOk() {

        List<Parcel> expected = new ArrayList<>();
        when(parcelRepository.findAll()).thenReturn(expected);

        List<Parcel> actual = mainService.getParcels();

        verify(parcelRepository).findAll();
        assertIterableEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void getParcelsWhenTwoParcelsExistsOk() {
        Parcel parcel1 = new Parcel();
        Parcel parcel2 = new Parcel();

        List<Parcel> expected = List.of(parcel1, parcel2);
        when(parcelRepository.findAll()).thenReturn(expected);

        List<Parcel> actual = mainService.getParcels();

        verify(parcelRepository).findAll();
        assertIterableEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void getParcelHistoryWhenParcelNotExistsThrowsException() {
        Long parcelId = 10L;

        when(parcelRepository.findById(parcelId)).thenReturn(Optional.empty());

        assertThrows(ParcelNotFoundException.class, () -> mainService.getParcelHistory(parcelId));

        verify(parcelRepository).findById(parcelId);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void getParcelHistoryWhenParcelNotHaveTransfersOk() {
        Long parcelId = 10L;

        Parcel parcel = new Parcel();
        List<Transfer> transfers = new ArrayList<>();
        ParcelHistoryDto expected = new ParcelHistoryDto(parcel, transfers);
        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        ParcelHistoryDto actual = mainService.getParcelHistory(parcelId);

        verify(parcelRepository).findById(parcelId);
        assertEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void getParcelHistoryWhenParcelHaveTwoTransfersOk() {
        Long parcelId = 10L;

        Parcel parcel = new Parcel();
        parcel.setId(2L);
        Transfer transfer1 = new Transfer();
        transfer1.setArrivalDate(OffsetDateTime.now());
        transfer1.setParcel(parcel);
        transfer1.setId(1L);

        Transfer transfer2 = new Transfer();
        transfer2.setParcel(parcel);
        transfer2.setArrivalDate(OffsetDateTime.now().minusDays(1));
        transfer2.setId(2L);

        Transfer transfer3 = new Transfer();
        transfer3.setParcel(parcel);
        transfer3.setId(3L);

        List<Transfer> transfers = List.of(transfer2, transfer1, transfer3);
        parcel.setTransfers(new HashSet<>(transfers));
        ParcelHistoryDto expected = new ParcelHistoryDto(parcel, transfers);

        when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

        ParcelHistoryDto actual = mainService.getParcelHistory(parcelId);

        verify(parcelRepository).findById(parcelId);
        assertEquals(expected, actual);
        verifyAllMocksNoMoreInteractions();
    }

    @Test
    public void registerPostalOfficesOk() {
        PostalOffice postalOffice1 = new PostalOffice("000001");
        PostalOffice postalOffice2 = new PostalOffice("000002");
        List<PostalOffice> postalOffices = List.of(postalOffice1, postalOffice2);
        when(postalOfficeRepository.saveAll(any(List.class))).thenReturn(postalOffices);

        List<PostalOffice> actual = mainService.registerPostalOffices(postalOffices);

        verify(postalOfficeRepository).saveAll(any(List.class));
        assertIterableEquals(postalOffices, actual);
        verifyAllMocksNoMoreInteractions();
    }

    private void verifyAllMocksNoMoreInteractions() {
        verifyNoMoreInteractions(parcelRepository,
                postalOfficeRepository,
                transferRepository);
    }

}
