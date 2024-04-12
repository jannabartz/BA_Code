package com.example.ba_transaktionen.klassen;

import javafx.scene.layout.Pane;

public class Position {

    //x und y Wert des Punktes auf der Pane
    private double x;
    private double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Position(Pane welt){
        //Zufalsspunkt auf der Anzeigefläsche erzeugen
        // -2*radius, auf jeder Seite etwas Abstand
        // + radius, damit es nicht bei Null startet
        this(Person.radius + Math.random()*(welt.getWidth()-2*Person.radius),
                Person.radius + Math.random()*(welt.getHeight()-2*Person.radius));
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    /**
     * Euclidean distance wird berechnet, d.h. der Abstand zwischen zwei Punkten
     *
     * @param andere Punkt zu dem Abstand bestimmt werden soll
     * @return Abstand zum Punkt andere
     */
    public double punktabstand(Position andere){
        return Math.sqrt(Math.pow(this.x - andere.x, 2) + Math.pow(this.y - andere.y,2));
    }


    /**
     * Methode, durch die bei jedem step die Punkte sich bewegen
     *
     * @param punkt Punkt der bewegt werden soll
     * @param welt Pane auf der sich der Punkt bewegen soll
     * @param ursprung Ursprungsposition von dem sich der Punkt weg bewegen soll
     */
    public void bewegen(Punkt punkt, Pane welt, Position ursprung){
        x += punkt.getDx();
        y += punkt.getDy();
        //wenn der x-Wert die rechte oder linke Kante berührt, soll verändert werden
        //radius, damit der Rand vom Kreis auch nicht ausserhalb der welt ist und nicht nur der Mittelpunkt
        //bewegungsradius, damit die Punkte sich nur im Radius von dem Regler bestimmten Wert befinden können
        if ( punktabstand(ursprung)>Person.bewegungsradius || x<Person.radius || x>welt.getWidth()-Person.radius){
            punkt.bewegeX();
            x += punkt.getDx();
        }
        // wenn y die obere oder untere Kante berührt, soll weg bewegen
        if ( punktabstand(ursprung)>Person.bewegungsradius || y<Person.radius || y>welt.getHeight()-Person.radius){
            punkt.bewegeY();
            y += punkt.getDy();
        }
    }

    /**
     * Methode zum Prüfen, ob ein Punkt mit einem anderen kollidiert
     *
     * @param andere anderer Punkt zum prüfen
     * @return wenn der Punkt sich im Umfang von 2* dem Radius des anderen befindet, wird true zurückgegeben
     */
    public boolean kollidieren(Position andere){
        return punktabstand(andere) < 2*Person.radius;
    }

}

