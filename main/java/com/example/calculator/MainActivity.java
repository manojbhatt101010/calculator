package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    TextView exp;
    Button backspace;
    boolean isPreviousResult;
    String invalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exp = findViewById(R.id.exp);
        backspace = findViewById(R.id.backspace);
        isPreviousResult = false;
        invalid = "Invalid Expression";

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expression = exp.getText().toString();
                if(expression.length() == 1)
                    exp.setText("0");
                else if(expression.equals(invalid))
                    exp.setText("0");
                else
                    exp.setText(expression.substring(0,  expression.length() - 1));
            }
        });

        backspace.setLongClickable(true);
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                exp.setText("0");

                return true;
            }
        });
    }

    public void addToExpression(View v) {
        Button clicked = (Button)v;
        char toAdd = clicked.getText().toString().charAt(0);

        if(isPreviousResult) {
            exp.setText("0");
            isPreviousResult = false;
        }

        String expression = exp.getText().toString();
        char last = expression.charAt(expression.length() - 1);

        if(expression.equals("0")) {
            if (toAdd != '(' && toAdd != '-' && !Character.isDigit(toAdd))
                return;
            expression = "";
        }

        if(last == '.' && toAdd == '.')
            return;

        if((last == 'x' || last == '÷' || last == '(' || last == '+' || last == '-') && (toAdd == '+' || toAdd == 'x' || toAdd == '÷'))
            return;

        if((last == '÷' || last == '-') && toAdd == '-')
            return;

        expression += toAdd;
        exp.setText(expression);
    }

    public String correctExpression(String expression) {
        for(int i = 0; i < expression.length() - 1;) {
            if((Character.isDigit(expression.charAt(i)) && expression.charAt(i + 1) == '(') || (expression.charAt(i) == ')' && Character.isDigit(expression.charAt(i + 1)))) {
                expression = expression.substring(0, i + 1) + 'x' + expression.substring(i + 1);
                i += 2;
            }
            else
                i++;
        }
        return expression;
    }

    public int getPrecedence(char c) {
        if(c == '+' || c == '-')
            return 1;
        else if(c == 'x' || c == '÷')
            return 2;
        else
            return -1;
    }

    public String toPostfix(String expression) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        int length = expression.length();

        try {
            for (int i = 0; i < length; i++) {
                char c = expression.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    StringBuilder n = new StringBuilder();
                    while (Character.isDigit(c) || c == '.') {
                        n.append(c);
                        i++;

                        if(i >= length)
                            break;

                        c = expression.charAt(i);
                    }
                    i--;
                    result.append(n);
                    result.append(" ");
                } else if (c == '(')
                    stack.push(c);

                else if (c == ')') {
                    while(!stack.isEmpty() && stack.peek() != '(') {
                        result.append(stack.pop());
                        result.append(" ");
                    }

                    if(!stack.isEmpty() && stack.peek() != '(')
                        throw new Exception();

                    else
                        stack.pop();
                }

                else {
                    while(!stack.isEmpty() && getPrecedence(c) <= getPrecedence(stack.peek())) {
                        if(stack.peek() == '(')
                            throw new Exception();
                        result.append(stack.pop());
                        result.append(" ");
                    }
                    stack.push(c);
                }
            }

            while(!stack.isEmpty()) {
                if(stack.peek() == '(')
                    throw new Exception();
                result.append(stack.pop());
                result.append(" ");
            }
        }
        catch (Exception e) {
            return invalid;
        }

        return result.toString();
    }

    public String evaluatePostfixExpression(String expression) {
        Stack<Double> stack = new Stack<>();

        try {
            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (c == ' ')
                    continue;

                else if (Character.isDigit(c) || c == '.') {
                    StringBuilder n = new StringBuilder();
                    while (Character.isDigit(c) || c == '.') {
                        n.append(c);
                        i++;
                        c = expression.charAt(i);
                    }
                    i--;
                    stack.push(Double.parseDouble(n.toString()));
                } else {
                    double a = stack.pop();
                    double b = stack.pop();

                    switch (c) {
                        case '+':
                            stack.push(a + b);
                            break;

                        case '-':
                            stack.push(b - a);
                            break;

                        case 'x':
                            stack.push(b * a);
                            break;

                        case '÷':
                            stack.push(b / a);
                            break;
                    }

                }
            }

            double result = stack.pop();
            if (result - (int) result == 0)
                return Integer.toString((int) result);

            return Double.toString(result);
        }
        catch (Exception e) {
            return invalid;
        }
    }

    public void evaluateExpression(View v) {
        String expression = correctExpression(exp.getText().toString());
        expression = toPostfix(expression);

        if(expression.equals(invalid))
            exp.setText(expression);
        else
            exp.setText(evaluatePostfixExpression(expression));

        isPreviousResult = true;
    }
}
