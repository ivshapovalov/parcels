package com.post.parcels;

import com.post.parcels.controller.ParcelController;
import com.post.parcels.controller.PostalOfficeController;
import com.post.parcels.service.MainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
class ParcelsApplicationTests extends CommonTest {

    @Autowired
    private ParcelController parcelController;

    @Autowired
    private PostalOfficeController postalOfficeController;

    @MockBean
    private MainService mainService;

    @Test
    public void contextLoads() {
        assertNotNull(postalOfficeController);
        assertNotNull(parcelController);
    }
}
