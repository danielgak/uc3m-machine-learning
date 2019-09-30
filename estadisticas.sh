#! /bin/bash
trap ctrl_c INT

TOTAL_WIN=0
TOTAL_LOSS=0
TOTAL_COLLISIONS=0
TOTAL_COINS=0
TOTAL_COINS_PERC=0
TOTAL_KILLS=0
TOTAL_KILLS_PERC=0
TOTAL_PASSED=0
TOTAL_TIME_OUT=0
i=0
function ctrl_c {
	echo -e "\n#### Se ha pulsado Ctrl + C. Recogida de estadísticas interrumpida. Se muestra los resultados parciales obtenidos ####"
	echo -e "\tTotal de niveles ejecutados: $i"
	resumen
	exit -1
}

function resumen {
	echo "Resumen:"
	echo -e "\tTotal niveles superados: $TOTAL_WIN (`python3 -c "print (round(($TOTAL_WIN / $i) * 100,2))"`%)"
	echo -e "\tTotal niveles NO superados: $TOTAL_LOSS (`python3 -c "print (round(($TOTAL_LOSS / $i) * 100,2))"`%)"
	echo -e "\tPorcentaje medio de nivel completado: `python3 -c "print (round($TOTAL_PASSED / $i,2))"`%"
	echo -e "\tMedia de colisiones con enemigo por nivel: `python3 -c "print (round($TOTAL_COLLISIONS / $i,2))"`"
	echo -e "\tMedia de muertes de enemigos por nivel: `python3 -c "print (round($TOTAL_KILLS / $i,2))"`"
	echo -e "\tMedia de muertes de enemigos por nivel (porcentaje): `python3 -c "print (round($TOTAL_KILLS_PERC / ($i + 1),2))"`%"
	echo -e "\tMedia de monedas recogidas por nivel: `python3 -c "print (round($TOTAL_COINS / $i,2))"`"
	echo -e "\tMedia de monedas recogidas por nivel (porcentaje): `python3 -c "print (round($TOTAL_COINS_PERC / $i,2))"`%"
	echo -e "\tNúmero de niveles no completados por time out: $TOTAL_TIME_OUT"
	echo -e "\tPorcentaje de niveles no completados por time out: `python3 -c "print (round(($TOTAL_TIME_OUT / $i) * 100,2))"`%"
}

if [ $# -ne 2 ]; then
	echo "Este script debe ser llamado con dos argumentos: ./estadisticas.sh <agent> <num_niveles>"
	exit -1
fi
ITERS=$(($2 - 1))
echo "#### Compilando... ####"
./compilar.sh
echo -e "\n#### Comenzando la ejecución de niveles para el agente $1. Se ejecutarán del nivel 0 al $ITERS ####"
for i in `seq 0 $ITERS`; do
	echo -e "\n\tEjecutando nivel con seed: $i"
	./ejecutar.sh $1 -ls $i -vis off > /tmp/estadisticas_marioAI.txt
	# Comprobación de errores al equivocarte al escribir nombre agente
	if [ "$i" -eq "0" ]; then
		ERROR=`cat /tmp/estadisticas_marioAI.txt | grep HumanKeyboardAgent`
		if [ ! -z "$ERROR"]; then
			printf "\033[0;31mERROR: the default HumanKeyboardAgent was loaded. Did you write correctly the agent name?\n\033[0m"
			exit -1
		fi
	fi
	MARIO_STATUS=`cat /tmp/estadisticas_marioAI.txt | grep "Status" | awk '{print $4}'`
	MARIO_MODE=`cat /tmp/estadisticas_marioAI.txt | grep "Mode" | awk '{print $4}'`
	COLLISIONS=`cat /tmp/estadisticas_marioAI.txt | grep "Collisions" | awk '{print $5}'`
	TOTAL_COLLISIONS=$(($TOTAL_COLLISIONS + $COLLISIONS))
	PASSED=`cat /tmp/estadisticas_marioAI.txt | grep "Passed" | awk '{print $11}'`
	PASSED=${PASSED#"("}
	PASSED_AUX=${PASSED%"%"}
	TOTAL_PASSED=$(($TOTAL_PASSED + $PASSED_AUX))
	TIME=`cat /tmp/estadisticas_marioAI.txt | grep "Spent" | awk '{print $4}'`
	COINS_NUM=`cat /tmp/estadisticas_marioAI.txt | grep "Coins" | awk '{print $4}'`
	TOTAL_COINS=$(($TOTAL_COINS + $COINS_NUM))
	COINS=`cat /tmp/estadisticas_marioAI.txt | grep "Coins" | awk '{print $7}'`
	COINS=${COINS#"("}
	COINS_AUX=${COINS%"%"}
	TOTAL_COINS_PERC=$(($TOTAL_COINS_PERC + $COINS_AUX))
	KILLS_NUM=`cat /tmp/estadisticas_marioAI.txt | grep "kills Total" | awk '{print $4}'`
	TOTAL_KILLS=$(($TOTAL_KILLS + $KILLS_NUM))
	KILLS=`cat /tmp/estadisticas_marioAI.txt | grep "kills Total" | awk '{print $8}'`
	KILLS=${KILLS#"("}
	KILLS=${KILLS%")"}
	KILLS_AUX=${KILLS%"%"}
	TOTAL_KILLS_PERC=$(($TOTAL_KILLS_PERC + $KILLS_AUX))
	if [ $MARIO_STATUS == "WIN!" ] ; then
		echo -e "\t\tWIN! Mario se ha chocado con $COLLISIONS enemigos y ha matado a $KILLS enemigos."
		TOTAL_WIN=$(($TOTAL_WIN + 1))
	else
		echo -e "\t\t$MARIO_STATUS Mario ha completado el $PASSED del nivel en $TIME segundos de juego."
		echo -e "\t\t`cat /tmp/estadisticas_marioAI.txt | grep "MEMO INFO:"`"
		TIME_OUT=`cat /tmp/estadisticas_marioAI.txt | grep "Time out!"`
		if [ ! -z "$TIME_OUT" ]; then
			TOTAL_TIME_OUT=$(($TOTAL_TIME_OUT + 1))
		fi
		TOTAL_LOSS=$(($TOTAL_LOSS + 1))
	fi
done
echo -e "\n#### Todos los niveles ejecutados. Recogida de estadísticas completada ####"
echo -e "\tTotal de niveles ejecutados: $2"
resumen
