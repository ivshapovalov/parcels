package com.post.parcels.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.parcels.CommonTest;
import com.post.parcels.model.entity.PostalOffice;
import com.post.parcels.service.MainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostalOfficeController.class)
@AutoConfigureMockMvc
public class PostalOfficeControllerTest extends CommonTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MainService mainService;

    @Test
    public void registerPostalOfficesWhenTwoPostalOfficeInRequestReturnOk() throws Exception {
        String jsonRequest = """
                    [
                        {
                            "address": "Moscow-000001",
                            "index": "000001",
                            "name": "Moscow-000001"
                        },
                        {
                            "address": "Moscow-000002",
                            "index": "000002",
                            "name": "Moscow-000002"
                        }                     
                    ]
                """;
        PostalOffice postalOffice1 = new PostalOffice("000001");
        postalOffice1.setName("Moscow-000001");
        postalOffice1.setAddress("Moscow-000001");
        PostalOffice postalOffice2 = new PostalOffice("000002");
        postalOffice1.setName("Moscow-000002");
        postalOffice1.setAddress("Moscow-000002");

        List<PostalOffice> response = Arrays.asList(postalOffice1, postalOffice2);

        String jsonResponse = objectMapper.writeValueAsString(response);

        when(mainService.registerPostalOffices(any(List.class))).thenReturn(response);
        this.mockMvc.perform(post("/postalOffices")
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(jsonResponse));

        verify(mainService).registerPostalOffices(any(List.class));
        verifyNoMoreInteractions(mainService);
    }
}
