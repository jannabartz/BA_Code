package com.example.ba_transaktionen.klassen;

import com.example.ba_transaktionen.Controller;

import java.util.Arrays;
import java.util.List;

public class Zentralbank {

    public static double leitzinsEuro;
    public static double leitzinsDollar;
    //statistische Werte der Zinsentwicklung des Euro von 2008 bis 2023
    private static final List<Double> ZINS_AENDERUNGEN_EURO = Arrays.asList(-0.25,-0.5, -0.5, -0.5,-0.25,-0.5,-0.5,0.0,0.0,+0.5,0.0, -0.5, -0.25, -0.25, 0.0, 0.0, 0.0,0.5,0.5,0.5,0.5,1.0,1.0,0.5);
    //statistische Werte der Zinsentwicklung des Dollar von 2008 bis 2023
    //die Listen haben eine unterschiedliche Anzahl an Werten, damit sich der Graph nicht gleichzeitig ändert
    private static final List<Double> ZINS_AENDERUNGEN_DOLLAR = Arrays.asList(-0.5,-1.0, 0.0, -0.5,-0.5,-0.5,-1.0,0.0,0.0,0.0,0.0, 0.0, 0.25, 0.25, 0.5, 0.5, 0.5,-0.5,-1.0,-0.5,0.0,0.0,0.5,1.0,1.0,1.0,1.0);

    public int currentIndexEuro = 0;
    public int currentIndexDollar = 0;
    private double inflation;


    //Entwicklung wird anhand der letzen Jahresentwicklung bestimmt
    public double zinsDevelopmentEuro(){

        //muss in jedem step aufgerufen werden, damit die Zinsen immer aktualisieren
        //wird trotzdem Minus gerechnet nach den mathematischen Rechengesetzen?
        leitzinsEuro += ZINS_AENDERUNGEN_EURO.get(currentIndexEuro);
        //die Entwicklung soll zwischen den Grenzen 6,5 und 0 liegen
        if(leitzinsEuro<0){
            currentIndexEuro = 14;
            leitzinsEuro = 0;
        }
        if(leitzinsEuro>4.0){
            currentIndexEuro = 0;
            leitzinsEuro = 4.0;
        }

        currentIndexEuro++;
        if(currentIndexEuro>=ZINS_AENDERUNGEN_EURO.size()){
            currentIndexEuro = 0;
        }
        return leitzinsEuro;
    }

    public double zinsDevelopmentDollar(){

        leitzinsDollar += ZINS_AENDERUNGEN_DOLLAR.get(currentIndexDollar);
        if(leitzinsDollar<0){
            currentIndexDollar = 22;
            leitzinsDollar = 0;
        }
        if(leitzinsDollar>4.0){
            currentIndexDollar = 0;
            leitzinsDollar = 4.0;
        }

        currentIndexDollar++;
        if(currentIndexDollar>=ZINS_AENDERUNGEN_DOLLAR.size()){
            currentIndexDollar = 0;
        }
        return leitzinsDollar;
    }

    public double getLeitzinsEuro() {
        return leitzinsEuro;
    }

    public static double getLeitzinsDollar() {
        return leitzinsDollar;
    }

    //Wenn der Leitzins einer Vergleichswährung (hier Dollar) höher ist, dann wird international mehr in diese Währung investiert und
    //die Nachfrage nach dem Euro sinkt, d.h. der Euro wird abgewertet und die Inflation steigt
    public double berechneInflation(){
        if(Zentralbank.leitzinsDollar>Zentralbank.leitzinsEuro){
            //Differenz zwischen Leitzinsdollar und Leitzinseuro berechnen und um so viel Prozent die Inflation steigen
            inflation = Zentralbank.leitzinsDollar-Zentralbank.leitzinsEuro;
        }
        return inflation;
    }



}