package com.example.routefinder.routing;

import com.example.routefinder.routing.dto.CountryDto;
import com.example.routefinder.routing.dto.CountryName;
import com.example.routefinder.routing.dto.RouteDto;
import com.example.routefinder.exception.RoutingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutingService {
    private final RoutingRepository routingRepository;

    private Map<CountryName, CountryDto> countryNameToCountryDtoMap;

    @PostConstruct
    public void init() {
        countryNameToCountryDtoMap = routingRepository.loadCountries();
    }

    @Cacheable("routing")
    public RouteDto routing(CountryName origin, CountryName destination) {
        var seenSet = Collections.<CountryDto>newSetFromMap(new IdentityHashMap<>());
        var foundList = new ArrayList<CountryDto>();

        var originDto = countryNameToCountryDtoMap.get(origin);
        var destinationDto = countryNameToCountryDtoMap.get(destination);
        seenSet.add(originDto); // to disallow going back to origin country

        find(originDto, destinationDto, seenSet, foundList);
        if (foundList.isEmpty()) {
            throw new RoutingNotFoundException();
        } else {
            foundList.add(originDto);
            Collections.reverse(foundList);
        }
        return new RouteDto(foundList.stream()
                .map(CountryDto::getName)
                .collect(Collectors.toList()));
    }

    private CountryDto find(CountryDto originDto, CountryDto destinationDto,
                            Set<CountryDto> seenSet, List<CountryDto> foundList) {
        var borders = originDto.getBorders();
        for (CountryDto border : borders) {
            if (border == destinationDto) {
                foundList.add(destinationDto);
                return destinationDto;
            }
        }

        for (CountryDto border : borders) {
            if (seenSet.add(border)) {
                var found = find(border, destinationDto, seenSet, foundList);
                if (found != null) {
                    foundList.add(border);
                    return found;
                }
            }
        }
        return null;
    }

}
