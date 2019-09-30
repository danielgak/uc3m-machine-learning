#! /bin/bash
if [ $# -ne 3 ]; then
	echo "Este script debe ser llamado con tres argumentos: ./recolectar_ejemplos.sh <agent> <freq_toma_ejemplos> <max_seed>"
	exit -1
fi
echo "#### Compilando el código ####"
./compilar.sh
echo "#### Borrando ejemplos de entrenamiento anteriores ($1.arff) ####"
rm -f weka/ejemplos_entrenamiento/$1_$2.arff
echo "#### Comienzo de la recolección de instancias de entrenamiento del agente $1 ####"
for i in `seq 0 $3`; do
	echo -e "\tRecolectando ejemplos del nivel con seed: $i"
	./ejecutar.sh $1 -ls $i -vis off > /dev/null
	head -n -1 weka/ejemplos_entrenamiento/$1_$2.arff > /tmp/$1_$2.arff.tmp ; mv /tmp/$1_$2.arff.tmp weka/ejemplos_entrenamiento/$1_$2.arff
done
echo "#### Fin de la recolección de instancias de entrenamiento ####"
echo "Comprueba el fichero $1.arff"