package com.example.dripwear.AdapterPattern;

public class BdtToUsdAdapter implements CurrencyConverter {
    private final UsdToBdtService usdService;

    public BdtToUsdAdapter(UsdToBdtService usdService) {
        this.usdService = usdService;
    }

    @Override
    public double convert(double bdt) {
        return bdt / usdService.rate();
    }
}
