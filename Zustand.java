package com.example.ba_transaktionen.klassen;

import javafx.scene.paint.Color;

public enum Zustand {

    neutral{
        public Color getColor(){
            return Color.BEIGE;
        }
    },
    //Zustand der Transaktionsansicht
    transaktion {
        public Color getColor(){
            return Color.BLUE;
        }
    },
    //Zustände der Hackeransicht
    hacker{
        public Color getColor(){ return Color.BLACK;}
    },
    hackerangriff{
        public Color getColor(){ return Color.DARKRED;}
    },
    hackerabwehr{
        public Color getColor(){ return Color.DARKGREEN;}
    };

    public abstract Color getColor();
}
