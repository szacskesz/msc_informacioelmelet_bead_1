--------------------------------------
 Készített: Szacsuri Norbert (G0XXTC) 
--------------------------------------

A forráskód a "./Sources" mappában található
A lefordított program a "./Binaries/program.jar" néven érhető el.
Futtatásához Java 8 futtató környezet szükséges.

Futtatás:
	java -jar program.jar inputFilePath outputFilePath
	(inputFilePath és az outputFilePath helyére az adott fájlok elérési útvonalait kell megadni)


Futtatás példa:
	A "./Binaries" mappában egy CMD (linuxon terminál) ablakot nyitva az alábbi paranccsal:
	java -jar program.jar "./../Examples/in.1.txt" "./../Examples/out.1.txt"


Néhány példa bemenet és eredmény fájl található az "./Examples" mappában.
A bemeneti fájlok tartalmazzák a kívánt 2 dimenziós diszkrét eloszlás mátrixát (P_ij).
A fájl 1 sora a mátrix 1 sorát kell tartalmaznia.
Fölösleges üres sor nem lehet a fájlban.
A soron belüli cella értékeket szóközzel kell elválasztani.

Az eredmények ellenőrzéséhez felhasznált forrás: https://planetcalc.com/2476/