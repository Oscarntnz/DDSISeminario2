.PHONY : clean run

all : OracleCon.class run

OracleCon.class : OracleCon.java
	javac OracleCon.java

run : OracleCon.class
	java -cp .:ojdbc10-full/ojdbc10.jar OracleCon

clean :
	rm *.class