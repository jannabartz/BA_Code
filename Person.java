package com.example.ba_transaktionen.klassen;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Person {

    public static int radius = 5;
    //5 Ticks/Steps um Transaktion durchzuführen
    public static int transaktionszeit = 20;
    //in welchem Radius (vom Regler manipulierbar) bewegen sich die Punkte
    public static int bewegungsradius = 200;
    //Anzahl der Transaktionen die grade stattfinden (also blau leuchten), für Anteildiagramm
    public static double summePersonenTransaktionen;
    //Anzahl der Coins die insgesamt im Umlauf sind, für Coins-Graph, werden aufsummiert
    public static int coinsUmlaufWert;
    //Anzahl der Coins, die der Hacker besitzt
    public static int coinsHacker;
    //Anzahl Teilnehmer, die nicht gehackt werden können
    public static int teilnehmerAbwehr;
    //Anzahl Teilnehmer, die gehackt wurden
    public static int teilnehmerGehackt;
    //Zahl, um Personenpaare zu identifizieren, die eine Transaktion durchführen
    public static int transaktionsPaar = 0;

    //Zustand des Teilnehmers
    private Zustand zustand;
    //Position des Teilnehmers auf der Pane
    private Position position;
    //Ursprungsposition des Teilnehmers auf der Pane
    private Position ursprung;
    //Teilnehmer als punkt dargestellt
    private Punkt punkt;
    //Kreis mit Radius und Zustandsfarbe des Teilnehmers
    private Circle kreis;
    //Pane auf der die Punkte angezeigt werden
    private Pane welt;
    //Zähler um zu gucken wie lange eine Transaktion dauert
    private int transaktionszähler = 0;
    //Zahl die jeder Person zugeordnet wird, um den Transaktionspartner zu finden
    private int paarZahl;

    public Person (Zustand zustand, Pane welt){
        this.zustand = zustand;
        this.punkt = new Punkt();
        this.position = new Position(welt);
        //man kann den ursprung nicht gleich position setzen, da sich diese ändert
        this.ursprung = new Position(position.getX(), position.getY());
        this.kreis = new Circle(radius, zustand.getColor());
        this.welt = welt;
        kreis.setStroke(Color.BLACK);
        welt.getChildren().add(kreis);
    }

    public Zustand getZustand(){
        return zustand;
    }

    public void setZustand(Zustand zustand){
        this.zustand = zustand;
        kreis.setFill(zustand.getColor());
        //System.out.println(zustand.getColor());
    }

    public void bewegen(){
        position.bewegen(punkt, welt, ursprung);
    }

    public void punktAnzeigen(){
        kreis.setRadius(radius);
        kreis.setTranslateX(position.getX());
        kreis.setTranslateY(position.getY());
    }

    /**
     * Methode zum Prüfen ob zwei Punkte aufeinander treffen und zum Ausführen einer Transaktion
     *
     * @param andere Punkt mit dem Kollision geprüft wird
     * @param WkeitTransaktion Wahrscheinlichkeit mit der Transaktion ausgeführt wird
     */
    public void kollisionCheck(Person andere, double WkeitTransaktion){
        if(position.kollidieren(andere.position)){
            //Math.random Zahl von 0 bis kleiner 1, bestimmt Wkeit von Stattfinden der Transaktion
            double zufallszahl = Math.random();
            //wenn zwei Punkte kollidieren, führen sie Transaktion mit Wahrscheinlichkeit von Slider aus
            //können nur Transaktion ausführen, wenn sie grade nicht in einer sind
            if(andere.getZustand()== Zustand.neutral && zustand == Zustand.neutral && zufallszahl<WkeitTransaktion){
                setZustand(Zustand.transaktion);
                andere.setZustand(Zustand.transaktion);
                //System.out.println("Ich bin in Kollision");
                if(this.paarZahl==0 && andere.paarZahl==0){
                    System.out.println("erste Transaktion");
                }
                else {
                    System.out.println("zweite Transaktion");
                }
                this.paarZahl = transaktionsPaar;
                andere.paarZahl = transaktionsPaar;
                //System.out.println(this.paarZahl + " andere:" + andere.paarZahl + " zaehler:" + transaktionsPaar);
                //System.out.println(this.getZustand() + " andere:" + andere.getZustand());
                transaktionsPaar++;
                //damit Anteil der Personen berechnet werden kann, die in einer Transaktion sind
                summePersonenTransaktionen = summePersonenTransaktionen +2;
                //bei jeder Transaktion geraten 1-5 Coins in Umlauf, diese werden addiert, sodass mit zunehmenden
                //Transaktionen auch zunehmende Coins in Umlauf geraten
                coinsUmlaufWert += (int) Math.random()*5 +1;
            }
        }
    }

    /**
     * Methode zum Prüfen ob der Hacker kollidiert oder ein gehackter Teilnehmer
     *
     * @param other Punkt mit dem Kollision geprüft wird
     * @param WkeitHackererfolg Wahrscheinlichkeit mit dem Hacker Teilnehmer hackt
     */
    public void hackerCheck(Person other, double WkeitHackererfolg) {
        if(position.kollidieren(other.position)){
            double zufallszahl = Math.random();
            if(other.getZustand()== Zustand.hacker && zustand == Zustand.neutral && zufallszahl<WkeitHackererfolg){
                //wenn die Wahrscheinlichkeit des Hackererfolgs stimmt, dann wird der Teilnehmer gehackt und rot markiert
                setZustand(Zustand.hackerangriff);
                teilnehmerGehackt++;
                //bei jedem Hackererfolg, kommen 1-5 Coins an den Hacker
                coinsHacker += (int) Math.random()*5 +1;
            }
            //mit einer Wkeit von 30% warnt der gehackte Teilnehmer den nicht gehackten
            if(other.getZustand()== Zustand.hackerangriff && zustand == Zustand.neutral && zufallszahl<0.3){
                //der gehackte Teilnehmer, teilt dem anderen mit, sich besser zu schützen und dieser tut das
                setZustand(Zustand.hackerabwehr);
                teilnehmerAbwehr++;
            }
        }
    }

    /**
     * Methode zum Beenden der Transaktion
     *
     * @param andere Punkt mit dem Transaktion durchgeführt wurde
     * verwendet einen Zähler, um den Zeitraum der Transaktion zu bestimmen
     */
    public void transaktionAbschließen(Person andere){
        if(zustand == Zustand.transaktion && andere.getZustand()== Zustand.transaktion && this.paarZahl==andere.paarZahl){
            transaktionszähler++;
            //wenn Zeit abgelaufen ist, sollen die Transaktionsteilnehmer wieder neutral sein
            if(transaktionszähler>transaktionszeit){
                //setZustand(Zustand.neutral);
                this.setZustand(Zustand.neutral);
                andere.setZustand(Zustand.neutral);
                summePersonenTransaktionen=summePersonenTransaktionen-2;
            }
        }
    }

    //Problem: nach einiger Zeit funktionieren werden die Punkte nicht mehr blau angezeigt
    //bei Wkeit 1 sollte bei jeder Kollision eine Transaktion stattfinden
    //Gefühl: wird nur blau angezeigt, wenn Punkt noch nicht in Transaktionszustand war
    //überlegung: Punkte können nicht nochmal blau werden, wenn sie schon in Transaktion waren, was ist die Lösung?

}
