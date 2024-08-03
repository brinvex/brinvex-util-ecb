package com.brinvex.util.ecb.api;

import java.time.LocalDate;

import static com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest.MomGrowthCalculation.DONT_CALCULATE_MOM_GROWTH;
import static com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest.YoyGrowthCalculation.DONT_CALCULATE_YOY_GROWTH;

public sealed interface EcbFetchRequest {

    record FxRequest(
            String quoteCcy,
            LocalDate startDayIncl,
            LocalDate endDayIncl
    ) implements EcbFetchRequest {
        public FxRequest(String quoteCcy, LocalDate startDayIncl) {
            this(quoteCcy, startDayIncl, null);
        }
    }

    record DepositFacilityRateRequest(
            LocalDate startDayIncl,
            LocalDate endDayIncl,
            EqualSubsequentValuesHandling equalSubsequentValuesHandling
    ) implements EcbFetchRequest {
        public enum EqualSubsequentValuesHandling {
            MERGE_EQUAL_SUBSEQUENT_VALUES,
            DONT_MERGE_EQUAL_SUBSEQUENT_VALUES,
        }

        public DepositFacilityRateRequest(LocalDate startDayIncl) {
            this(startDayIncl, null, EqualSubsequentValuesHandling.DONT_MERGE_EQUAL_SUBSEQUENT_VALUES);
        }

        public DepositFacilityRateRequest(LocalDate startDayIncl, LocalDate endDayIncl) {
            this(startDayIncl, endDayIncl, EqualSubsequentValuesHandling.DONT_MERGE_EQUAL_SUBSEQUENT_VALUES);
        }

        public DepositFacilityRateRequest(LocalDate startDayIncl, EqualSubsequentValuesHandling equalSubsequentValuesHandling) {
            this(startDayIncl, null, equalSubsequentValuesHandling);
        }
    }

    record HICPInflationRequest(
            LocalDate startDayIncl,
            LocalDate endDayIncl,
            MomGrowthCalculation momGrowthCalculation,
            YoyGrowthCalculation yoyGrowthCalculation
    ) implements EcbFetchRequest {

        public enum MomGrowthCalculation {
            CALCULATE_MOM_GROWTH,
            DONT_CALCULATE_MOM_GROWTH,
        }

        public enum YoyGrowthCalculation {
            CALCULATE_WITH_YOY_GROWTH,
            DONT_CALCULATE_YOY_GROWTH,
        }

        public HICPInflationRequest(LocalDate startDayIncl) {
            this(startDayIncl, null, DONT_CALCULATE_MOM_GROWTH, DONT_CALCULATE_YOY_GROWTH);
        }

        public HICPInflationRequest(LocalDate startDayIncl, MomGrowthCalculation momGrowthCalculation, YoyGrowthCalculation yoyGrowthCalculation) {
            this(startDayIncl, null, momGrowthCalculation, yoyGrowthCalculation);
        }

        public HICPInflationRequest(LocalDate startDayIncl, LocalDate endDayIncl) {
            this(startDayIncl, endDayIncl, DONT_CALCULATE_MOM_GROWTH, DONT_CALCULATE_YOY_GROWTH);
        }
    }


}
