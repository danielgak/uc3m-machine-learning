package ch.idsia.agents.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.ArrayList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

import ch.idsia.tools.Instance;
import ch.idsia.tools.Cluster;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class P3BotAgent extends BasicMarioAIAgent implements Agent {

    int tick;
    private Random R = null;
    private byte jumpCounter, atascado;
    private int lastTickIntermediateReward, lastTickKillsTotal, lastTickCoinsGained, lastCellPosition_X;
    
    // Tabla Q
    private float [][] qtable;
    // private Cluster [] clusters;

    // Instancia del tick actual del juego
    private Instance tickInstance;

    public P3BotAgent() {
        super("P3BotAgent");
        reset();
        jumpCounter = atascado = 0;
        lastCellPosition_X = 0;
        
        // this.clusters = new Cluster[10];
        
        // this.clusters[0] = new Cluster(1153f,1.414f,8f,10f,12.728f,18f,18f,7.071f,2f,8f,12.728f,18f,18f,1.414f,8f,8f,12.728f,18f,18f,1f,4f,0f,0f,0f,0f,1f,1f,1f,1f,0f,1f);
        // this.clusters[1] = new Cluster(331f,1f,9f,10f,5.099f,14f,10f,1f,8f,9f,2.236f,10f,7f,8.062f,5f,2f,2f,11f,9f,3f,7f,0f,0f,0f,1f,1f,0f,0f,1f,0f,0f);
        // this.clusters[2] = new Cluster(51f,1.414f,8f,10f,7.071f,16f,10f,2f,7f,9f,6.708f,12f,3f,12.728f,0f,0f,1f,10f,9f,2f,16f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // this.clusters[3] = new Cluster(483f,12.728f,0f,0f,12.728f,18f,18f,12.728f,0f,0f,12.728f,18f,18f,12.728f,0f,0f,3.162f,12f,8f,0f,1f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // this.clusters[4] = new Cluster(421f,12.728f,0f,0f,7.071f,14f,14f,5.099f,8f,4f,5.099f,10f,4f,7f,9f,2f,7.071f,10f,2f,2f,5f,0f,0f,0f,1f,1f,1f,1f,1f,0f,1f);
        // this.clusters[5] = new Cluster(2445f,4.123f,5f,10f,6.325f,15f,11f,1f,8f,9f,2f,11f,9f,7.81f,4f,3f,1.414f,10f,8f,2f,8f,0f,1f,0f,1f,1f,0f,1f,1f,0f,0f);
        // this.clusters[6] = new Cluster(1491f,3.606f,6f,11f,12.728f,18f,18f,12.728f,0f,0f,1.414f,10f,8f,12.728f,0f,0f,1f,10f,9f,1f,11f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f);
        // this.clusters[7] = new Cluster(1773f,12.728f,0f,0f,3.606f,12f,7f,2f,7f,9f,1f,10f,9f,12.728f,0f,0f,12.728f,18f,18f,2f,0f,0f,1f,0f,1f,1f,0f,0f,1f,0f,0f);
        // this.clusters[8] = new Cluster(621f,12.728f,0f,0f,7.616f,16f,12f,6f,3f,9f,9.055f,18f,8f,7.28f,2f,7f,12.728f,18f,18f,1f,2f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // this.clusters[9] = new Cluster(1057f,2.236f,8f,11f,8.602f,14f,2f,1.414f,8f,8f,2f,11f,9f,3.162f,8f,6f,9.434f,14f,1f,3f,7f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f);

        this.qtable = new float[16][action.length];

        // Cargar tabla Q
        Scanner s = null;
        float qvalue = -1.0f;
        try {
            s = new Scanner(new File("qtable.txt"));
            s.useLocale(Locale.US);
            if (s.hasNextLine()) {
                System.out.println("Cargando la tabla Q:");
                for(int i = 0; i < qtable.length; i++){
                    for(int j = 0; j < qtable[i].length; j++){
                        qvalue = s.nextFloat();
                        System.out.print(qvalue+"\t");
                        qtable[i][j] = qvalue;
                    }
                    s.nextLine();
                    System.out.print("\n");
                }
            }
        } catch (Exception ex) {
            System.out.println("Error loading Q table from file: " + ex.getMessage());
        } finally {
            if (s != null)
                s.close();
        }
    }

    public int calculateSituation(Instance instance) {
        // int cluster = -1;
        // float min_distance = Float.MAX_VALUE, distance;
        
        // for (int i = 0; i < clusters.length; i++) {
        //     distance = clusters[i].calculateSimilitude(instance);
        //     if (distance < min_distance) {
        //         cluster = i;
        //         min_distance = distance;
        //     }
        // }
        // System.out.println("##### Situation: "+cluster+" #####");
        // return cluster;

        if (instance.isMarioOnGround == 0) {
            // Mario en el aire
            if ((instance.enemyNearRight == 1 || instance.enemyAheadOnFloorHeight == 1) &&
            (instance.blockNearRight == 1 || instance.blockAheadOnFloorHeight == 1)) {
                // Enemigo y obstáculo cerca
                if (instance.enemyAheadOnFloorHeight == 1 && instance.blockAheadOnFloorHeight == 1) {
                    // El enemigo y el obstáculo están a la misma altura que Mario
                    return 0;
                } else if (instance.enemyAheadOnFloorHeight == 1) {
                    // El enemigo está a la misma altura que Mario
                    return 1;
                } else if (instance.blockAheadOnFloorHeight == 1) {
                    // El obstáculo está a la misma altura que Mario
                    return 2;
                } else {
                    // El enemigo y el obstáculo están a distinta altura que Mario
                    return 3;
                }
            }
            else if (instance.enemyNearRight == 1 || instance.enemyAheadOnFloorHeight == 1) {
                // Enemigo cerca
                if (instance.enemyAheadOnFloorHeight == 1) {
                    // El enemigo está a la misma altura que Mario
                    return 4;
                } else {
                    // El enemigo está a distinta altura que Mario
                    return 5;
                }
            } else if (instance.blockNearRight == 1 || instance.blockAheadOnFloorHeight == 1) {
                // Obstáculo cerca
                if (instance.blockAheadOnFloorHeight == 1) {
                    // El bloque está a la misma altura que Mario
                    return 6;
                } else {
                    // El bloque está a distinta altura que Mario
                    return 7;
                }
            } else {
                // mario en el aire y no hay ni enemigo ni obstáculo cerca
                return 8;
            }
        } else {
            // Mario está en el suelo
            if (instance.enemyAheadOnFloorHeight == 1) {
                // Enemigo delante a la misma altura que Mario
                return 9;
            } else if (instance.enemyNearRight == 1) {
                // Enemigo cerca
                if (instance.blockAheadOnFloorHeight == 1) {
                    // Posible enemigo encima de obstáculo
                    return 10;
                } else if (instance.blockNearRight == 1) {
                    // Bloques y enemigos a la derecha
                    return 11;
                } else {
                    // Solo enemigos cerca
                    return 12;
                }
            } else if (instance.blockNearRight == 1 || instance.blockAheadOnFloorHeight == 1) {
                // Obstáculo cerca
                if (instance.blockAheadOnFloorHeight == 1) {
                    // Bloque a la misma altura que Mario
                    return 13;
                } else {
                    // Bloque a distinta altura que Mario
                    return 14;
                }
            } else {
                // Mario en el suelo y no hay ni enemigo ni obstáculo cerca
                return 15;
            }
        }
    }

    public int calculateAction(int situation) {
        float qvalue1 = -1.0f, qvalue2 = -1.0f;
        int act1 = -1, act2 = -1;

        for (int i = 0; i < action.length; i++) {
            if (qtable[situation][i] > qvalue1) {
                // Guardar segundo mejor valor q y acción
                if (act1 != -1) {
                    qvalue2 = qvalue1;
                    act2 = act1;
                    System.out.println("Qvalue2 for action "+act2+" is "+qvalue2);
                }
                // Guardar mejor valor q y accion
                qvalue1 = qtable[situation][i];
                act1 = i;
                System.out.println("Qvalue1 for action "+act1+" is "+qvalue1);
            }
            // Guardar segundo mejor valor q y acción
            else if (qtable[situation][i] > qvalue2) {
                qvalue2 = qtable[situation][i];
                act2 = i;
                System.out.println("Qvalue2 for action "+act2+" is "+qvalue2);
            }
        }
        // Para evitar quedarnos atascados en la misma pos X elegimos accion con segundo mayor valor q
        if(atascado > 12) {
            System.out.println("%%%%%%%%%% Aplicando accion secundaria: "+act2+" %%%%%%%%%%");
            atascado = 0;
            return act2;
        }
        return act1;
    }

    public void reset() {
        // Dummy reset, of course, but meet formalities!
        R = new Random();
    }
    
    public void integrateObservation(Environment environment) {
        // IMPORTANTE: Si se utilizan métodos que tardan mucho como println, cada tick puede tardar en procesarse más de
        // de lo que permite la competición de Mario AI. Si el agente es demasiado lento procesando y el simulador no
        // puede funcionar en tiempo real, se cerrará automáticamente, lor lo que se insta a que el código escrito sea
        // lo más eficiente posible.


        // INFORMACION DEL ENTORNO

        // En la interfaz Environment.java vienen definidos los metodos que se pueden emplear para recuperar informacion
        // del entorno de Mario. Algunos de los mas importantes (y que utilizaremos durante el curso)...

        // System.out.println("########################### TICK " + tick + " ###########################");

        // Registro en fichero de entrenamiento
        // tf.writeExample(environment, tick, action, false);

        final int UPDATE_TICKS = 5;
        final double MAX_DISTANCE = Math.sqrt(162);
        final byte MARIO_MERGE_POS = 9;
        int lastTickDiffIR=-1, last6TicksDiffIR=-1, numEnemiesObserved=0, numCoinsObserved=0;
        byte enemyWasKilledBin, coinsGainedLastTick, pressedKeysByte=0;
        byte[] nearestEnemyLeftCoords={0, 0}, nearestEnemyRightCoords={18, 18};
        byte[] nearestBlockLeftCoords={0, 0}, nearestBlockRightCoords={18, 18};
        byte[] nearestCoinLeftCoords={0, 0}, nearestCoinRightCoords={18, 18};
        double nearestEnemyLeft=MAX_DISTANCE, nearestEnemyRight=MAX_DISTANCE;
        double nearestBlockLeft=MAX_DISTANCE, nearestBlockRight=MAX_DISTANCE;
        double nearestCoinLeft=MAX_DISTANCE, nearestCoinRight=MAX_DISTANCE;
        byte enemyNearRight=0, blockNearRight=0;
        byte enemyAheadOnFloorHeight=0, blockAheadOnFloorHeight=0, abyssAhead=0, marioWasInjuredBin=0, isSlopeDown=0;
        int[] last6ticksIntermediateReward = new int[6];

        // Solicitamos la información de evaluación y sobre el entorno necesaria
        int evaluationInfo[]=environment.getEvaluationInfoAsInts();
        int intermediateReward=environment.getIntermediateReward();
        byte[][] mergeObs=environment.getMergedObservationZZ(1, 1);

        // Calculamos las diferencias de intermediateReward
        if (tick > 0) {
            lastTickDiffIR = intermediateReward - lastTickIntermediateReward;
            if (tick > 5)
                last6TicksDiffIR = intermediateReward - last6ticksIntermediateReward[0];
        }
        // Actualizamos los valores para futuros ejemplos
        lastTickIntermediateReward = intermediateReward;
        for (byte i = 0; i < UPDATE_TICKS; i++)
            last6ticksIntermediateReward[i] = last6ticksIntermediateReward[i+1];
        last6ticksIntermediateReward[last6ticksIntermediateReward.length-1] = intermediateReward;

        // Calculamos si en este tick hemos matado a un enemigo o recogido una moneda
        // y actualizamos valores para la próxima instancia de entrenamiento;
        enemyWasKilledBin = (byte)(evaluationInfo[6] - lastTickKillsTotal);
        lastTickKillsTotal = evaluationInfo[6];
        coinsGainedLastTick = (byte)(evaluationInfo[10] - lastTickCoinsGained);
        lastTickCoinsGained = evaluationInfo[10];

        // Buscamos los enemigos, bloques y monedas más cercanos por la izq. y la der.y calculamos su distancia euclídea
        double aux;
        for (byte i = 0; i < mergeObs.length; i++) {
            for (byte j = 0; j < mergeObs[i].length; j++) {
                if ((mergeObs[i][j] == -24 || mergeObs[i][j] == -60 || mergeObs[i][j] == -62 || mergeObs[i][j] == -85) && i <= MARIO_MERGE_POS) {
                    // Obstáculo
                    // Solo tenemos en cuenta los que están a la altura de los pies de Mario o por encima
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestBlockLeft) {
                            nearestBlockLeft = aux;
                            nearestBlockLeftCoords[0] = j;
                            nearestBlockLeftCoords[1] = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestBlockRight) {
                            nearestBlockRight = aux;
                            nearestBlockRightCoords[0] = j;
                            nearestBlockRightCoords[1] = i;
                        }
                    }
                } else if (mergeObs[i][j] == 2) {
                    // Moneda
                    numCoinsObserved++;
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestCoinLeft) {
                            nearestCoinLeft = aux;
                            nearestCoinLeftCoords[0] = j;
                            nearestCoinLeftCoords[1] = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestCoinRight) {
                            nearestCoinRight = aux;
                            nearestCoinRightCoords[0] = j;
                            nearestCoinRightCoords[1] = i;
                        }
                    }
                } else if (mergeObs[i][j] == 80) {
                    // Enemigo
                    numEnemiesObserved++;
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestEnemyLeft) {
                            nearestEnemyLeft = aux;
                            nearestEnemyLeftCoords[0] = j;
                            nearestEnemyLeftCoords[1] = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestEnemyRight) {
                            nearestEnemyRight = aux;
                            nearestEnemyRightCoords[0] = j;
                            nearestEnemyRightCoords[1] = i;
                        }
                    }
                }
            }
        }
        // Comporbamos el contenido de las regiones de estudio
        for (byte i = 8; i < 11; i++) {
            for (byte j = 10; j < 12; j++) {
                if (i != 10) {
                    if (mergeObs[i][j] == -24 || mergeObs[i][j] == -60 || mergeObs[i][j] == -62 || mergeObs[i][j] == -85) {
                        // Obstáculo/ladrillo
                        blockNearRight = 1;
                    }
                    else if (mergeObs[i][j] == 80) {
                        // Enemigo
                        enemyNearRight = 1;
                    }
                } else {
                    if (mergeObs[i][j] == -24 || mergeObs[i][j] == -60 || mergeObs[i][j] == -62 || mergeObs[i][j] == -85) {
                        // Obstáculo/ladrillo
                        blockAheadOnFloorHeight = 1;
                    } else{
                        if (mergeObs[i][j] == 80) {
                            // Enemigo
                            enemyAheadOnFloorHeight = 1;
                        }
                        if (environment.isMarioOnGround()) {
                            isSlopeDown = 1;
                        }
                    }
                }
            }
        }
        // Comprobamos si hay foso en la columna [10] de merge
        if (mergeObs[16][10] == 0) {
            abyssAhead = 1;
        }

        // Generamos la instancia del tick
        tickInstance = new Instance(
            (short) evaluationInfo[12],
            (short) evaluationInfo[11],
            (short) intermediateReward,
            (short) lastTickDiffIR,
            (short) last6TicksDiffIR,
            (short) 0,
            (short) 0,
            (short) 0,
            (float) nearestEnemyLeft,
            (byte) nearestEnemyLeftCoords[0],
            (byte) nearestEnemyLeftCoords[1],
            (float) nearestEnemyRight,
            (byte) nearestEnemyRightCoords[0],
            (byte) nearestEnemyRightCoords[1],
            (float) nearestBlockLeft,
            (byte) nearestBlockLeftCoords[0],
            (byte) nearestBlockLeftCoords[1],
            (float) nearestBlockRight,
            (byte) nearestBlockRightCoords[0],
            (byte) nearestBlockRightCoords[1],
            (float) nearestCoinLeft,
            (byte) nearestCoinLeftCoords[0],
            (byte) nearestCoinLeftCoords[1],
            (float) nearestCoinRight,
            (byte) nearestCoinRightCoords[0],
            (byte) nearestCoinRightCoords[1],
            (short) numEnemiesObserved,
            (short) numCoinsObserved,
            (byte) enemyNearRight,
            (byte) blockNearRight,
            (byte) enemyAheadOnFloorHeight,
            (byte) blockAheadOnFloorHeight,
            (byte) abyssAhead,
            (byte) ((environment.isMarioOnGround())? 1 : 0),
            (byte) ((environment.isMarioAbleToJump())? 1 : 0),
            (byte) ((environment.isMarioAbleToShoot())? 1 : 0),
            (byte) ((environment.isMarioCarrying())? 1 : 0),
            (byte) enemyWasKilledBin,
            (byte) marioWasInjuredBin,
            (byte) isSlopeDown,
            (byte) coinsGainedLastTick,
            (byte) evaluationInfo[7],
            (byte) evaluationInfo[8],
            (byte) pressedKeysByte
        );
        
        // Para evitar quedarnos atascados, contamos el número de ticks en los que Mario se mantiene en la misma posición X
        if (evaluationInfo[0] == lastCellPosition_X) {
            atascado++;
        } else {
            lastCellPosition_X = evaluationInfo[0];
            atascado = 0;
        }

        tick++;
    }

    public boolean[] getAction() {
        // La accion es un array de booleanos de dimension 6
        // action[Mario.KEY_LEFT] Mueve a Mario a la izquierda
        // action[Mario.KEY_RIGHT] Mueve a Mario a la derecha
        // action[Mario.KEY_DOWN] Mario se agacha si esta en estado grande
        // action[Mario.KEY_JUMP] Mario salta
        // action[Mario.KEY_SPEED] Incrementa la velocidad de Mario y dispara si esta en modo fuego
        // action[Mario.KEY_UP] Arriba
        // Se puede utilizar cualquier combinacion de valores true, false para este array
        // Por ejemplo: (false true false true false false) Mario salta a la derecha
        // IMPORTANTE: Si se ejecuta la accion anterior todo el tiempo, Mario no va a saltar todo el tiempo hacia adelante.
        // Cuando se ejecuta la primera vez la accion anterior, se pulsa el boton de saltar, y se mantiene pulsado hasta que
        // no se indique explicitamente action[Mario.KEY_JUMP] = false. Si habeis podido jugar a Mario en la consola de verdad,
        // os dareis cuenta que si manteneis pulsado todo el tiempo el boton de saltar, Mario no salta todo el tiempo sino una
        // unica vez en el momento en que se pulsa. Para volver a saltar debeis despulsarlo (action[Mario.KEY_JUMP] = false),
        // y volverlo a pulsar (action[Mario.KEY_JUMP] = true).
        // Para P1 solo válidas estas teclas:
        // action[Mario.KEY_RIGHT] = true;
        // action[Mario.KEY_JUMP] = true;
        // System.out.println("HOJA X: Y");
    
        // Reseteamos las teclas pulsadas
        for (byte i = 0; i < Environment.numberOfKeys; i++)
            action[i] = false;

        // Llamamos a la función de evaluación para conocer la acción a ejecutar
        switch(calculateAction(calculateSituation(tickInstance))){
            case 0: // None
                break;
            case 1: // Jump
                action[Mario.KEY_JUMP] = true;
                break;
            case 2: // Right
                action[Mario.KEY_RIGHT] = true;
                break;
            case 3: // Right-Jump
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_JUMP] = true;
                break;
            case 4: // Right-Speed
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_SPEED] = true;
                break;
            case 5: // Right-Jump-Speed
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_JUMP] = true;
                action[Mario.KEY_SPEED] = true;
                break;
            case 6: // Left
                action[Mario.KEY_LEFT] = true;
                break;
            case 7: // Left-Jump
                action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_JUMP] = true;
                break;
            case 8: // Left-Speed
                action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_SPEED] = true;
                break;
            case 9:  // Left-Jump-Speed
                action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_JUMP] = true;
                action[Mario.KEY_SPEED] = true;
                break;

        }
        // Para desbloquear salto (tecla de salto se mantiene pulsada y Mario en el suelo no puede saltar)
        if (action[Mario.KEY_JUMP]) {
            jumpCounter++;
            if (jumpCounter >= 10){ // Si Mario lleva más de 10 ticks intentando saltar
                if (!this.isMarioOnGround) {
                    action[Mario.KEY_JUMP] = false; // Tecla de salto a false por al menos un tick
                    jumpCounter = 0;
                }
            }
        } else jumpCounter = 0;

        // // CUIDADO, PROCEDURAL: Para evitar quedarnos atascados contra muros 
        // if (atascado >= 48) { // Si Mario lleva más de 2 segundos (48 ticks) en la misma posición X
        //     // Intentamos saltar e ir a la derecha
        //     action[Mario.KEY_RIGHT] = true;
        //     action[Mario.KEY_JUMP] = true;
        //     atascado = 0;
        // }
        return action;
    }
}
