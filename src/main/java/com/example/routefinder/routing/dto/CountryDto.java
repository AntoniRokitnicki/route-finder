package com.example.routefinder.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CountryDto {
    private final CountryName name;
    private final List<CountryDto> borders = new ArrayList<>();
}
