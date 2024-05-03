package com.example.ba_transaktionen;

import com.example.ba_transaktionen.klassen.Coins;
import com.example.ba_transaktionen.klassen.Zentralbank;
import com.example.ba_transaktionen.klassen.Person;
import com.example.ba_transaktionen.klassen.Simulation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    Button resetB;
    @FXML
    Button startB;
    @FXML
    Button stopB;
    @FXML
    Button stepB;
    @FXML
    Button chartB;
    @FXML
    Button hackerB;
    @FXML
    Slider anzahlSlider;
    @FXML
    Slider transaktionSlider;
    @FXML
    Slider abstandSlider;
    @FXML
    Slider coinSlider;
    @FXML
    Slider euroSlider;
    @FXML
    Slider dollarSlider;
    @FXML
    Label transwkeitLabel;
    @FXML
    TextField tickText;
    @FXML
    Pane world;

    Simulation simulation;
    Zentralbank EZB;
    //Anzahl der Punkte bzw. Teilnehmer
    int Teilnehmeranzahl = 50;
    //Wahrscheinlichkeit, dass eine Transaktion stattfindet oder ein Hacker erfolgreich ist
    double WkeitTransHackErfolg = 0.5;
    //Prüfung ob Hackeransicht aktiv ist
    boolean hackerSzeneAktiv = false;
    //Uhr, die für die für die Zeit in der Simulation notwendig ist
    private SimulationsUhr uhr;
    //Controller für das Diagrammfenster der Transaktionen
    ControllerCharts chartFenster;
    //Controller für das Diagrammfenster bei der Hackeransicht
    ControllerHackerCharts hackerFenster;
    //stage für das Chartfenster der Transaktionen
    Stage stageCharts;
    //stage für das Chartfenster des Hackers
    Stage stageHackerCharts;

    public class SimulationsUhr extends AnimationTimer{

        //Konstante zur Bestimmung der Bewegungsgeschwindigkeit
        private long BILDER_PRO_SEKUNDE = 50L;
        //eine Null mehr als beim Hacker, damit sich die Punkte langsamer bewegen, da die Transaktionen sonst zu unübersichtlich sind
        private long INTERVAL = 10000000000L/ BILDER_PRO_SEKUNDE;
        //eine Null weniger, damit es sich schneller bewegt, weil sonst zu langweilig
        private long INTERVAL_HACKER = 1000000000L/ BILDER_PRO_SEKUNDE;
        private long vergangen = 0;
        private int ticks = 0;

        @Override
        //nach einer kurzen Zeit werden die Punkte immer wieder bewegt, weil step ausgeführt wird
        public void handle(long jetzt) {
            if(hackerSzeneAktiv){
                if(jetzt- vergangen > INTERVAL_HACKER){
                    step();
                    vergangen = jetzt;
                    ticks++;
                }
            }
            else {
                if (jetzt - vergangen > INTERVAL) {
                    step();
                    vergangen = jetzt;
                    ticks++;
                }
            }
        }
        public int getTicks(){
            return ticks;
        }
        public void resetTicks(){
            ticks = 0;
        }
        public void tick(){
            ticks++;
        }
    }

    //erste Methode die Aufgerufen wird, wenn sich das Fenster öffnet
    @FXML
    public void initialize(){
        anzahlSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setAnzahl();
            }
        });
        transaktionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setWkeitTransHack();
            }
        });
        abstandSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setBewegungsradius();
            }
        });
        coinSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setCoins();
            }
        });
        euroSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setEuroLeitzins();
            }
        });
        dollarSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setDollarLeitzins();
            }
        });
        uhr = new SimulationsUhr();
        world.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
    }

    //Methode, die aufgerufen wird, beim Drücken des Reset-Buttons
    @FXML
    public void reset(){
        //Simulation soll stoppen
        stop();
        uhr.resetTicks();
        tickText.setText(""+ uhr.getTicks());
        //Punkte sollen nicht mehr angezeigt werden
        world.getChildren().clear();
        //damit Zähler wieder von 0 beginnt
        Person.transaktionsPaar = 0;
        //Slidereinstellungen werden übernommen
        setAnzahl();
        setWkeitTransHack();
        setBewegungsradius();
        setCoins();
        setEuroLeitzins();
        setDollarLeitzins();
        simulation = new Simulation(world, Teilnehmeranzahl, hackerSzeneAktiv);
        EZB = new Zentralbank();
        if(chartFenster!=null) {
            //wichtige Abfrage, da ansonsten chartfenster (vor drücken des button) noch nicht initialisiert wurde und es eine NullPointerException gibt
            chartFenster.reset();
        }
        //wenn chartfenster geschlossen wird, soll button wieder anklickbar sein
        //muss zuerst geprüft werden ob !=null, da sonst NullPointerException
        if(stageCharts!=null&&!stageCharts.isShowing()) {
            chartB.setDisable(false);
        }
        if(hackerFenster!=null) {
            hackerFenster.reset();
        }
        if(stageHackerCharts!=null&&!stageHackerCharts.isShowing()) {
            chartB.setDisable(false);
        }

    }

    //die Anzahl an Teilnehmern bzw. Punkten wird bestimmt
    public void setAnzahl(){
        Teilnehmeranzahl = (int) anzahlSlider.getValue();
    }

    //die Wahrscheinlichkeit, dass eine Transaktion bzw. ein Hackerangriff bei Kollision erfolgreich ist
    public void setWkeitTransHack(){
        WkeitTransHackErfolg = transaktionSlider.getValue();
    }

    //Radius in dem sich die Punkte bewegen sollen
    public void setBewegungsradius(){
        Person.bewegungsradius = (int) abstandSlider.getValue();
    }

    public void setCoins() { Coins.anzahlCoins = (int) coinSlider.getValue(); }

    public void setEuroLeitzins(){ EZB.leitzinsEuro = euroSlider.getValue(); }

    public void setDollarLeitzins(){ EZB.leitzinsDollar = dollarSlider.getValue(); }

    @FXML
    public void start(){
        uhr.start();
        disableButton(true,false,true);
    }

    @FXML
    public void stop(){
        uhr.stop();
        disableButton(false,true,false);
    }

    @FXML
    public void step() {

        //überlegen wie vorher und Task wegmachen
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                simulation.bewegen();
                simulation.prüfeKollision(WkeitTransHackErfolg, hackerSzeneAktiv);
                simulation.transaktionAbschließen();
                EZB.zinsDevelopmentEuro();
                EZB.zinsDevelopmentDollar();
                uhr.tick();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                simulation.malen();
                tickText.setText("" + uhr.getTicks());
                if (chartFenster != null) {
                    chartFenster.drawCharts(simulation, Teilnehmeranzahl, uhr, EZB);
                }
                if (hackerFenster != null) {
                    hackerFenster.drawCharts(simulation, Teilnehmeranzahl, uhr);
                }
            });
        });

        new Thread(task).start();
    }

    /**
     * Methode zur Änderung der Transaktionsansicht in die Hackeransicht
     * ein Punkt ist schwarz (Hacker), wenn dieser Punkt berührt wird, dann wird der berührte
     * Punkt rot (Geld geht an Hacker) oder bleibt neutral
     * wenn roter Punkt berührt wird, dann wird der berührte Punkte grün (roter Punkt warnt und
     * anderer schützt sich besser vor Angriffen) oder bleibt neutral
     * grüne Punkte sind geschützt und können nicht mehr angegriffen werden
     */
    @FXML
    public void hackerOn(){
        if(!hackerSzeneAktiv) {
            if(stageCharts!=null) {
                //Chartfenster der Transaktionen soll sich automatisch schließen
                stageCharts.close();
            }
            //Button Text muss sich sinngemäß ändern
            hackerB.setText("Off");
            //damit man sich wieder die Charts anzeigen lassen kann
            chartB.setDisable(false);
            transwkeitLabel.setText("Hackererfolgwahrscheinlichkeit");
            hackerSzeneAktiv = true;
            //reset, damit Änderungen übernommen werden
            reset();
        }
        else{
            if(stageHackerCharts!=null) {
                stageHackerCharts.close();
            }
            hackerB.setText("On");
            chartB.setDisable(false);
            transwkeitLabel.setText("Transaktionswahrscheinlichkeit");
            hackerSzeneAktiv = false;
            reset();
        }
    }

    //neues Fenster mit Charts soll sich seperat zur Simulation öffnen, um die Übersichtlichkeit zu verbessern
    @FXML
    void chartsAnzeigen(ActionEvent event){
        //man muss erst reset drücken, damit charts angezeigt werden
        if(!hackerSzeneAktiv) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("ChartGui.fxml"));
                Scene sceneCharts = new Scene(fxmlLoader.load());
                //bei Öffnen soll ControllerCharts initialisiert werden
                chartFenster = fxmlLoader.getController();
                chartB.setDisable(true);
                stageCharts = new Stage();
                stageCharts.setTitle("Diagramme zu der Simulation");
                stageCharts.setScene(sceneCharts);
                stageCharts.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("HackerChartGui.fxml"));
                Scene sceneHackerCharts = new Scene(fxmlLoader.load());
                //bei Öffnen soll ControllerHackerCharts initialisiert werden
                hackerFenster = fxmlLoader.getController();
                chartB.setDisable(true);
                stageHackerCharts = new Stage();
                stageHackerCharts.setTitle("Diagramme zum Hackerangriff");
                stageHackerCharts.setScene(sceneHackerCharts);
                stageHackerCharts.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disableButton(boolean start, boolean stop, boolean step){
        startB.setDisable(start);
        stopB.setDisable(stop);
        stepB.setDisable(step);
    }


}