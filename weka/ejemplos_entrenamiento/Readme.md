# Ejemplos de entrenamiento Mario AI
En este directorio se almacenan los ficheros .arff con los ejemplos de entrenamiento tomados en las ejecuciones de nuestros agentes.

Estos ficheros se nombran como \<nombreAgente\>_\<frecuenciaCapturaEjemplos\>.arff y contienen en cada línea la siguiente información, es este orden:

## Atributos para ayudar a localizar o identificar la instancia de entrenamiento
- [0] Tiempo gastado (int)
- [1] Tiempo restante (int)
## Atributos relacionados con la intermediateReward
- [2] intermediateReward (int)
- [3] intermediateReward ganada en el último tick (int)
- [4] intermediaReward en los últimos 6 ticks (0.25s) (int)
- [5] intermediaReward obtenido en el tick n+6 (0.25s) (int)
- [6] intermediaReward obtenido en el tick n+12 (0.5s) (int)
- [7] intermediaReward obtenido en el tick n+24 (1s) (int)
## Atributos relacionados con la matriz de observación mergeObs
- [8] Distancia euclídea al enemigo más cercano por la izquierda (x in [0-9]) (double)
- [9] Número de columna (coord x) matriz de observación enemigo más cercano por la izquierda (x in [0-9]) (byte)
- [10] Número de fila (coord y) matriz de observación enemigo más cercano por la izquierda (x in [0-9]) (byte)
- [11] Distancia euclídea al enemigo más cercano por la derecha (x in [10-18]) (double)
- [12] Número de columna (coord x) matriz de observación enemigo más cercano por la derecha (x in [10-18]) (byte)
- [13] Número de fila (coord y) matriz de observación enemigo más cercano por la derecha (x in [10-18]) (byte)
- [14] Distancia euclídea al bloque/ladrillo más cercano por la izquierda (x in [0-9]) (double)
- [15] Número de columna (coord x) matriz de observación bloque/ladrillo más cercano por la izquierda (x in [0-9]) (byte)
- [16] Número de fila (coord y) matriz de observación bloque/ladrillo más cercano por la izquierda (x in [0-9]) (byte)
- [17] Distancia euclídea al bloque/ladrillo más cercano por la derecha (x in [10-18]) (double)
- [18] Número de columna (coord x) matriz de observación bloque/ladrillo más cercano por la derecha (x in [10-18]) (byte)
- [19] Número de fila (coord y) matriz de observación bloque/ladrillo más cercano por la derecha (x in [10-18]) (byte)
- [20] Distancia euclídea a la moneda más cercana por la izquierda (x in [0-9]) (double)
- [21] Número de columna (coord x) matriz de observación moneda más cercana por la izquierda (x in [0-9]) (byte)
- [22] Número de fila (coord y) matriz de observación moneda más cercana por la izquierda (x in [0-9]) (byte)
- [23] Distancia euclídea a la moneda más cercana por la derecha (x in [10-18]) (double)
- [24] Número de columna (coord x) matriz de observación moneda más cercana por la derecha (x in [10-18]) (byte)
- [25] Número de fila (coord y) matriz de observación moneda más cercana por la derecha (x in [10-18]) (byte)
- [26] Número de enemigos observados en el entorno (int)
- [27] Número de monedas observadas en el entorno (int)
## Atributos booleanos o atributos binarios
- [28] Enemigo cercano por la derecha (en matriz de observación [8-9][10-11]) (bool)(0, 1)
- [29] Bloque cercano por la derecha (en matriz de observación [8-9][10-11]) (bool)(0, 1)
- [30] Enemigo delante a nivel del suelo (por debajo de los pies) (en matriz de observación [10][10-11]) (bool)(0, 1)
- [31] Bloque delante a nivel del suelo (por debajo de los pies) (en matriz de observación [10][10-11]) (bool)(0, 1)
- [32] Hay foso/abismo/acantilado delante (en columna [10]) (bool)(0, 1) **Comentado en el código, actualmente no se implementa puesto que los identifica mal**
- [33] isMarioOnGround (bool)(0, 1)
- [34] isMarioAbleToJump (bool)(0, 1)
- [35] isMarioAbleToShoot (bool)(0, 1)
- [36] isMarioCarrying (bool)(0, 1)
- [37] Se ha matado o no un enemigo en el tick actual (bool)(0, 1)
- [38] Mario ha sido dañado o no en el tick actual (bool)(0, 1)
- [39] Hay desnivel hacia abajo delante (y in [10-11]) (bool)(0, 1)
## Otros atributos
- [40] Número de monedas recogidas en el tick actual (int)
- [41] marioMode (int)(0, 1, 2) == (Small, Large, Fire)
- [42] marioStatus (int)(0, 1, 2) == (Small, Large, Fire)
## Clase. Clasificamos por acción
- [43] Acción realizada o tecla pulsada (int)(0, 1, 2, 3, 4, 5, 6, 7, 8, 9) == (N, J, R, RJ, RS, RJS, L, LJ, LS, LJS)