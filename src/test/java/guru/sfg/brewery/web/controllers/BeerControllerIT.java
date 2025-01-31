package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Test
    void initCreationFormWithSpring() throws Exception{
        mockMvc.perform(get("/beers/new").with(httpBasic("spring","guru")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));

    }

    @Test
    void initCreationFormWithUser() throws Exception{
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("spring","guru")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void initCreationFormWithScott() throws Exception{
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("scott","tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isUnauthorized());
    }


    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
    void findBeersHttpBasic(String user, String password) throws Exception {
        mockMvc.perform(get("/beers/find").with(anonymous())
                .with(httpBasic(user, password)))
                .andExpect(status().is2xxSuccessful());
    }
}
