package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT{

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/4bfe7354-931f-44e4-82c4-245976769867")
                        .header("Api-Key", "spring").header("Api-Secret", "guru"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/4bfe7354-931f-44e4-82c4-245976769867")
                        .header("Api-Key", "spring").header("Api-Secret", "guruXXX"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/4bfe7354-931f-44e4-82c4-245976769867")
                        .with(httpBasic("spring","guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/4bfe7354-931f-44e4-82c4-245976769867"))
                .andExpect(status().isUnauthorized());
    }



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
