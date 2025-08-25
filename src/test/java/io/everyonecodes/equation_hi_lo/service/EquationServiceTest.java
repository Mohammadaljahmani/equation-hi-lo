package io.everyonecodes.equation_hi_lo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EquationServiceTest {

    private final EquationService equationService = new EquationService();

    @ParameterizedTest
    @CsvSource({
            "10.0, 5 * 2",
            "13.0, 10 + sqrt(9)",
            "5.0, 10 / 2",
            "25.0, 5 * (2+3)",
            "0.0, 5 / 0",
            "0.0, 5 + * 2"
    })
    void evaluate(double expected, String expression) {
        double result = equationService.evaluate(expression);
        Assertions.assertEquals(expected, result);
    }
}