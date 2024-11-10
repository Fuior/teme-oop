Am creat pachetul fileoutput in care am facut clase pentru implementarea functionalitatilor din cerinta temei.

Clasele mele sunt:

	Player: Are campuri sfecifice unui jucator. Unele preiau informatii din clasele de input, 
		altele reprezinta atribute care se schimba sau trebuie sa fie contorizate pe parcursul unui joc.
		
	CardProperties: Ia atributele unei carti din clasa "CardInput" si are campuri care sa ii contorizeze starea din timpul unui joc.
	
	GameBoard: Are un camp in care se memoreaza cartile plasate pe tabla de joc. 
		Contine metode ce se ocupa de actiunile ce pot fi facute de cartile de pe tabla si de erou.
		
	GameState: Gestioneaza jucatorul activ, runda la care a ajuns jocul si verifica daca jocul s-a terminat.
	
	Output: Creeaza nodurile pentru fisierele json.
	
	GameActions: Apeleaza metodele din clasa "Output".
	
	PlayGame: Are drept campuri 2 jucatori, tabla de joc, inputul unui test si actiunile ce pot fi facute intr-un joc. 
		Metoda "doAction" verifica ce comanda are la input si, in functie de caz, apeleaza o functie din clasa "GameBoard" 
		sau din clasa "GameActions". Metoda "doActions" itereaza prin toate comenzile unui joc si apeleaza pentru fiecare metoda "doAction".
		Metoda "play" instantiaza campurile necesare pentru un joc, itereaza prin jocurile de la input si 
		executa comenzile fiecarui joc prin metoda "doActions".
		
Am folosit in implementare:
	-Agregarea
	-Inlantuirea constructorilor
	-Expresii lambda in switch-ul din clasa "PlayGame"
