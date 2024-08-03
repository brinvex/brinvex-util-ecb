package com.brinvex.util.ecb.api;

import com.brinvex.util.ecb.api.EcbFetchRequest.DepositFacilityRateRequest;
import com.brinvex.util.ecb.api.EcbFetchRequest.FxRequest;
import com.brinvex.util.ecb.api.EcbFetchRequest.HICPInflationRequest;
import com.brinvex.util.ecb.api.EcbFetchResponse.InflationResponse;

import java.time.LocalDate;
import java.util.SortedMap;

public interface EcbFetchService {

    SortedMap<LocalDate, Double> fetch(FxRequest fxReq);

    SortedMap<LocalDate, Double> fetch(DepositFacilityRateRequest depoFacilityRateReq);

    SortedMap<LocalDate, InflationResponse> fetch(HICPInflationRequest depoFacilityRateReq);
}
