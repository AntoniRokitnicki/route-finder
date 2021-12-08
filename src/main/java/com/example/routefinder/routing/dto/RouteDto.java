package com.example.routefinder.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteDto {
    private List<CountryName> route;
}
