package com.example.routefinder

import com.example.routefinder.exception.RoutingNotFoundException
import com.example.routefinder.routing.RoutingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.example.routefinder.routing.dto.CountryName.*

@SpringBootTest
class RoutingServiceTest extends Specification {

    @Autowired
    @Subject
    private RoutingService routingService

    @Unroll
    def "routing for #origin, #destination == #journey"() {
        expect:
        routingService.routing(origin, destination).route == journey

        where:
        origin | destination || journey
        CZE    | AUT         || [CZE, AUT]
        CZE    | AUT         || [CZE, AUT]
        CZE    | ITA         || [CZE, AUT, ITA]
        ITA    | CZE         || [ITA, AUT, CZE]
        USA    | ARG         || [USA, MEX, BLZ, GTM, SLV, HND, NIC, CRI,
                                 PAN, COL, BRA, ARG]
        ESP    | VNM         || [ESP, AND, FRA, BEL, DEU, AUT, CZE, POL,
                                 BLR, LVA, EST, RUS, AZE, ARM, GEO, TUR,
                                 IRN, AFG, PAK, CHN, VNM]
        MYS    | ZAF         || [MYS, THA, MMR, BGD, IND, BTN, CHN, AFG,
                                 IRN, ARM, AZE, GEO, RUS, BLR, LVA, LTU,
                                 POL, CZE, AUT, DEU, BEL, FRA, AND, ESP,
                                 MAR, DZA, TUN, LBY, TCD, CMR, CAF, COD,
                                 AGO, ZMB, BWA, ZAF]
    }

    @Unroll
    def "not route found"() {
        when:
        routingService.routing(ALA, ALB).route
        then:
        thrown RoutingNotFoundException
    }

}
