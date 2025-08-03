package com.example.dripwear.Domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class BannerModelTest {

    @ParameterizedTest
    @MethodSource("urlProvider")
    void testUrlHandling(String inputUrl, String expectedOutput) {
        BannerModel banner = new BannerModel();
        banner.setUrl(inputUrl);
        assertEquals(expectedOutput, banner.getUrl());
    }

    static Stream<Arguments> urlProvider() {
        return Stream.of(
                Arguments.of("https://valid.com", "https://valid.com"),
                Arguments.of("http://test.com",   "http://test.com"),
                Arguments.of("",                  ""),
                Arguments.of(null,                null),
                Arguments.of("invalid url",       "invalid url")
        );
    }
}