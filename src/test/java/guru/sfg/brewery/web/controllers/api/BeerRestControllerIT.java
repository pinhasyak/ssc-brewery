package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;


    @DisplayName("Delete Test")
    @Nested
    class DeleteTest{

        public Beer beerToDelete(){
            Random random = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Beer to deleted")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(2)
                    .quantityToBrew(22)
                    .upc(String.valueOf(random.nextInt(999999999)))
                    .build()
            );

        }


        @Test
        @Disabled
        void deleteBeer() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId())
                            .header("Api-Key", "spring").header("Api-Secret", "guru"))
                    .andExpect(status().isOk());
        }
        @Test
        void deleteBeerBadCreds() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId())
                            .header("Api-Key", "spring").header("Api-Secret", "guruXXX"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteBeerHttpBasicAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId())
                            .with(httpBasic("spring","guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerHttpBasicCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId())
                            .with(httpBasic("scott","tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerHttpBasicUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId())
                            .with(httpBasic("user","password")))
                    .andExpect(status().isForbidden());
        }
        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/"+beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
        void findBeerByIdAndDelete(String user, String password) throws Exception{
            UUID beerId = beerToDelete().getId();

            mockMvc.perform(get("/api/v1/beer/"+beerId)
                    .with(httpBasic(user, password)))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/api/v1/beer/"+beerId)
                            .with(httpBasic("spring","guru")))
                    .andExpect(status().is2xxSuccessful());
        }
    }



    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
    void findBeers(String user, String password) throws Exception{
        mockMvc.perform(get("/api/v1/beer/")
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }


    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
    void findBeerByUpc(String user, String password) throws Exception{
        mockMvc.perform(get("/api/v1/beerupc/0631234200036")
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

}
