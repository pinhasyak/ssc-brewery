/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.*;
import guru.sfg.brewery.domain.security.*;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Created by jt on 2019-01-26.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";

    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTING = "Key West Distributing";

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";
    public static final String STPETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (authorityRepository.count() == 0) {
            loadSecurityData();

            loadBreweryData();
            loadTastingRoomData();
            loadCustomers();
        }

    }

    private void loadCustomers() {
        Role customerRole = roleRepository.findByName(Roles.ROLE_CUSTOMER).orElseThrow();

        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());
        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());
        Customer keyWestCustomer = customerRepository.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        User stPeteUser = userRepository.save(User.builder()
                .username(STPETE_USER)
                .password(passwordEncoder.encode("password"))
                .role(customerRole)
                .customer(stPeteCustomer)
                .build());
        User dunedinUser = userRepository.save(User.builder()
                .username(DUNEDIN_USER)
                .password(passwordEncoder.encode("password"))
                .role(customerRole)
                .customer(dunedinCustomer)
                .build());
        User keyWestUser = userRepository.save(User.builder()
                .username("keywest")
                .password(passwordEncoder.encode("password"))
                .role(customerRole)
                .customer(keyWestCustomer)
                .build());

        //create order
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);

    }

    private BeerOrder createOrder(Customer customer) {
        return beerOrderRepository.save(BeerOrder.builder()
                .customer(customer)
                        .orderStatusCallbackUrl("http://example.com")
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(new HashSet<>(Set.of(BeerOrderLine.builder()
                        .beer(beerRepository.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build())))
                .build());
    }

    private void loadSecurityData() {
        //      Beer auths
        Authority createBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_CREATE).build());
        Authority updateBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_UPDATE).build());
        Authority deleteBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_DELETE).build());
        Authority readBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_READ).build());

//      Brewery auths
        Authority createBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_CREATE).build());
        Authority updateBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_UPDATE).build());
        Authority deleteBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_DELETE).build());
        Authority readBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_READ).build());

//      Beer auths
        Authority createCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_CREATE).build());
        Authority updateCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_UPDATE).build());
        Authority deleteCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_DELETE).build());
        Authority readCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_READ).build());//      Beer auths

//      Beer order
        Authority createOrderAuthority = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_CREATE).build());
        Authority updateOrderAuthority = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_UPDATE).build());
        Authority deleteOrderAuthority = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_DELETE).build());
        Authority readOrderAuthority = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_READ).build());

        Authority createOrderAuthorityCustomer = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_CREATE_CUSTOMER).build());
        Authority updateOrderAuthorityCustomer = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_UPDATE_CUSTOMER).build());
        Authority deleteOrderAuthorityCustomer = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_DELETE_CUSTOMER).build());
        Authority readOrderAuthorityCustomer = authorityRepository.save(Authority.builder().permission(Authorities.ORDER_READ_CUSTOMER).build());


        Role adminRole = roleRepository.save(Role.builder().name(Roles.ROLE_ADMIN).build());
        Role customerRole = roleRepository.save(Role.builder().name(Roles.ROLE_CUSTOMER).build());
        Role userRole = roleRepository.save(Role.builder().name(Roles.ROLE_USER).build());

        adminRole.setAuthorities(new HashSet<>(Set.of(
                createBeerAuthority, updateBeerAuthority, deleteBeerAuthority, readBeerAuthority,
                createBreweryAuthority, updateBreweryAuthority, deleteBreweryAuthority, readBreweryAuthority,
                createCustomerAuthority, updateCustomerAuthority, deleteCustomerAuthority, readCustomerAuthority,
                createOrderAuthority, updateOrderAuthority, deleteOrderAuthority, readOrderAuthority

        )));

        customerRole.setAuthorities(new HashSet<>(Set.of(
                readBeerAuthority,
                readCustomerAuthority,
                readBreweryAuthority,
                createOrderAuthorityCustomer, updateOrderAuthorityCustomer, deleteOrderAuthorityCustomer, readOrderAuthorityCustomer
        )));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeerAuthority)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());

        userRepository.findAll().forEach(user -> {
            log.info("user data: {}", user);
            user.getAuthorities().forEach(authority -> log.info("user authority: {}", authority.getAuthority()));
        });
    }

    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
}
