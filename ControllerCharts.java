package com.example.ba_transaktionen;

import com.example.ba_transaktionen.klassen.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;


public class ControllerCharts {

    @FXML
    Pane coinsUmlauf;
    @FXML
    Pane anteilTeilnehmer;
    @FXML
    Pane coinsDurchschnitt;
    @FXML
    Pane inflation;
    @FXML
    Pane leitzins;
    @FXML
    Label anteilTransaktionen;
    @FXML
    Label resetInfo;
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox vboxAT;

    //Linien Diagramm für Coins im Umlauf
    LineChart<Number, Number> lineChartUmlauf = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Werte für Linien Diagramm Coins im Umlauf
    XYChart.Series<Number, Number> lineChartDatenUmlauf = new XYChart.Series<>();
    //Linien Diagramm für den Kursverlauf
    LineChart<Number, Number> lineChartKurs = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Werte für Linien Diagramm Kursverlauf
    XYChart.Series<Number, Number> lineChartDatenKurs = new XYChart.Series<>();
    //Linien Diagramm für die Inflation
    LineChart<Number, Number> lineChartInflation = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Werte für Linien Diagramm Inflation
    XYChart.Series<Number, Number> lineChartDatenInflation = new XYChart.Series<>();
    //Linien Diagramm für den Leitzins von Dollar und Euro
    LineChart<Number, Number> lineChartLeitzins = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Werte für Linien Diagramm Leitzins von Dollar und Euro
    XYChart.Series<Number, Number> lineChartDatenLeitzinsEuro = new XYChart.Series<>();
    XYChart.Series<Number, Number> lineChartDatenLeitzinsDollar = new XYChart.Series<>();
    //Map für Balkendiagramm richtige Höhe und Farbe zu bestimmen
    EnumMap<Zustand, Rectangle> balkendiagramm = new EnumMap<Zustand, Rectangle>(Zustand.class);
    private double previousInflation = 0;
    double waehrungsWert = 0;

    @FXML
    public void initialize() {
        //resetInfo.setText("Klicke auf 'Reset' und 'Start' um \n die Charts angezeigt zu bekommen");
    }

    public void reset(){

        //wichtig, da sonst die Prozentzahl falsch ist
        Person.summePersonenTransaktionen=0;
        waehrungsWert = 0;
        Coins.nachfrageListe.clear();

        anteilTransaktionen.setText("0.0 %");
        anteilTeilnehmer.getChildren().clear();

        lineChartUmlauf.getData().clear();
        //Muss neue Serie, weil es sonst zu Fehlermeldung kommt, dass Serie mehrfach hinzugefügt wird
        lineChartDatenUmlauf = new XYChart.Series<>();
        lineChartUmlauf.getData().add(lineChartDatenUmlauf);
        coinsUmlauf.getChildren().clear();
        //damit Punkte im Chart nicht angezeigt werden
        lineChartUmlauf.setCreateSymbols(false);
        lineChartUmlauf.setMaxHeight(300);
        lineChartUmlauf.setMinWidth(900);
        //damit Überschrift nicht angezeigt wird
        lineChartUmlauf.setLegendVisible(false);
        coinsUmlauf.getChildren().add(lineChartUmlauf);

        lineChartKurs.getData().clear();
        lineChartDatenKurs = new XYChart.Series<>();
        lineChartKurs.getData().add(lineChartDatenKurs);
        coinsDurchschnitt.getChildren().clear();
        lineChartKurs.setCreateSymbols(false);
        lineChartKurs.setMaxHeight(300);
        lineChartKurs.setMinWidth(900);
        lineChartKurs.setLegendVisible(false);
        coinsDurchschnitt.getChildren().add(lineChartKurs);

        lineChartInflation.getData().clear();
        lineChartDatenInflation = new XYChart.Series<>();
        lineChartInflation.getData().add(lineChartDatenInflation);
        inflation.getChildren().clear();
        lineChartInflation.setCreateSymbols(false);
        lineChartInflation.setMaxHeight(300);
        lineChartInflation.setMinWidth(900);
        lineChartInflation.setLegendVisible(false);
        inflation.getChildren().add(lineChartInflation);

        lineChartLeitzins.getData().clear();
        lineChartDatenLeitzinsEuro = new XYChart.Series<>();
        lineChartDatenLeitzinsDollar = new XYChart.Series<>();
        lineChartLeitzins.getData().addAll(lineChartDatenLeitzinsEuro,lineChartDatenLeitzinsDollar);
        leitzins.getChildren().clear();
        lineChartLeitzins.setCreateSymbols(false);
        lineChartLeitzins.setMaxHeight(300);
        lineChartLeitzins.setMinWidth(900);
        lineChartLeitzins.setLegendVisible(false);
        leitzins.getChildren().add(lineChartLeitzins);

        //neue Vierecke für Balkendiagramm mit passender Farbe erzeugen
        int aufsummieren = 0;
        for(Zustand zustand : Zustand.values()){
            Rectangle balken = new Rectangle(60,0, zustand.getColor());
            //damit man neutralen Balken besser sieht
            if(balken.getFill()==Color.BEIGE){
                balken.setFill(Color.DARKGRAY);
            }
            //damit sie nebeneinander gemalt werden
            balken.setTranslateX(aufsummieren);
            aufsummieren +=65;
            balkendiagramm.put(zustand,balken);
            anteilTeilnehmer.getChildren().add(balken);
        }

    }

