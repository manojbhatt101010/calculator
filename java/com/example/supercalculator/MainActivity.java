package com.example.supercalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isPreviousResult = false;

    public void calculateExpression(View view)
    {
        TextView expression = findViewById(R.id.expression);
        TextView result = findViewById(R.id.result);

        String exp = expression.getText().toString();
        exp = correctExpression(exp);
        char[] tokens = exp.toCharArray();

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        try {
            for (int i = 0; i < tokens.length; i++)
            {
                if (isNumber(tokens[i]) || tokens[i] == '.')
                {
                    StringBuilder number = new StringBuilder();

                    while (i < tokens.length && (isNumber(tokens[i]) || tokens[i] == '.'))
                        number.append(tokens[i++]);

                    values.push(Double.parseDouble(number.toString()));
                    i--;
                }
                else if (tokens[i] == '(')
                    operators.push(tokens[i]);

                else if (tokens[i] == ')') {
                    while (operators.peek() != '(')
                        values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

                    operators.pop();
                }
                else
                {
                    if (tokens[i] == '-')
                        if (i == 0 || tokens[i - 1] == 'x' || tokens[i - 1] == '(' || tokens[i - 1] == '÷') {
                            StringBuilder number = new StringBuilder();

                            while (i < tokens.length && (isNumber(tokens[i]) || tokens[i] == '.' || tokens[i] == '-'))
                                number.append(tokens[i++]);

                            values.push(Double.parseDouble(number.toString()));
                            i--;
                            continue;
                        }

                    while (!operators.empty() && hasPrecedence(tokens[i], operators.peek()))
                        values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

                    operators.push(tokens[i]);
                }
            }

            while (!operators.empty())
                values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

            double answer = values.pop();
            String resultString;
            if (answer - (int) answer == 0)
                resultString = Integer.toString((int) answer);
            else
                resultString = Double.toString(answer);

            expression.setText(resultString);
        }
        catch (Exception e){
            String wrongExp = "Wrong Expression";
            result.setText(wrongExp);
        }

        isPreviousResult = true;
    }

    public boolean hasPrecedence(char op1, char op2)
    {
        if(op2 == '(' || op2 == ')')
            return false;
        return !((op1 == 'x' || op1 == '÷') && (op2 == '+' || op2 == '-'));
    }

    public double applyOperation(char operator, double b, double a)
    {
        switch(operator)
        {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case 'x':
                return a * b;
            case '÷':
                return a / b;
        }
        return 0;
    }

    public String correctExpression(String exp)
    {
        for(int i = 0; i < exp.length() - 1;)
        {
            if(isNumber(exp.charAt(i)) && exp.charAt(i + 1) == '(') {
                exp = exp.substring(0, i + 1) + 'x' + exp.substring(i + 1);
                i += 2;
            }

            else if(exp.charAt(i) == ')' && isNumber(exp.charAt(i+1))){
                exp = exp.substring(0, i +1 ) + 'x' + exp.substring(i + 1);
                i += 2;
            }

            else
                i++;
        }
        return exp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button[] arr = new Button[15];
        final TextView expression = findViewById(R.id.expression);
        final TextView result = findViewById(R.id.result);

        for(int i = 0; i < 11; i++)
        {
            int buttonId = getResources().getIdentifier("num_" + i, "id", getPackageName());
            arr[i] = findViewById(buttonId);
            arr[i].setOnClickListener(this);
        }

        for(int i = 11; i < 15; i++)
        {
            int buttonId = getResources().getIdentifier("operator_" + (i - 11), "id", getPackageName());
            arr[i] = findViewById(buttonId);
            arr[i].setOnClickListener(this);
        }

        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expression.setText("0");
                result.setText("");
            }
        });

        Button backspace = findViewById(R.id.backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
                String currentText = expression.getText().toString();
                if(currentText.length() == 1){
                    expression.setText("0");
                    return;
                }

                currentText = currentText.substring(0, currentText.length() - 1);
                expression.setText(currentText);
            }
        });

        Button ob = findViewById(R.id.ob), cb = findViewById(R.id.cb);
        ob.setOnClickListener(this);
        cb.setOnClickListener(this);
    }

    public boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    @Override
    public void onClick(View view) {
        Button button = (Button)view;
        String toPush = button.getText().toString();
        TextView expression = findViewById(R.id.expression);
        TextView result = findViewById(R.id.result);
        result.setText("");
        String currentText = expression.getText().toString();
        char last = currentText.charAt(currentText.length() - 1);

        if(currentText.equals("0")) {
            if (toPush.charAt(0) != '-' && toPush.charAt(0) != '(' && !isNumber(toPush.charAt(0)))
                return;
            currentText = "";
        }

        else if(isPreviousResult){
            if(isNumber(toPush.charAt(0)))
                currentText = "";

            isPreviousResult = false;
        }

        if(last == 'x' || last == '÷' || last == '(' || last == '+' || last == '-')
            if(toPush.equals("+") || toPush.equals("x") || toPush.equals("÷"))
                toPush = "";

        if((last == '+' || last == '-') && toPush.equals("-"))
            toPush = "";


        currentText += toPush;
        if(currentText.length() > 13)
            expression.setTextSize(40);
        else if(currentText.length() > 7)
            expression.setTextSize(50);
        expression.setText(currentText);
    }
}