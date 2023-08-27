package com.post.parcels.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.parcels.CommonTest;
import com.post.parcels.exceptions.*;
import com.post.parcels.model.dto.*;
import com.post.parcels.model.entity.Parcel;
import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.model.entity.Transfer;
import com.post.parcels.service.MainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParcelController.class)
@AutoConfigureMockMvc
public class ParcelControllerTest extends CommonTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MainService mainService;

    @Test
    public void registerParcelsWhenOneParcelInRequestReturnOk() throws Exception {
        String jsonRequest = """
                    [
                        {
                            "parcel_type":"Посылка",
                            "acceptance_index":"000001",
                            "receiver_address":"Moscow",
                            "receiver_name":"Alex",
                            "receiver_index":"000010"
                        }
                    ]
                """;
        RegisterParcelDto registerParcelDto = new RegisterParcelDto();
        registerParcelDto.setType(Parcel.Type.PACKAGE);
        registerParcelDto.setAcceptanceIndex("000001");
        registerParcelDto.setReceiverIndex("000010");
        registerParcelDto.setReceiverName("Alex");
        registerParcelDto.setReceiverAddress("Moscow");

        PostalOffice acceptancePostalOffice = new PostalOffice("000001");
        PostalOffice receiverPostalOffice = new PostalOffice("000010");
        List<Parcel> response = Collections.singletonList(registerParcelDto.toParcel(acceptancePostalOffice, receiverPostalOffice));

        String jsonResponse = objectMapper.writeValueAsString(response);

        when(mainService.registerParcels(any(List.class))).thenReturn(response);
        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(jsonResponse));

        verify(mainService).registerParcels(any(List.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void registerParcelsWhenTwoParcelsInRequestReturnOk() throws Exception {
        String jsonRequest = """
                    [
                        {
                            "parcel_type":"Посылка",
                            "acceptance_index":"000001",
                            "receiver_address":"Moscow",
                            "receiver_name":"Alex",
                            "receiver_index":"000010"
                               
                        },
                        {
                            "parcel_type":"Бандероль",
                            "acceptance_index":"000002",
                            "receiver_address":"Kursk",
                            "receiver_name":"Jack",
                            "receiver_index":"000020"
                        }                        
                    ]
                """;
        RegisterParcelDto registerParcelDto1 = new RegisterParcelDto();
        registerParcelDto1.setType(Parcel.Type.PACKAGE);
        registerParcelDto1.setAcceptanceIndex("000001");
        registerParcelDto1.setReceiverIndex("000010");
        registerParcelDto1.setReceiverName("Alex");
        registerParcelDto1.setReceiverAddress("Moscow");

        RegisterParcelDto registerParcelDto2 = new RegisterParcelDto();
        registerParcelDto2.setType(Parcel.Type.BANDEROLE);
        registerParcelDto2.setAcceptanceIndex("000002");
        registerParcelDto2.setReceiverIndex("000020");
        registerParcelDto2.setReceiverName("Jack");
        registerParcelDto2.setReceiverAddress("Kursk");

        PostalOffice postalOffice1 = new PostalOffice("000001");
        PostalOffice postalOffice2 = new PostalOffice("000002");
        PostalOffice postalOffice10 = new PostalOffice("000010");
        PostalOffice postalOffice20 = new PostalOffice("000020");
        List<Parcel> response = Arrays.asList(
                registerParcelDto1.toParcel(postalOffice1, postalOffice10),
                registerParcelDto2.toParcel(postalOffice2, postalOffice20)
        );

        String jsonResponse = objectMapper.writeValueAsString(response);

        when(mainService.registerParcels(any(List.class))).thenReturn(response);
        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(jsonResponse));

        verify(mainService).registerParcels(any(List.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void registerParcelsWhenSenderIndexNotMatchPatternBadRequest() throws Exception {
        String jsonRequest = """
                 [
                        {
                            "parcel_type":"Посылка",
                            "acceptance_index":"00000001",
                            "receiver_address":"Moscow",
                            "receiver_name":"Alex",
                            "receiver_index":"0010"
                        }                  
                 ]
                """;
        String jsonResponse = """                
                {
                    "acceptanceIndex": "must match \\"^[0-9]{6}$\\"",
                    "receiverIndex": "must match \\"^[0-9]{6}$\\""
                }                
                """;

        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoInteractions(mainService);
    }

    @Test
    public void registerParcelsWhenFieldsAreEmptyBadRequest() throws Exception {
        String jsonRequest = """
                 [
                    {}                    
                 ]
                """;
        String jsonResponse = """
                    {
                        "receiverAddress": "must not be blank",
                        "acceptanceIndex": "must not be blank",
                        "receiverName": "must not be blank",
                        "type": "must not be null",
                        "receiverIndex": "must not be blank"
                    }
                """;

        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoInteractions(mainService);
    }

    @Test
    public void registerParcelsWhenIndexFieldsNotMatchesPatternBadRequest() throws Exception {
        String jsonRequest = """
                [
                    {
                        "parcel_type":"Посылка",
                        "acceptance_index":"00000001",
                        "receiver_address":"Moscow",
                        "receiver_name":"Alex",
                        "receiver_index":"0010"
                    } 
                ]
                """;
        String jsonResponse = """
                    {
                        "acceptanceIndex": "must match \\"^[0-9]{6}$\\"",
                        "receiverIndex": "must match \\"^[0-9]{6}$\\""
                    }
                """;

        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoInteractions(mainService);
    }

    @Test
    public void registerParcelsWhenParcelTypeInvalidBadRequest() throws Exception {
        String jsonRequest = """
                 [
                        {
                            "parcel_type":"Конверт",
                            "acceptance_index":"000001",
                            "receiver_address":"Moscow",
                            "receiver_name":"Alex",
                            "receiver_index":"000010"
                        }                  
                 ]
                """;
        String plainResponse = "Enum value 'Конверт' should be: 'Посылка, Бандероль, Открытка, Письмо'";


        this.mockMvc.perform(post("/parcels")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(plainResponse));

        verifyNoInteractions(mainService);
    }

    @Test
    public void departParcelWhenIdIsInvalidBadRequest() throws Exception {
        String parcelId = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String jsonRequest = """
                    {   
                        "departure_index":"000001",
                        "arrival_index":"000002"
                    }
                """;

        String jsonResponse = """
                    {
                        "parcel_id": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\\""
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%s/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenFieldsAreNullBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                    }
                """;

        String jsonResponse = """
                    {
                        "departureIndex": "must not be blank",
                        "arrivalIndex": "must not be blank"
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenIndexFieldsNotMatchedPatternBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                        "departure_index":"0000001",
                        "arrival_index":"0002"
                    }
                """;

        String jsonResponse = """
                    {
                        "departureIndex": "must match \\"^[0-9]{6}$\\"",
                        "arrivalIndex": "must match \\"^[0-9]{6}$\\""
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenOneParcelInRequestReturnOk() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                        "departure_index":"000001",
                        "arrival_index":"000002"
                    }
                """;
        Transfer transfer = new Transfer();

        String jsonResponse = objectMapper.writeValueAsString(transfer);

        when(mainService.departParcel(any(Long.class), any(DepartureParcelDto.class))).thenReturn(transfer);
        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(mainService).departParcel(anyLong(), any(DepartureParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenParcelNotExistsBadRequest() throws Exception {
        Long parcelId = 1000000L;
        String jsonRequest = """
                    {   
                        "departure_index":"000001",
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find parcel with id '1000000'";

        when(mainService.departParcel(any(Long.class), any(DepartureParcelDto.class))).thenThrow(
                new ParcelNotFoundException(parcelId)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).departParcel(anyLong(), any(DepartureParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenPostalOfficeNotExistsBadRequest() throws Exception {
        String postalIndex = "000001";
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                        "departure_index":"000001",
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find postal office with index '000001'";

        when(mainService.departParcel(any(Long.class), any(DepartureParcelDto.class))).thenThrow(
                new PostalOfficeNotFoundException(postalIndex)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).departParcel(anyLong(), any(DepartureParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void departParcelWhenParcelAtPostalOfficeBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                        "departure_index":"000001",
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find parcel with id '1' at postal office '000001' with status 'В почтовом офисе'";

        when(mainService.departParcel(any(Long.class), any(DepartureParcelDto.class))).thenThrow(
                new ParcelNotFoundAtPostalOfficeException(parcelId, "000001", Parcel.Status.AT_POSTAL_OFFICE)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/depart", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).departParcel(anyLong(), any(DepartureParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenFieldsAreNullBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {   
                    }
                """;

        String jsonResponse = """
                    {
                        "arrivalIndex": "must not be blank"
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }


    @Test
    public void arriveParcelWhenIdIsInvalidBadRequest() throws Exception {
        String parcelId = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String jsonResponse = """
                    {
                        "parcel_id": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\\""
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%s/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenIndexFieldsNotMatchedPatternBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"0002"
                    }
                """;

        String jsonResponse = """
                    {
                        "arrivalIndex": "must match \\"^[0-9]{6}$\\""
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenOneParcelInRequestReturnOk() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;
        ArrivalParcelDto arrivalParcelDto = new ArrivalParcelDto();
        arrivalParcelDto.setArrivalIndex("000002");
        Transfer transfer = new Transfer();

        String jsonResponse = objectMapper.writeValueAsString(transfer);

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenReturn(transfer);
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenParcelNotExistsBadRequest() throws Exception {
        Long parcelId = 1000000L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find parcel with id '1000000'";

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new ParcelNotFoundException(parcelId)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenPostalOfficeNotExistsBadRequest() throws Exception {
        String postalIndex = "000001";
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find postal office with index '000001'";

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new PostalOfficeNotFoundException(postalIndex)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenParcelNotInTransitBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = String.format("Could not find parcel with id '%s' and status '%s'", parcelId, Parcel.Status.IN_TRANSIT);

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new ParcelWithStatusNotFoundException(parcelId, Parcel.Status.IN_TRANSIT)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenActiveParcelNotFoundBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Could not find active transfer for parcel id '1'";

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new ActiveTransferNotFoundException(parcelId)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenMultipleParcelsFoundBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        String textResponse = "Found many active transfers for parcel id '1'";

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new ManyActiveTransfersFoundException(parcelId)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void arriveParcelWhenDepartureTimeGreaterThanArrivalTimeBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "arrival_index":"000002"
                    }
                """;

        OffsetDateTime departureTime = OffsetDateTime.parse("2023-08-19T13:23:00.000+03:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toInstant()
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime arrivalTime = OffsetDateTime.parse("2022-08-19T13:23:00.000+03:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toInstant()
                .atOffset(ZoneOffset.UTC);

        String textResponse = String.format("Parcel '%s' transfer time '%s' greater than '%s'",
                parcelId, departureTime, arrivalTime);

        when(mainService.arriveParcel(any(Long.class), any(ArrivalParcelDto.class))).thenThrow(
                new ParcelTransferTimeInvalidException(parcelId, departureTime, arrivalTime)
        );
        this.mockMvc.perform(post(String.format("/parcels/%d/arrive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).arriveParcel(anyLong(), any(ArrivalParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void receiveParcelsWhenIdIsInvalidBadRequest() throws Exception {
        String parcelId = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String jsonRequest = """
                    {
                        "receiver_index":"000002"
                    }
                """;
        String jsonResponse = """
                    {
                        "parcel_id": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\\""
                    }
                """;

        this.mockMvc.perform(post(String.format("/parcels/%s/receive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoInteractions(mainService);
    }

    @Test
    public void receiveParcelsWhenParcelWithIdNotExistsBadRequest() throws Exception {
        Long parcelId = 1L;
        String jsonRequest = """
                    {
                        "receiver_index":"000002"
                    }
                """;
        String textResponse = "Could not find parcel with id '1'";

        when(mainService.receiveParcel(any(Long.class), any(ReceiveParcelDto.class))).thenThrow(
                new ParcelNotFoundException(parcelId)
        );

        this.mockMvc.perform(post(String.format("/parcels/%s/receive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).receiveParcel(anyLong(), any(ReceiveParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void receiveParcelsWhenPostalOfficeNotExistsBadRequest() throws Exception {
        Long parcelId = 1L;
        String postalIndex = "000001";

        String jsonRequest = """
                    {
                        "receiver_index":"000002"
                    }
                """;

        String textResponse = "Could not find postal office with index '000001'";

        when(mainService.receiveParcel(any(Long.class), any(ReceiveParcelDto.class))).thenThrow(
                new PostalOfficeNotFoundException(postalIndex)
        );

        this.mockMvc.perform(post(String.format("/parcels/%s/receive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).receiveParcel(anyLong(), any(ReceiveParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void receiveParcelsWhenParcelNotInFinalDestinationBadRequest() throws Exception {
        Long parcelId = 1L;

        String jsonRequest = """
                    {
                        "receiver_index":"000002"
                    }
                """;

        String textResponse =
                String.format("Could not find parcel with id '%s' at postal office '%s' with status '%s'", parcelId, "000002", Parcel.Status.WAITING_FOR_RECEIVING);

        when(mainService.receiveParcel(any(Long.class), any(ReceiveParcelDto.class))).thenThrow(
                new ParcelNotFoundAtPostalOfficeException(parcelId, "000002", Parcel.Status.WAITING_FOR_RECEIVING)
        );

        this.mockMvc.perform(post(String.format("/parcels/%s/receive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(textResponse));

        verify(mainService).receiveParcel(anyLong(), any(ReceiveParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void receiveParcelsReturnOk() throws Exception {
        Long parcelId = 1L;

        String jsonRequest = """
                    {
                        "receiver_index":"000002"
                    }
                """;
        Parcel parcel = new Parcel();
        String jsonResponse = objectMapper.writeValueAsString(parcel);

        when(mainService.receiveParcel(any(Long.class), any(ReceiveParcelDto.class))).thenReturn(parcel);
        this.mockMvc.perform(post(String.format("/parcels/%s/receive", parcelId))
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(mainService).receiveParcel(anyLong(), any(ReceiveParcelDto.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void getParcelsWhenNoParcelsExistsReturnOk() throws Exception {
        List<Parcel> parcels = new ArrayList<>();
        String jsonResponse = objectMapper.writeValueAsString(parcels);

        when(mainService.getParcels()).thenReturn(parcels);
        this.mockMvc.perform(get("/parcels"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(mainService).getParcels();
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void getParcelsWhenTwoParcelsExistsReturnOk() throws Exception {
        Parcel parcel1 = new Parcel();
        parcel1.setStatus(Parcel.Status.RECEIVED);
        parcel1.setId(1);
        Parcel parcel2 = new Parcel();
        parcel2.setStatus(Parcel.Status.IN_TRANSIT);
        parcel2.setId(2);
        List<Parcel> parcels = List.of(parcel1, parcel2);
        String jsonResponse = objectMapper.writeValueAsString(parcels);

        when(mainService.getParcels()).thenReturn(parcels);
        this.mockMvc.perform(get("/parcels"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].parcel_id", is(1)))
                .andExpect(jsonPath("$[1].parcel_id", is(2)));

        verify(mainService).getParcels();
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void getParcelHistoryReturnOk() throws Exception {
        Long parcelId = 1L;
        Parcel parcel = new Parcel();
        parcel.setStatus(Parcel.Status.RECEIVED);
        parcel.setId(1);
        PostalOffice postalOffice1 = new PostalOffice("000001");
        PostalOffice postalOffice2 = new PostalOffice("000002");
        PostalOffice postalOffice3 = new PostalOffice("000003");
        Transfer transfer1 = new Transfer();
        transfer1.setParcel(parcel);
        transfer1.setId(1L);
        transfer1.setDeparturePostalOffice(postalOffice1);
        transfer1.setArrivalPostalOffice(postalOffice2);

        Transfer transfer2 = new Transfer();
        transfer2.setParcel(parcel);
        transfer2.setId(2L);
        transfer2.setDeparturePostalOffice(postalOffice2);
        transfer2.setArrivalPostalOffice(postalOffice3);

        List<Transfer> transfers = List.of(transfer1, transfer2);

        ParcelHistoryDto parcelHistoryDto = new ParcelHistoryDto(parcel, transfers);

        String jsonResponse = objectMapper.writeValueAsString(parcelHistoryDto);

        when(mainService.getParcelHistory(any(Long.class))).thenReturn(parcelHistoryDto);
        this.mockMvc.perform(get(String.format("/parcels/%s/history", parcelId)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(mainService).getParcelHistory(any(Long.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void getParcelsHistoryWhenTransfersNotExistsReturnOk() throws Exception {
        Long parcelId = 1L;
        List<Transfer> transfers = new ArrayList<>();
        Parcel parcel = new Parcel();
        ParcelHistoryDto parcelHistoryDto = new ParcelHistoryDto(parcel, transfers);

        String jsonResponse = objectMapper.writeValueAsString(parcelHistoryDto);

        when(mainService.getParcelHistory(any(Long.class))).thenReturn(parcelHistoryDto);
        this.mockMvc.perform(get(String.format("/parcels/%s/history", parcelId)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(mainService).getParcelHistory(any(Long.class));
        verifyNoMoreInteractions(mainService);
    }

    @Test
    public void getParcelsTransfersWhenParcelIdIsInvalidBadRequest() throws Exception {
        String parcelId = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String jsonResponse = """
                    {
                        "parcel_id": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\\""
                    }
                """;

        this.mockMvc.perform(get(String.format("/parcels/%s/history", parcelId)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));

        verifyNoInteractions(mainService);
    }

}
