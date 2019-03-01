package com.github.mikesafonov.starter.smpp;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.github.mikesafonov.starter.smpp.sender.UnknownTypeOfAddressParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mike Safonov
 */
class UnknownTypeOfAddressParserTest {

    private static final TypeOfAddress unknown = new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);

    private UnknownTypeOfAddressParser addressParser = new UnknownTypeOfAddressParser();

    private static List<String> sources() {
        return asList("TEST-A", "Test A", "Test_a", "Hello", "380223092235", "780223092235");
    }

    @ParameterizedTest
    @MethodSource("sources")
    void shouldReturnUnknownAddress(String source) {
        TypeOfAddress typeOfAddress = addressParser.getSource(source);

        assertEquals(unknown.getTon(), typeOfAddress.getTon());
        assertEquals(unknown.getNpi(), typeOfAddress.getNpi());
    }
}
