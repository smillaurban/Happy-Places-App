# Happy Places App

Mit der **Happy Places App** kannst du besondere Orte auf einer Karte markieren, beschreiben und mit einem Bild versehen. Die App speichert deine Orte dauerhaft, sodass sie auch nach dem Schließen erhalten bleiben.

## Funktionen

- **Karte anzeigen:** Interaktive Karte mit Zoom und Standortanzeige.
- **Ort hinzufügen:** Markiere einen Punkt auf der Karte, gib einen Titel und eine Beschreibung ein und füge optional ein Bild hinzu.
- **Aktuellen Standort verwenden:** Setze einen Marker auf deinen aktuellen Standort (nach Berechtigungsabfrage).
- **Orte speichern:** Alle Orte werden dauerhaft gespeichert (Room-Datenbank).
- **Orte anzeigen:** Deine gespeicherten Orte werden beim Start automatisch geladen.
- **Navigation:** Einfaches Zurücknavigieren zum Startscreen.

## Berechtigungen

- **Standortzugriff:** Für die Funktion "Aktuellen Standort verwenden" wird die Standortberechtigung benötigt.

## Tech-Stack

- Kotlin, Jetpack Compose
- Room (persistente Speicherung)
- osmdroid (Kartenanzeige)

## Installation

1. Repository klonen  
   `git clone https://github.com/smillaurban/Happy-Places-App.git`
2. In Android Studio öffnen und auf ein Gerät/Emulator installieren.

## Hinweise

- Die App benötigt mindestens Android 6.0 (API 23).
- Standortberechtigung muss beim ersten Start erteilt werden.

---

Viel Spaß beim Entdecken deiner Happy Places!
