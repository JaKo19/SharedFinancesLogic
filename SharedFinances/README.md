## *Mit Maven arbeiten (Eclipse)*
1. Maven clean (Rechtsklick auf Projekt >> Run As >> 4 Maven clean)
	1. Räumt alle Dateien ordentlich auf, am besten alles vor jeder Maven Operation cleanen
	
2. Maven jar erstellen
	1. Maven clean
	2. Maven clean package (Rechtsklick auf Projekt >> Run As >> 3 Maven build... >> Goals: clean package)
	3. jar Datei wird in target gespeichert (auch andere Dateien die unwichtig sind)
	
3. "Fehler: Hauptklasse main.java.com.sharedfinances.main.Main konnte nicht gefunden oder geladen werden" beheben!
	1. bevor eine Hauptklasse ausgeführt werden kann muss das Projekt "gebuildet" werden
	(Rechtsklick auf Projekt >> Run As >> 2 Maven build)
