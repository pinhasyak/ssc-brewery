package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/customers/")
@RestController
public class BeerOrderControllerV2 {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @PreAuthorize("hasAnyAuthority('order.read') OR " +
            "hasAnyAuthority('customer.order.read') " +
            "AND @beerOrderAuthenticationManager.customerIdMatches(authentication, #user.getCustomer().getId)")
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if(user.getCustomer() != null){
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        }else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @PreAuthorize("hasAnyAuthority('order.read') OR " +
            "hasAnyAuthority('customer.order.read')")
    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId")UUID orderId){
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if (beerOrderDto == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not found");
        }
        log.debug("Found order: {}", beerOrderDto);
        return beerOrderDto;
    }

}
