package com.example.ba_transaktionen;

import com.example.ba_transaktionen.klassen.Person;
import com.example.ba_transaktionen.klassen.Simulation;
import com.example.ba_transaktionen.klassen.Zustand;
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
    Label anteilTransaktionen;
    @FXML
    Label resetInfo;
    @FXML
    ScrollPane scrollPane;
    @FXML
    GridPane gridPane;
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
    //Map für Balkendiagramm richtige Höhe und Farbe zu bestimmen
    EnumMap<Zustand, Rectangle> balkendiagramm = new EnumMap<Zustand, Rectangle>(Zustand.class);

    @FXML
    public void initialize() {
        resetInfo.setText("Klicke auf 'Reset' und 'Start' um \n die Charts angezeigt zu bekommen");
    }

    public void reset(){

        //wichtig, da sonst die Prozentzahl falsch ist
        Person.summePersonenTransaktionen=0;

        anteilTransaktionen.setText("0.0 %");
        anteilTeilnehmer.getChildren().clear();

        lineChartUmlauf.getData().clear();
        //Muss neue Serie, weil es sonst zu Fehlermeldung kommt, dass Serie mehrfach hinzugefügt wird
        lineChartDatenUmlauf = new XYChart.Series<>();
        lineChartUmlauf.getData().add(lineChartDatenUmlauf);
        coinsUmlauf.getChildren().clear();
        //damit Punkte im Chart nicht angezeigt werden
        lineChartUmlauf.setCreateSymbols(false);
        lineChartUmlauf.setMaxHeight(250);
        //damit Überschrift nicht angezeigt wird
        lineChartUmlauf.setLegendVisible(false);
        coinsUmlauf.getChildren().add(lineChartUmlauf);

        lineChartKurs.getData().clear();
        lineChartDatenKurs = new XYChart.Series<>();
        lineChartKurs.getData().add(lineChartDatenKurs);
        coinsDurchschnitt.getChildren().clear();
        lineChartKurs.setCreateSymbols(false);
        lineChartKurs.setMaxHeight(250);
        lineChartKurs.setLegendVisible(false);
        coinsDurchschnitt.getChildren().add(lineChartKurs);

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

    /**
     * Methode um die Diagramme parallel zur Simulation anzuzeigen
     *
     * @param simulation aktuelle Simulation zu der die Diagramme angezeigt werden sollen
     * @param Teilnehmeranzahl Anzahl der Punkte
     * @param clock Uhr, damit sich die Diagramme mit der Zeit mitbewegen
     */
    public void drawCharts(Simulation simulation, int Teilnehmeranzahl, Controller.SimulationsUhr clock) {

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

                lineChartDatenUmlauf.getData().add(new XYChart.Data<>(clock.getTicks() / 5.0, Person.coinsUmlaufWert));
                //Farbe der Daten muss geändert werden, damit sich LineChart Linie ändert
                lineChartDatenUmlauf.getNode().setStyle("-fx-stroke: blue; ");
                //Kurs bestimmt sich durch die Transaktionen (also die Nachfrage), wobei sich eine Transaktion positiv oder negativ auswirken kann
                //deshalb Bestimmung einer Zufallszahl, die negativ, positiv oder null sein kann
                double randomAuswirkung = ((Math.random() * 0.2) - 0.1);
                lineChartDatenKurs.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, Person.summePersonenTransaktionen *  randomAuswirkung));
                lineChartDatenKurs.getNode().setStyle("-fx-stroke: #bb1212;");
            }
        }

    }

}
