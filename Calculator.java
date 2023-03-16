package com.company;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.security.KeyPair;
import java.util.*;

import static com.company.Decimal.reduce;
import static com.company.Decimal.tryParse;

public class Calculator {

    private static int getPriority(String operation) {
        return operation.equals("/") || operation.equals("*") ? 1 : 0;
    }

    private static void sort(List<Map.Entry<Integer, String>> operations) {
        for (int i = 0; i < operations.size() - 1; i++) {
            for (int j = 0; j < operations.size() - i - 1; j++) {
                if (getPriority(operations.get(i).getValue()) < getPriority(operations.get(i + 1).getValue())) {
                    Map.Entry<Integer, String> temp = operations.get(i);
                    operations.set(i, operations.get(i + 1));
                    operations.set(i + 1, temp);
                }
            }
        }
    }

    private static INumber calculateOneOperation(Decimal one, String operation, Decimal two) {
        switch (operation) {
            case "*" -> {
                return Decimal.multiply(one, two);
            }
            case "-" -> {
                return Decimal.subtract(one, two);
            }
            case "+" -> {
                return Decimal.add(one, two);
            }
            case "/" -> {
                return Decimal.division(one, two);
            }
            default -> throw new IllegalArgumentException("Incorrect operation");
        }
    }
    public static void isCorrectBracket(String operation) {
        operation = operation.replace("[0-9]*", "");
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < operation.length(); i++) {
            if(operation.charAt(i) == '(') {
                stack.push(operation.charAt(i));
            } else if(operation.charAt(i) == ')') {
                if(stack.isEmpty() || stack.peek() != '(') throw new IllegalArgumentException("Некорректное выражение");
                stack.pop();
            }
        }
        if(!stack.isEmpty()) throw new IllegalArgumentException("Некорректное выражение");
    }

    public static Decimal calculate(String operation) throws IllegalArgumentException {
        //List<String> decimals = new ArrayList<>();
        Decimal result = null;
        isCorrectBracket(operation);
        while(true) {
            String[] brackets = operation.split("\\(|\\)");
            if(brackets.length == 0) break;
            for (String bracket : brackets) {
                if (bracket.isEmpty() || bracket.equals(" ")) continue;
                List<Map.Entry<Integer, String>> operations = new ArrayList<>();
                List<String> decimals = new ArrayList<>();
                decimals.addAll(Arrays.asList(bracket.split(" ")));
                decimals.removeIf(a-> a.equals("") || a.equals(" "));
                for (int i = 0; i < decimals.size(); i++) {
                    if (!decimals.get(i).equals("") && !tryParse(decimals.get(i))) {
                        operations.add(Map.entry(i, decimals.get(i)));
                    } else if (i > 0 && tryParse(decimals.get(i - 1))) {
                        decimals.add(i, "*");
                        operations.add(Map.entry(i++, "*"));
                    }
                }
                sort(operations);
                for (Map.Entry<Integer, String> op : operations) {
                    int index = op.getKey();
                    Decimal one = Decimal.parse(decimals.get(index - 1));
                    Decimal two = Decimal.parse(decimals.get(index + 1));
                    result = (Decimal) calculateOneOperation(one, op.getValue(), two);
                    decimals.set(index + 1, result.toString());
                    decimals.set(index - 1, result.toString());
                }
                if(brackets.length == 1 && brackets[brackets.length - 1].equals(bracket)) return reduce(result);
                operation = operation.replace("(" + bracket + ")", result.toString() + " ");
                operation = operation.replace(bracket, result.toString());
                break;
            }
        }
        return reduce(result);
    }
}
