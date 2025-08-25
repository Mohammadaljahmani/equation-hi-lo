package io.everyonecodes.equation_hi_lo.service;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

@Service
public class EquationService {


    public double evaluate(String equationString) {

        String sanitizedEquation = equationString.replace("√", "sqrt");

        try {

            ExpressionBuilder builder = new ExpressionBuilder(sanitizedEquation);


            builder.function(new net.objecthunter.exp4j.function.Function("sqrt", 1) {
                @Override
                public double apply(double... args) {
                    return Math.sqrt(args[0]);
                }
            });


            Expression expression = builder.build();

            return expression.evaluate();

        } catch (IllegalArgumentException | ArithmeticException e) {
            System.err.println("Invalid equation provided: '" + equationString + "'. Returning 0.0. Error: " + e.getMessage());
            return 0.0;
        }
    }
}