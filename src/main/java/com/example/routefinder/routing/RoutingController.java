package com.example.routefinder.routing;

import com.example.routefinder.routing.dto.CountryName;
import com.example.routefinder.routing.dto.RouteDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class RoutingController {
    private final RoutingService routingService;

    @GetMapping(value = "/routing/{origin}/{destination}")
    public RouteDto routing(@PathVariable("origin") CountryName origin,
                            @PathVariable("destination") CountryName destination) {
        return routingService.routing(origin, destination);
    }
}
