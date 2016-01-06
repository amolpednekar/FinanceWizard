package com.example.fizz.financewizard;

/**
 * Created by Amol on 21 Dec 15.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Calc extends AppCompatActivity implements View.OnClickListener{

    Button one, two, three, four, five, six, seven, eight, nine, zero, add, sub, mul, div, cancel, equal,decimal;
    EditText disp;
    float op1;
    float op2;
    String optr="+";
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc);

        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        zero = (Button) findViewById(R.id.zero);
        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.sub);
        mul = (Button) findViewById(R.id.mul);
        div = (Button) findViewById(R.id.div);
        cancel = (Button) findViewById(R.id.Reset);
        equal = (Button) findViewById(R.id.equal);
        decimal=(Button)findViewById(R.id.decimal);

        disp = (EditText) findViewById(R.id.display);

        try{
            one.setOnClickListener(this);

            two.setOnClickListener(this);

            three.setOnClickListener(this);

            four.setOnClickListener(this);

            five.setOnClickListener(this);

            six.setOnClickListener(this);

            seven.setOnClickListener(this);

            eight.setOnClickListener(this);

            nine.setOnClickListener(this);

            zero.setOnClickListener(this);

            cancel.setOnClickListener(this);

            add.setOnClickListener(this);

            sub.setOnClickListener(this);

            mul.setOnClickListener(this);

            div.setOnClickListener(this);

            equal.setOnClickListener(this);

            decimal.setOnClickListener(this);
        }
        catch(Exception e){

        }
    }
    public void operation(){
        if(optr.equals("+")){
            op2 = Float.parseFloat(disp.getText().toString());
            disp.setText("");
            op1 = op1 + op2;
            disp.setText(Float.toString(op1));
        }
        else if(optr.equals("-")){
            op2 = Float.parseFloat(disp.getText().toString());
            disp.setText("");
            op1 = op1 - op2;
            disp.setText(Float.toString(op1));
        }
        else if(optr.equals("*")){
            op2 = Float.parseFloat(disp.getText().toString());
            disp.setText("");
            op1 = op1 * op2;
            disp.setText( Float.toString(op1));
        }
        else if(optr.equals("/")){
            op2 = Float.parseFloat(disp.getText().toString());
            disp.setText("");
            op1 = op1 / op2;
            if(op2==0){
                disp.setText("Infinity");
            }else
            disp.setText(Float.toString(op1));
        }
        optr="+";

    }
    @Override
    public void onClick(View arg0) {
        Editable str =  disp.getText();

        switch(arg0.getId()){

            case R.id.decimal:
                str = str.append(decimal.getText());
                disp.setText(str);
                break;
            case R.id.zero:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("0");
                    flag=0;
                }
                else {
                str = str.append(zero.getText());
                disp.setText(str);}
                break;
            case R.id.one:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("1");
                    flag=0;
                }
                else {
                    str = str.append(one.getText());
                    disp.setText(str);
                }
                break;
            case R.id.two:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("2");
                    flag=0;
                }
                else{
                str = str.append(two.getText());
                disp.setText(str);}
                break;
            case R.id.three:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("3");
                    flag=0;
                }
                else {
                str = str.append(three.getText());
                disp.setText(str);}
                break;
            case R.id.four:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("4");
                    flag=0;
                }
                else {
                str = str.append(four.getText());
                disp.setText(str);}
                break;
            case R.id.five:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("5");
                    flag=0;
                }
                else {
                str = str.append(five.getText());
                disp.setText(str);}
                break;
            case R.id.six:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("6");
                    flag=0;
                }
                else {
                str = str.append(six.getText());
                disp.setText(str);}
                break;
            case R.id.seven:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("7");
                    flag=0;
                }
                else {
                str = str.append(seven.getText());
                disp.setText(str);}
                break;
            case R.id.eight:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("8");
                    flag=0;
                }
                else {
                str = str.append(eight.getText());
                disp.setText(str);}

                break;
            case R.id.nine:
                if(flag==1){
                    op1=0;
                    op2=0;
                    disp.setText("9");
                    flag=0;
                }
                else {
                str = str.append(nine.getText());
                disp.setText(str);}
                break;
            case R.id.Reset:
                op1 = 0;
                op2 = 0;
                disp.setText("");

                break;
            case R.id.add:
                optr = "+";
                if(disp.getText().toString().trim().length() <= 0) return;
                else if(op1 == 0){
                    op1 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                }
                else{
                    op2 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                    op1 = op1 + op2;
                }
                break;
            case R.id.sub:
                optr = "-";
                if(disp.getText().toString().trim().length() <= 0) return;
                else if(op1 == 0){
                    op1 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                }
                else{
                    op2 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                    op1 = op1 - op2;
                }
                break;
            case R.id.mul:
                optr = "*";
                if(disp.getText().toString().trim().length() <= 0) return;
            else if(op1 == 0){
                    op1 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                }
                else{
                    op2 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                    op1 = op1 * op2;
                }
                break;
            case R.id.div:
                optr = "/";
                if(disp.getText().toString().trim().length() <= 0) return;
                else if(op1 == 0){
                    op1 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                }
                else{
                    op2 = Float.parseFloat(disp.getText().toString());
                    disp.setText("");
                    op1 = op1 / op2;
                }
                break;
            case R.id.equal:
                flag=1;
                if(disp.getText().toString().trim().length() <= 0)
                    return;
                 else
                if(!optr.equals(null)){
                        operation();
                }
                break;
        }
    }
}