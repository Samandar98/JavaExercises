package calculations;

import interpreter.Interpreter;
import util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Calculate {

    public final static String INVALID_NUMBER_FORMAT = "Error: Invalid number format '%s'";
    private final static String DIVISION_BY_ZERO = "Error: Division by zero";

    /**
     * Returns the result of an expression or null on error
     * @param expression in string
     * @param variables map of variables
     * @return float number or null
     */
    public static Float calculate(String expression, Map<String, Float> variables) {
        return calculatePostfix(infixToPostfix(expression), variables);
    }

    private static Float calculatePostfix(List<String> list, Map<String, Float> variables) {
        LinkedList<Float> stack = new LinkedList<>();
        float second;
        for (String str : list) {
            switch (str) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "^":
                    second = stack.pop();
                    stack.push((float) Math.pow(stack.pop(), second));
                    break;
                case "-":
                    second = stack.pop();
                    stack.push(stack.pop() - second);
                    break;
                case "/":
                    second = stack.pop();
                    if (second == 0) {
                        System.out.printf(DIVISION_BY_ZERO);
                        return null;
                    }
                    stack.push(stack.pop() / second);
                    break;
                case "FABS":
                    stack.push(stack.pop() * (-1));
                    break;
                default:
                    try {
                        stack.push(Float.parseFloat(str));
                    } catch (NumberFormatException ex) {
                        if (Util.isValidVariableName(str)) {
                            stack.push(variables.getOrDefault(str.toUpperCase(), 0f));
                        } else {
                            System.out.printf(INVALID_NUMBER_FORMAT, str);
                            return null;
                        }
                    }
            }
        }
        return stack.pop();
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> result = new ArrayList<>();
        LinkedList<Character> stackOper = new LinkedList<>();
        LinkedList<String> stackFunc = new LinkedList<>();
        String numberOrVariable = "";
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ') { // ignore spaces
                continue;
            }

            // added in list number or variable
            if (precedence(c) > -1 && !numberOrVariable.isEmpty()) {
                if (c == '(' && numberOrVariable.toUpperCase().startsWith("F")) {
                    stackFunc.push(numberOrVariable.toUpperCase());
                } else {
                    result.add(numberOrVariable);
                }
                numberOrVariable = "";
            }

            // check if char is operator +,-,*,/,^
            if (precedence(c) > 0) {
                while(!stackOper.isEmpty() && precedence(stackOper.peek()) >= precedence(c)) {
                    result.add(stackOper.pop().toString());
                }
                stackOper.push(c);
            } else if (c == ')') {
                char x = stackOper.pop();
                while (x != '('){
                    result.add(Character.toString(x));
                    x = stackOper.pop();
                }
                if (stackFunc.size() > 0 && stackOper.size() == 0) {
                    result.add(stackFunc.pop());
                }
            } else if (c == '(') {
                stackOper.push(c);
            } else {
                // character is neither operator nor (,)
                numberOrVariable += Character.toString(c);
            }
        }
        if (!numberOrVariable.isEmpty()) {
            result.add(numberOrVariable);
        }
        while (stackOper.size() > 0) {
            result.add(stackOper.pop().toString());
        }
        return result;
    }

    private static int precedence(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            case '(':
            case ')':
                return 0;
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(infixToPostfix("FABS(1+2*(2+2)) + 3"));
    }

}