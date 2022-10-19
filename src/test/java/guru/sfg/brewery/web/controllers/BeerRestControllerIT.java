package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT{




    @Test
    void findBeers() throws Exception{
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }


    @Test
    void findBeerById() throws Exception{
        mockMvc.perform(get("/api/v1/beer/4bfe7354-931f-44e4-82c4-245976769867")) // httpBasic("pinhas", "Pinhas")
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception{
        mockMvc.perform(get("/api/v1/beerupc/0631234200036"))
                .andExpect(status().isOk());
    }

}