    public double berechneWaehrungswert(Zentralbank EZB){
        // Berechnet den Wert der Währung basierend auf der Inflation und der Nachfrage
        double inflationsRate = EZB.berechneInflation();
        double nachfrage = Person.summePersonenTransaktionen;
        //damit es nicht aufsummiert wird
        waehrungsWert = 0;

        System.out.println("Inflationsrate: " + inflationsRate);
        System.out.println("Nachfrage: " + nachfrage);
        if (inflationsRate > previousInflation) {
            // Wenn die Inflation steigt, verringert sich der Wert der Währung
            waehrungsWert -= inflationsRate;
        } else if (inflationsRate < previousInflation) {
            waehrungsWert += inflationsRate;
        }

        //Die Nachfrage (anhand von Transaktionen berechnet) addiert sich immer zum Währungswert
        waehrungsWert+= Coins.nachfrage();
        System.out.println("Nachfrage: " + Coins.nachfrage());

        System.out.println("vorherige Inflationsrate: " + previousInflation);
        System.out.println("aktueller Währungswert: " + waehrungsWert);
        previousInflation = inflationsRate;
        return waehrungsWert;
    }

    /**
     * Methode um die Diagramme parallel zur Simulation anzuzeigen
     *
     * @param simulation aktuelle Simulation zu der die Diagramme angezeigt werden sollen
     * @param Teilnehmeranzahl Anzahl der Punkte
     * @param clock Uhr, damit sich die Diagramme mit der Zeit mitbewegen
     */
    public void drawCharts(Simulation simulation, int Teilnehmeranzahl, Controller.SimulationsUhr clock, Zentralbank EZB) {

        //beim Runden mit 10000.0 multiplizieren, um Zahl auf 4 Nachkommastellen zu bringen und dann zu runden
        double gerundeteProzentzahl = Math.round(((Person.summePersonenTransaktionen/ Teilnehmeranzahl)*100 * 10000.0) / 10000.0);
        anteilTransaktionen.setText(gerundeteProzentzahl+" %");

        //Map mit Zustand der Punkte und der Anzahl dieser
        EnumMap<Zustand, Integer> aktuelleTeilnehmer = new EnumMap<Zustand, Integer>(Zustand.class);
        for(Person person:simulation.getPersonen()){
            if(!aktuelleTeilnehmer.containsKey(person.getZustand())){
                aktuelleTeilnehmer.put(person.getZustand(),0);
            }
            aktuelleTeilnehmer.put(person.getZustand(), 1+ aktuelleTeilnehmer.get(person.getZustand()));
        }

        for(Zustand zustand : balkendiagramm.keySet()){
            if(aktuelleTeilnehmer.containsKey(zustand)){
                //die Höhe des Balken soll immer die Höhe der passenden Teilnehmer haben
                balkendiagramm.get(zustand).setHeight(aktuelleTeilnehmer.get(zustand));
                //bei Null starten, wäre das Histogramm falsch rum, deshalb 100
                balkendiagramm.get(zustand).setTranslateY(30+100-aktuelleTeilnehmer.get(zustand));

                lineChartDatenUmlauf.getData().add(new XYChart.Data<>(clock.getTicks() / 5.0, Person.alleCoins.size()));
                //Farbe der Daten muss geändert werden, damit sich LineChart Linie ändert
                lineChartDatenUmlauf.getNode().setStyle("-fx-stroke: blue; ");
                //Kurs bestimmt sich durch die Transaktionen (also die Nachfrage), wobei sich eine Transaktion positiv oder negativ auswirken kann
                //deshalb Bestimmung einer Zufallszahl, die negativ, positiv oder null sein kann
                double randomAuswirkung = ((Math.random() * 0.2) - 0.1);
                lineChartDatenKurs.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, berechneWaehrungswert(EZB)));
                lineChartDatenKurs.getNode().setStyle("-fx-stroke: #bb1212;");

                lineChartDatenInflation.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, EZB.berechneInflation()));
                lineChartDatenInflation.getNode().setStyle("-fx-stroke: #288b36;");

                lineChartDatenLeitzinsEuro.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, EZB.getLeitzinsEuro()));
                lineChartDatenLeitzinsDollar.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, EZB.getLeitzinsDollar()));
                lineChartDatenLeitzinsEuro.getNode().setStyle("-fx-stroke: #7d298f;");
                lineChartDatenLeitzinsDollar.getNode().setStyle("-fx-stroke: #16bfbf;");
            }
        }

    }

}
