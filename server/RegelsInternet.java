package qwirkle;

public class RegelsInternet {

	public RegelsInternet() {
		
	}

}

/*

Bericht:
-zet ruilen/plaatsen
-spelernaam
-resultaat (errorcode)
-winnaar
-wie aan de beurt is
-beschikbare stenen
-stenen uit de zak gepakt (bepaalt server)
-aantal punten
-aantal spelers (2,3,4)


protocol coordinaten: 0,0 begin, daarna naar boven en naar links wordt - en rechts en onder wordt plus

0 tot 5 kleur
0 tot 5 vorm
tupeltjes type, dus 35 = kleur 3 en vorm 5


sturen van data via een lange string

C -> S trade	: steen locatie(x,y) (steen locatie enz) spatie ertussen
C -> S place	: score/-1 (-1 = foute move)
S -> C makemove	: 


alle stenen in een keer verstuurd




*/
