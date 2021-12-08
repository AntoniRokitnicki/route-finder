package com.example.routefinder.routing;

import com.example.routefinder.routing.dto.Country;
import com.example.routefinder.routing.dto.CountryDto;
import com.example.routefinder.routing.dto.CountryName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Repository
@AllArgsConstructor
public class RoutingRepository {

    private static final String COUNTRIES_JSON = "countries.json";

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Map<CountryName, CountryDto> loadCountries() {
        var url = getClass().getClassLoader().getResource(COUNTRIES_JSON);
        Assert.notNull(url, () -> format("File %s not found", COUNTRIES_JSON));
        var countryList = objectMapper.<List<Country>>readValue(new File(url.toURI()), objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class));

        var countryNameToCountryDtoMap = countryList.stream()
                .map(Country::getCca3)
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                CountryDto::new,
                                this::onDuplicateCountry,
                                IdentityHashMap::new)
                );

        addBorders(countryList, countryNameToCountryDtoMap);
        return countryNameToCountryDtoMap;
    }

    private CountryDto onDuplicateCountry(CountryDto country, CountryDto country2) {
        throw new IllegalStateException(format("Duplicated country %s in json", country.getName()));
    }

    private void addBorders(List<Country> countryList, Map<CountryName, CountryDto> countryNameToCountryDtoMap) {
        for (Country country : countryList) {
            CountryDto countryDto = countryNameToCountryDtoMap.get(country.getCca3());
            for (CountryName border : country.getBorders()) {
                CountryDto borderDto = countryNameToCountryDtoMap.get(border);
                Assert.notNull(borderDto, () -> format("Border country %s not found for country %s", border, country.getCca3()));
                countryDto.getBorders().add(borderDto);
            }
        }
    }

}
