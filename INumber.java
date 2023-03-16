package com.company;

public interface INumber {
    double sum(INumber number);
    INumber add(INumber number);
    double sub(INumber number);
    INumber subtract(INumber number);
    double mult(INumber number);
    INumber multiply(INumber number);
    double divide(INumber number);
    INumber division(INumber number);
    double getValue();
}
