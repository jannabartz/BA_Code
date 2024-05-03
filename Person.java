package com.example.ba_transaktionen.klassen;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

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
    //Coins, die jede Person am Anfang hat
    public int coinAnzahl;

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
    //Liste mit allen Coins, die in einer Transaktion ausgegeben werden
    public static List<Coins> alleCoins = new ArrayList<>();

    public Person (Zustand zustand, Pane welt){
        this.zustand = zustand;
        this.punkt = new Punkt();
        this.position = new Position(welt);
        this.coinAnzahl = Coins.anzahlCoins;
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
                //es soll geguckt werden ob Coins zur Verfügung stehen und daraufhin soll ein neuer Coin ausgegeben werden
                if(this.coinAnzahl!=0){
                    //wenn Teilnehmer noch Coins hat, dann wird ein Coin an den anderen übertragen
                    setZustand(Zustand.transaktion);
                    andere.setZustand(Zustand.transaktion);
                    Coins coinTransaktion = new Coins();
                    coinTransaktion.ausgegeben = true;
                    //coinTransaktion.besitzer = andere;
                    //der andere Teilnehmer will Coins von dem Teilnehmer this und wird somit zum Nachfrager nach Coins
                    Coins.nachfrageListe.add(andere);
                    //die eine Person überträgt einen Coin an die andere Person
                    andere.coinAnzahl++;
                    this.coinAnzahl--;
                    // Fügen Sie den neuen Coin zur Liste hinzu
                    alleCoins.add(coinTransaktion);
                }
                this.paarZahl = transaktionsPaar;
                andere.paarZahl = transaktionsPaar;
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

                //ist die Transaktion abgeschlossen, ist die andere Person kein Nachfrager mehr
                Coins.nachfrageListe.remove(andere);
                this.transaktionszähler = 0;
                andere.transaktionszähler = 0;
                this.paarZahl = 0;
                andere.paarZahl = 0;
            }
        }
    }

    //Problem: nach einiger Zeit funktionieren werden die Punkte nicht mehr blau angezeigt
    //bei Wkeit 1 sollte bei jeder Kollision eine Transaktion stattfinden
    //Gefühl: wird nur blau angezeigt, wenn Punkt noch nicht in Transaktionszustand war
    //überlegung: Punkte können nicht nochmal blau werden, wenn sie schon in Transaktion waren, was ist die Lösung?

}
