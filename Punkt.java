package com.example.ba_transaktionen.klassen;

public class Punkt {

    private double dx;
    private double dy;
    //Geschwindigkeit mit der sich die Punkte bewegen
    public static final double SPEED = 3;

    public Punkt(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }

    public Punkt(){
        //bei Java nicht 360 Grad, sondern Radien von Null bis 2 Pi
        //Richtung in Radien als Winkel und zufällige Bewegung
        double dir = Math.random() *2 * Math.PI;
        //x- und y-Werte werden mit Sinus und Cosinus-Funktion bestimmt
        dx = Math.sin(dir);
        dy = Math.cos(dir);
    }

    public double getDx(){
        return dx*SPEED;
    }

    public double getDy(){
        return dy*SPEED;
    }

    public void bewegeX(){
        dx*=-1;
    }

    public void bewegeY(){
        dy*=-1;
    }
}
