.PHONY : clean

all : Interfaz.class Gestor.class
	@echo "Iniciar programa"
	java -cp .:ojdbc10-full/ojdbc10.jar Interfaz

Interfaz.class : Interfaz.java
	@echo "Compilando Interfaz"
	javac Interfaz.java

Gestor.class : Gestor.java
	@echo "Compilando Gestor"
	javac Gestor.java

documentacion :
	doxygen Doxyfile

clean :
	rm -f *.class
