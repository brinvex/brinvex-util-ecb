package com.brinvex.util.ecb.api;

import java.math.BigDecimal;

public sealed interface EcbFetchResponse {

    record InflationResponse(
            BigDecimal cpi,
            BigDecimal momGrowthFactor,
            BigDecimal yoyGrowthFactor
    ) implements EcbFetchResponse {
    }


}
