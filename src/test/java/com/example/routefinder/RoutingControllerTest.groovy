package com.example.routefinder

import com.example.routefinder.exception.RoutingNotFoundException
import com.example.routefinder.routing.RoutingController
import com.example.routefinder.routing.RoutingService
import com.example.routefinder.routing.dto.CountryName
import com.example.routefinder.routing.dto.RouteDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RoutingController.class)
class RoutingControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @MockBean
    private RoutingService routingService

    def "when route found return status code 200 with json"() {
        when:
        def route = new RouteDto([CountryName.CZE, CountryName.AUT, CountryName.ITA])
        when(routingService.routing(CountryName.CZE, CountryName.ITA)).thenReturn(route)

        then:
        mockMvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"route\":[\"CZE\",\"AUT\",\"ITA\"]}"))
    }

    def "when no found return 400 error code"() {
        when:
        when(routingService.routing(CountryName.CZE, CountryName.ITA)).thenThrow(RoutingNotFoundException.class)

        then:
        mockMvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isNotFound())
    }

}
