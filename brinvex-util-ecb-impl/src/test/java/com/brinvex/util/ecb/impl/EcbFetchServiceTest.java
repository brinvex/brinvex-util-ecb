package com.brinvex.util.ecb.impl;

import com.brinvex.util.ecb.api.EcbFetchRequest.DepositFacilityRateRequest;
import com.brinvex.util.ecb.api.EcbFetchRequest.FxRequest;
import com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest;
import com.brinvex.util.ecb.api.EcbFetchResponse.InflationResponse;
import com.brinvex.util.ecb.api.EcbFetchService;
import com.brinvex.util.ecb.api.ObjectFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.SequencedMap;

import static com.brinvex.util.ecb.api.EcbFetchRequest.DepositFacilityRateRequest.EqualSubsequentValuesHandling.MERGE_EQUAL_SUBSEQUENT_VALUES;
import static com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest.MomGrowthCalculation.CALCULATE_MOM_GROWTH;
import static com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest.YoyGrowthCalculation.CALCULATE_WITH_YOY_GROWTH;
import static com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest.YoyGrowthCalculation.DONT_CALCULATE_YOY_GROWTH;
import static java.time.LocalDate.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ExtractMethodRecommender")
class EcbFetchServiceTest {

    @AfterEach
    void slowDownToAvoidHttpGoAway() {
        try {
            Thread.sleep(Duration.ofSeconds(4));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    void fetchFx_oneDay() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate day = parse("2024-08-01");
        SequencedMap<LocalDate, Double> fxRates = ecbFetchService.fetch(
                new FxRequest("CZK", day, day));

        assertEquals(1, fxRates.size());
        assertEquals(day, fxRates.firstEntry().getKey());
        assertEquals(25.454, fxRates.get(day));
    }

    @Test
    void fetchFx_recentDays() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        int nDays = 9;
        SequencedMap<LocalDate, Double> fxRates = ecbFetchService.fetch(
                new FxRequest("CZK", LocalDate.now().minusDays(nDays)));
        int size = fxRates.size();
        assertTrue(size >= 3);
        assertTrue(size <= nDays - 1);
    }

    @Test
    void fetchDepoFacilityInterRates_oneDay() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate day = parse("2024-08-01");
        SequencedMap<LocalDate, Double> rates = ecbFetchService.fetch(
                new DepositFacilityRateRequest(day, day));

        assertEquals(1, rates.size());
        assertEquals(day, rates.firstEntry().getKey());
        assertEquals(3.75, rates.get(day));
    }

    @Test
    void fetchDepoFacilityInterRates_oneYear() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate startDayIncl = parse("2023-01-01");
        LocalDate endDayIncl = parse("2023-12-31");
        SequencedMap<LocalDate, Double> rates = ecbFetchService.fetch(
                new DepositFacilityRateRequest(startDayIncl, endDayIncl));

        assertEquals(365, rates.size());
        assertEquals(startDayIncl, rates.firstEntry().getKey());
        assertEquals(2.0, rates.get(startDayIncl));
        assertEquals(4.0, rates.get(endDayIncl));
    }

    @Test
    void fetchDepoFacilityInterRates_merge() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate startDayIncl = parse("2021-01-01");
        LocalDate endDayIncl = parse("2021-12-31");
        SequencedMap<LocalDate, Double> rates = ecbFetchService.fetch(
                new DepositFacilityRateRequest(startDayIncl, endDayIncl, MERGE_EQUAL_SUBSEQUENT_VALUES));

        assertEquals(1, rates.size());
        assertEquals(startDayIncl, rates.firstEntry().getKey());
        assertEquals(-0.5, rates.get(startDayIncl));
        assertEquals(-0.5, rates.get(startDayIncl));
    }

    @Test
    void fetchDepoFacilityInterRates_now() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        SequencedMap<LocalDate, Double> rates = ecbFetchService.fetch(
                new DepositFacilityRateRequest(yesterday, yesterday));

        assertEquals(1, rates.size());
        assertEquals(yesterday, rates.firstEntry().getKey());
    }

    @Test
    void fetchInflation_oneMonth() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate day = parse("2024-07-01");
        SequencedMap<LocalDate, InflationResponse> results = ecbFetchService.fetch(
                new HICPInflationRequest(day, day));

        assertEquals(1, results.size());
        assertEquals(day, results.firstEntry().getKey());
        assertEquals(126.56, results.get(day).cpi().doubleValue());
    }

    @Test
    void fetchInflation_oneYear() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate middleMonthStartDayIncl = parse("2023-07-15");
        LocalDate middleMonthEndDayIncl = parse("2024-06-15");
        SequencedMap<LocalDate, InflationResponse> results = ecbFetchService.fetch(
                new HICPInflationRequest(middleMonthStartDayIncl, middleMonthEndDayIncl));

        assertEquals(12, results.size());
        assertEquals(parse("2023-07-01"), results.firstEntry().getKey());
        assertEquals(parse("2024-06-01"), results.lastEntry().getKey());

    }

    @Test
    void fetchInflation_calcMom() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate middleMonthStartDayIncl = parse("2023-07-15");
        LocalDate middleMonthEndDayIncl = parse("2024-06-15");
        SequencedMap<LocalDate, InflationResponse> results = ecbFetchService.fetch(
                new HICPInflationRequest(
                        middleMonthStartDayIncl,
                        middleMonthEndDayIncl,
                        CALCULATE_MOM_GROWTH,
                        DONT_CALCULATE_YOY_GROWTH
                ));

        assertEquals(12, results.size());
        assertEquals(parse("2023-07-01"), results.firstEntry().getKey());
        assertEquals(parse("2024-06-01"), results.lastEntry().getKey());
        assertEquals(0, results.get(parse("2024-06-01")).momGrowthFactor().compareTo(new BigDecimal("1.002138")));

    }

    @Test
    void fetchInflation_calcYoy() {
        EcbFetchService ecbFetchService = ObjectFactory.INSTANCE.getEcbFetchService();
        LocalDate middleMonthStartDayIncl = parse("2023-07-15");
        LocalDate middleMonthEndDayIncl = parse("2024-06-15");
        SequencedMap<LocalDate, InflationResponse> results = ecbFetchService.fetch(
                new HICPInflationRequest(
                        middleMonthStartDayIncl,
                        middleMonthEndDayIncl,
                        CALCULATE_MOM_GROWTH,
                        CALCULATE_WITH_YOY_GROWTH
                ));

        assertEquals(12, results.size());
        assertEquals(parse("2023-07-01"), results.firstEntry().getKey());
        assertEquals(parse("2024-06-01"), results.lastEntry().getKey());
        assertEquals(0, results.get(parse("2024-06-01")).momGrowthFactor().compareTo(new BigDecimal("1.002138")));
        assertEquals(0, results.get(parse("2024-06-01")).yoyGrowthFactor().compareTo(new BigDecimal("1.025188")));

        assertEquals(0, results.get(parse("2024-01-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.028")));
        assertEquals(0, results.get(parse("2024-02-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.026")));
        assertEquals(0, results.get(parse("2024-03-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.024")));
        assertEquals(0, results.get(parse("2024-04-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.024")));
        assertEquals(0, results.get(parse("2024-05-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.026")));
        assertEquals(0, results.get(parse("2024-06-01")).yoyGrowthFactor().setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("1.025")));

    }
}