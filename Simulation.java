package com.example.ba_transaktionen.klassen;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Simulation {

    private ArrayList<Person> personenListe;

    //alles muss der welt hinzugefügt werden
    public Simulation(Pane welt, int personenanzahl, boolean hacker){
        personenListe = new ArrayList<Person>();
        //alle Teilnehmer sind am Anfang neutral
        for(int i=0; i<personenanzahl; i++){
            personenListe.add(new Person(Zustand.neutral, welt));
        }
        //bei der Hackeransicht, muss es einen Nichtneutralen geben, den Hacker
        if(hacker){
            personenListe.add(new Person(Zustand.hacker,welt));
        }
        //malen wichtig, sonst sammeln sich alle Punkte an einer Stelle und werden nicht angezeigt
        malen();
    }

    public ArrayList<Person> getPersonen(){
        return personenListe;
    }

    public void bewegen(){
        for(Person p:personenListe){
            p.bewegen();
        }
    }

    public void malen(){
        for(Person p:personenListe){
            p.punktAnzeigen();
        }
    }

    /**
     * Methode zum Prüfen ob in der Personenliste eine Kollision stattfindet
     *
     * @param WkeitTransHackErfolg Wahrscheinlichkeit, mit der Transaktion (nach erfolgreicher Kollision) stattfindet
     *                         oder Hacker erfolgreich einen Teilnehmer hackt
     * @param hacker Variable zum Prüfen, ob die Hackeransicht aktiv ist
     */
    public void prüfeKollision(double WkeitTransHackErfolg, boolean hacker){
        for(Person person1:personenListe){
            for (Person person2:personenListe){
                //um zu verhindern, dass man mit sich selbst kollidiert
                if(person1!=person2){
                    //bei Hacker sollen keine Transaktionen ausgeführt werden, aber ein Kollisionscheck mit dem Hacker oder einem Infizierten
                    if(hacker){
                        //in dem Fall ist WkeitTransHackErfolg die Wahrscheinlichkeit, dass der Hacker erfolgreich ist
                        person1.hackerCheck(person2, WkeitTransHackErfolg);
                    }
                    else {
                        //hier ist die WkeitTransHackErfolg die Wahrscheinlichkeit, dass eine Transaktion stattfindet
                        person1.kollisionCheck(person2, WkeitTransHackErfolg);
                        malen();
                    }
                }
            }
        }
    }


    public void transaktionAbschließen(){
        for(Person person1:personenListe){
            for (Person person2:personenListe){
                //um zu verhindern, dass man mit sich selbst abschließt
                if(person1!=person2){
                    person1.transaktionAbschließen(person2);
                }
            }
        }
    }

}
