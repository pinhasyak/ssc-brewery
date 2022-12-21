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
class BeerOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

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
    void createOrderNotAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildbBeerOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildbBeerOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildbBeerOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildbBeerOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }
    @Transactional
    @Test
    void pickupOrderNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isUnauthorized());
    }
    @Transactional
    @WithUserDetails("spring")
    @Test
    void pickupOrderUserAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void pickupOrderUserAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void pickupOrderUserNOTAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersAdmin() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void listOrdersAuthCustomer() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().is2xxSuccessful());
    }

    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrdersNOTAuthCustomer() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    private BeerOrderDto buildbBeerOrderDto(Customer stPeteCustomer, UUID beerId) {
        List<BeerOrderLineDto> orderLineDtos = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());
        return BeerOrderDto.builder()
                .orderStatus(OrderStatusEnum.NEW)
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLineDtos)
                .customerId(stPeteCustomer.getId())
                .build();
    }
}