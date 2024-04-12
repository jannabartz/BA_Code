package com.example.ba_transaktionen;

import com.example.ba_transaktionen.klassen.Person;
import com.example.ba_transaktionen.klassen.Simulation;
import com.example.ba_transaktionen.klassen.Zustand;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;

public class ControllerHackerCharts {

    @FXML
    Pane coinsHackerPane;
    @FXML
    Label teilnehmerLabel;
    @FXML
    Pane teilnehmerPane;
    @FXML
    Label resetInfo;

    //Linien Diagramm für Coins des Hackers
    LineChart<Number, Number> lineChartHacker = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Werte für Linien Diagramm Coins des Hackers
    XYChart.Series<Number, Number> lineChartDatenHacker = new XYChart.Series<>();
    //Linien Diagramm für Teilnehmerzustand
    LineChart<Number, Number> lineChartTeilnehmer = new LineChart<>(new NumberAxis(), new NumberAxis());
    //Teilnehmeranzahl gehackt für Linien Diagramm Teilnehmerzustand
    XYChart.Series<Number, Number> lineChartDatenTeilnehmerRot = new XYChart.Series<>();
    //Teilnehmeranzahl geschützt für Linien Diagramm Teilnehmerzustand
    XYChart.Series<Number, Number> lineChartDatenTeilnehmerGrün = new XYChart.Series<>();
    //Teilnehmeranzahl neutral für Linien Diagramm Teilnehmerzustand
    XYChart.Series<Number, Number> lineChartDatenTeilnehmerGrau = new XYChart.Series<>();
    EnumMap<Zustand, Rectangle> balkendiagramm = new EnumMap<Zustand, Rectangle>(Zustand.class);

    @FXML
    public void initialize() {
        teilnehmerLabel.setText("Auswirkungen auf die Teilnehmer \n rot = gehackt, grün = geschützt, grau = neutral");
        resetInfo.setText("Klicke auf 'Reset' und 'Start' um \n die Charts angezeigt zu bekommen");
    }

    public void reset(){

        Person.teilnehmerGehackt = 0;
        Person.teilnehmerAbwehr = 0;

        lineChartHacker.getData().clear();
        lineChartDatenHacker = new XYChart.Series<>();
        lineChartHacker.getData().add(lineChartDatenHacker);
        coinsHackerPane.getChildren().clear();
        lineChartHacker.setCreateSymbols(false);
        lineChartHacker.setMaxHeight(300);
        lineChartHacker.setLegendVisible(false);
        coinsHackerPane.getChildren().add(lineChartHacker);

        lineChartTeilnehmer.getData().clear();
        lineChartDatenTeilnehmerRot = new XYChart.Series<>();
        lineChartDatenTeilnehmerGrün = new XYChart.Series<>();
        lineChartDatenTeilnehmerGrau = new XYChart.Series<>();
        lineChartTeilnehmer.getData().addAll(lineChartDatenTeilnehmerRot, lineChartDatenTeilnehmerGrün, lineChartDatenTeilnehmerGrau);
        teilnehmerPane.getChildren().clear();
        lineChartTeilnehmer.setCreateSymbols(false);
        lineChartTeilnehmer.setMaxHeight(300);
        lineChartTeilnehmer.setLegendVisible(false);
        teilnehmerPane.getChildren().add(lineChartTeilnehmer);

    }

    /**
     * Zeichnet die Linien Diagramme für die Coins des Hackers und die Auswirkungen auf die Teilnehmer
     * @param simulation aktuelle Simulation zu der die Diagramme angezeigt werden sollen
     * @param Teilnehmeranzahl Anzahl der Punkte
     * @param clock Uhr, damit sich die Diagramme mit der Zeit mitbewegen
     */
    public void drawCharts(Simulation simulation, int Teilnehmeranzahl, Controller.SimulationsUhr clock) {

        EnumMap<Zustand, Integer> aktuelleTeilnehmer = new EnumMap<Zustand, Integer>(Zustand.class);
        for(Person person:simulation.getPersonen()){
            if(!aktuelleTeilnehmer.containsKey(person.getZustand())){
                aktuelleTeilnehmer.put(person.getZustand(),0);
            }
            aktuelleTeilnehmer.put(person.getZustand(), 1+ aktuelleTeilnehmer.get(person.getZustand()));
        }

        for(Zustand zustand: aktuelleTeilnehmer.keySet()){

            lineChartDatenHacker.getData().add(new XYChart.Data<>(clock.getTicks() / 5.0, Person.coinsHacker));
            lineChartDatenHacker.getNode().setStyle("-fx-stroke: blue; ");

            lineChartDatenTeilnehmerGrün.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, Person.teilnehmerAbwehr));
            lineChartDatenTeilnehmerRot.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, Person.teilnehmerGehackt));
            lineChartDatenTeilnehmerGrau.getData().add(new XYChart.Data<>(clock.getTicks()/5.0, Teilnehmeranzahl-Person.teilnehmerAbwehr-Person.teilnehmerGehackt));
            lineChartDatenTeilnehmerRot.getNode().setStyle("-fx-stroke: darkred;");
            lineChartDatenTeilnehmerGrün.getNode().setStyle("-fx-stroke: darkgreen;");
            lineChartDatenTeilnehmerGrau.getNode().setStyle("-fx-stroke: darkgrey");
        }


    }
}
