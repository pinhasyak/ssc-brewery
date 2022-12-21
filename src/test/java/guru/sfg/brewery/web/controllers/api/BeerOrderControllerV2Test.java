package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import guru.sfg.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerV2Test extends BaseIT {

    public static final String API_ROOT = "/api/v2/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;

    List<Beer> loadedBeers;

    @BeforeEach
    public void setUpTest() {
        stPeteCustomer = customerRepository.findCustomersByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING)
                .stream().findFirst().orElseThrow();
        dunedinCustomer = customerRepository.findCustomersByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING)
                .stream().findFirst().orElseThrow();
        keyWestCustomer = customerRepository.findCustomersByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTING)
                .stream().findFirst().orElseThrow();
        loadedBeers = beerRepository.findAll();
    }



    @Test
    void getOrdersNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + "/orders/"+beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void getOrdersAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + "/orders/"+beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void getOrdersAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + "/orders/"+beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void getOrdersNOTAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + "/orders/"+beerOrder.getId()))
                .andExpect(status().isNotFound());
    }
}