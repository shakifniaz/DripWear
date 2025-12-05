package com.example.dripwear.AdapterPattern;

public class UsdToBdtService {
    private static final double USD_TO_BDT_RATE = 120.0;

    public double convertUsdToBdt(double usd) {
        return usd * USD_TO_BDT_RATE;
    }

    public double rate() {
        return USD_TO_BDT_RATE;
    }
}
