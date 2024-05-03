package com.example.ba_transaktionen.klassen;

import java.util.HashSet;
import java.util.Set;

public class Coins {

    //jeder Teilnehmer hat am Anfang so viele Coins, kann auch von 0-50 gehen
    public static int anzahlCoins;

    public boolean ausgegeben;
    public Person besitzer;
    // Liste, die alle Nachfrager nach Coins speichert
    public static Set<Person> nachfrageListe = new HashSet<>();

    public static double nachfrage(){
        // Zählen Sie die Anzahl der eindeutigen Besitzer
        int anzahlBesitzer = nachfrageListe.size();

        // Berechnet die Nachfrage basierend auf der Anzahl der Besitzer und der Anzahl der Coins
        // wenn es also mehr Nachfrager gibt, als die Coins die eine Person hat, dann steigt die Nachfrage
        if (anzahlBesitzer > anzahlCoins) {
            return (anzahlBesitzer - anzahlCoins) / (double) anzahlCoins;
        } else {
            return 0;
        }
    }



}
