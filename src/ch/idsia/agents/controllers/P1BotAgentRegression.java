/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.TrainingFile;

import java.util.Random;

public class P1BotAgentRegression extends BasicMarioAIAgent implements Agent {

    int tick;
    private Random R = null;
    private TrainingFile tf;
    private double[] predicted6IntermediateReward, predicted12IntermediateReward, predicted24IntermediateReward;
    private int predicted6success, predicted6fail, predicted12success, predicted12fail, predicted24success, predicted24fail;
    private long totalIntermediateReward, totalPredicted6IntermediateReward, totalPredicted12IntermediateReward, totalPredicted24IntermediateReward;

    private final int UPDATE_TICKS = 5;
    private final double MAX_DISTANCE = Math.sqrt(162);
    private final byte MARIO_MERGE_POS = 9;

    private boolean marioCanGoBack, captureJumpLonely, captureStay;
    private byte frecuencia, atascado;
    private int lastTickIntermediateReward, lastTickCoinsGained, lastTickKillsTotal, lastCellPosition_X;
    // private int[] last6ticksIntermediateReward;
    private int[] last24ticksIntermediateReward;
    private boolean[] lastTickKeys;

    public P1BotAgentRegression() {
        super("P1BotAgentRegression");
        reset();
        predicted6IntermediateReward = new double[6];
        predicted12IntermediateReward = new double[12];
        predicted24IntermediateReward = new double[24];
        predicted6success = predicted6fail = predicted12success = predicted12fail = predicted24success = predicted24fail = 0;
        totalIntermediateReward = totalPredicted6IntermediateReward = totalPredicted12IntermediateReward = totalPredicted24IntermediateReward = 0L;
        lastTickIntermediateReward = 0;
        lastTickCoinsGained = 0;
        lastTickKillsTotal = 0;
        atascado = 0;
        lastCellPosition_X = 0;
        // last6ticksIntermediateReward = new int[6];
        last24ticksIntermediateReward = new int[24];
        tick = 0;
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

        // System.out.println("------------------ TICK " + tick + " ------------------");

        // Registro en fichero de entrenamiento
        // tf.writeExample(environment, tick, action);

        /*
        // Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con informacion de los elementos
        // en la escena. La funcion getLevelSceneObservationZ recibe un numero para indicar el nivel de detalle
        // de la informacion devuelta. En uno de los anexos del tutorial 1 se puede encontrar informacion de
        // los niveles de detalle y el tipo de informacion devuelta.
        System.out.println("\nESCENA");
        byte [][] envesc;
        envesc = environment.getLevelSceneObservationZ(1);
        for (int mx = 0; mx < envesc.length; mx++){
            System.out.print(mx + ": [");
            for (int my = 0; my < envesc[mx].length; my++)
                System.out.print(envesc[mx][my] + " ");

            System.out.println("]");
        }
        */

        /*
        // Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con informacion de los enemigos
        // en la escena. La funcion getEnemiesObservationZ recibe un numero para indicar el nivel de detalle
        // de la informacion devuelta. En uno de los anexos del tutorial 1 se puede encontrar informacion de
        // los niveles de detalle y el tipo de informacion devuelta.
        System.out.println("\nENEMIGOS");
        byte [][] envenm;
        envenm = environment.getEnemiesObservationZ(1);
        for (int mx = 0; mx < envenm.length; mx++) {
            System.out.print(mx + ": [");
            for (int my = 0; my < envenm[mx].length; my++)
                System.out.print(envenm[mx][my] + " ");

            System.out.println("]");
        }
        */

        /*
        // Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con la union de los dos arrays
        // anteriores, es decir, devuelve en un mismo array la informacion de los elementos de la
        // escena y los enemigos.
        System.out.println("\nMERGE");
        byte [][] env;
        env = environment.getMergedObservationZZ(1, 1);
        for (int mx = 0; mx < env.length; mx++) {
            System.out.print(mx + ": [");
            for (int my = 0; my < env[mx].length; my++)
                System.out.print(env[mx][my] + " ");

            System.out.println("]");
        }
        */

        // Posicion de Mario utilizando las coordenadas del sistema
        // System.out.println("POSICION MARIO");
        // float[] posMario;
        // posMario = environment.getMarioFloatPos();
        // for (int mx = 0; mx < posMario.length; mx++)
        //      System.out.print(posMario[mx] + " ");

        // Posicion que ocupa Mario en el array anterior
        // System.out.println("\nPOSICION MARIO MATRIZ");
        // int[] posMarioEgo;
        // posMarioEgo = environment.getMarioEgoPos();
        // for (int mx = 0; mx < posMarioEgo.length; mx++)
        //      System.out.print(posMarioEgo[mx] + " ");


        // Estado de mario
        // marioStatus, marioMode, isMarioOnGround (1 o 0), isMarioAbleToJump() (1 o 0), isMarioAbleToShoot (1 o 0),
        // isMarioCarrying (1 o 0), killsTotal, killsByFire,  killsByStomp, killsByShell, timeLeft
        // System.out.println("\nESTADO MARIO");
        // int[] marioState;
        // marioState = environment.getMarioState();
        // for (int mx = 0; mx < marioState.length; mx++)
        //      System.out.print(marioState[mx] + " ");


        // Mas informacion de evaluacion...
        // distancePassedCells, distancePassedPhys, flowersDevoured, killsByFire, killsByShell, killsByStomp, killsTotal, marioMode,
        // marioStatus, mushroomsDevoured, coinsGained, timeLeft, timeSpent, hiddenBlocksFound
        // System.out.println("\nINFORMACION DE EVALUACION");
        // int[] infoEvaluacion;
        // infoEvaluacion = environment.getEvaluationInfoAsInts();
        // for (int mx = 0; mx < infoEvaluacion.length; mx++)
        //      System.out.print(infoEvaluacion[mx] + " ");


        // Informacion del refuerzo/puntuacion que ha obtenido Mario. Nos puede servir para determinar lo bien o mal que lo esta haciendo.
        // Por defecto este valor engloba: reward for coins, killed creatures, cleared dead-ends, bypassed gaps, hidden blocks found
        // System.out.println("\nREFUERZO");
        // int reward = environment.getIntermediateReward();
        // System.out.print(reward);

        // System.out.println("\n");

        // Obtenemos información sobre la posición de los obstáculos y los enemigos
        mergedObservation = environment.getMergedObservationZZ(2, 2);
        // Obtenemos información sobre Mario
        isMarioOnGround = environment.isMarioOnGround();
        isMarioAbleToJump = environment.isMarioAbleToJump();

        String pressedKeys = "";
        int lastTickDiffIR = -1, last6TicksDiffIR = -1;
        byte enemyWasKilledBin, coinsGainedLastTick, pressedKeysByte = 0;
        byte[] nearestEnemyLeftCoords = {0,0}, nearestEnemyRightCoords = {18, 18};
        byte[] nearestBlockLeftCoords = {0,0}, nearestBlockRightCoords = {18, 18};
        byte[] nearestCoinLeftCoords = {0,0}, nearestCoinRightCoords = {18,18};
        double nearestEnemyLeft = MAX_DISTANCE, nearestEnemyRight = MAX_DISTANCE;
        double nearestBlockLeft = MAX_DISTANCE, nearestBlockRight = MAX_DISTANCE;
        double nearestCoinLeft = MAX_DISTANCE, nearestCoinRight = MAX_DISTANCE;
        byte enemyNearRight = 0, blockNearRight = 0;
        byte enemyAheadOnFloorHeight = 0, blockAheadOnFloorHeight = 0, abyssAhead = 0, marioWasInjuredBin = 0, isSlopeDown = 0;

        // ************ Calculamos el ejemplo de entrenamiento ************

        // Solicitamos la información de evaluación y sobre el entorno necesaria
        int evaluationInfo[] = environment.getEvaluationInfoAsInts();
        int intermediateReward = environment.getIntermediateReward();
        byte[][] mergeObs = environment.getMergedObservationZZ(1, 1);

        if (tick == 0) {
            lastTickKeys = action;
        }

        // Calculamos las diferencias de intermediateReward
        int intermediateRewardPredicted6 = 0, intermediateRewardPredicted12 = 0, intermediateRewardPredicted24 = 0, diff;
        if (tick > 0) {
            lastTickDiffIR = intermediateReward - lastTickIntermediateReward;
            if (lastTickDiffIR < 0) {
                marioWasInjuredBin = 1;
            }
            if (tick > 5) {
                last6TicksDiffIR = intermediateReward - last24ticksIntermediateReward[18];
                intermediateRewardPredicted6 = intermediateReward + last6TicksDiffIR;
                if (tick > 11) {
                    diff = intermediateReward - last24ticksIntermediateReward[12];
                    intermediateRewardPredicted12 = intermediateReward + diff;
                    if (tick > 23) {
                        diff = intermediateReward - last24ticksIntermediateReward[0];
                        intermediateRewardPredicted24 = intermediateReward + diff;
                    } else intermediateRewardPredicted24 = intermediateReward + diff * 2;
                } else {
                    intermediateRewardPredicted12 = intermediateReward + last6TicksDiffIR * 2;
                    intermediateRewardPredicted24 = intermediateReward + last6TicksDiffIR * 4;
                }
            } else {
                intermediateRewardPredicted6 = intermediateReward + lastTickDiffIR * 6;
                intermediateRewardPredicted12 = intermediateReward + lastTickDiffIR * 12;
                intermediateRewardPredicted24 = intermediateReward + lastTickDiffIR * 24;
            }
        }
        // Actualizamos los valores para futuros ejemplos
       lastTickIntermediateReward = intermediateReward;
       for (byte i = 0; i < 23; i++)
           last24ticksIntermediateReward[i] = last24ticksIntermediateReward[i+1];
       last24ticksIntermediateReward[last24ticksIntermediateReward.length-1] = intermediateReward;

    //    // Calculamos las diferencias de intermediateReward
    //    if (ticks > 0) {
    //        lastTickDiffIR = intermediateReward - lastTickIntermediateReward;
    //        if (ticks > 5)
    //            last6TicksDiffIR = intermediateReward - last6ticksIntermediateReward[0];
    //    }
    //    // Actualizamos los valores para futuros ejemplos
    //    lastTickIntermediateReward = intermediateReward;
    //    for (int i = 0; i < UPDATE_TICKS; i++)
    //        last6ticksIntermediateReward[i] = last6ticksIntermediateReward[i+1];
    //    last6ticksIntermediateReward[last6ticksIntermediateReward.length-1] = intermediateReward;

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
                    // TODO quizás estaría bien diferenciar el tipo de enemigo, porque los hay que vuelan y que no, etc
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

        // Vemos que teclas están pulsadas
        for (int i = 0; i < lastTickKeys.length; i++) {
            if (lastTickKeys[i]) {
                // Si la tecla i está pulsada
                if (Mario.KEY_RIGHT == i)
                // Está pulsada la tecla ir hacia la derecha
                    pressedKeys += "R";
               else if (Mario.KEY_LEFT == i && !lastTickKeys[Mario.KEY_RIGHT])
               // Está pulsada la tecla ir hacia la izquierda y no está pulsada a la vez la tecla
               // ir hacia la derecha, ya que damos priorida a la acción "ir hacia la derecha"
                   pressedKeys += "L";
                else if (Mario.KEY_JUMP == i) // Está pulsada la tecla salto
                    pressedKeys += "J";
               else if (Mario.KEY_SPEED == i) // Está pulsada la tecla speed
                   pressedKeys += "S";
            }
        }
        // Transformamos los valores string (L, LJ, LS, LJS, R, RJ, RS, RJS, J, N) a valores numéricos
        if (pressedKeys.equals("J")) {
            pressedKeysByte = 1;
        }
        else if (pressedKeys.equals("R"))
            pressedKeysByte = 2;
        else if (pressedKeys.equals("RJ"))
            pressedKeysByte = 3;
        else if (pressedKeys.equals("RS"))
            pressedKeysByte = 4;
        else if (pressedKeys.equals("RJS"))
            pressedKeysByte = 5;
        else if (pressedKeys.equals("L")) {
            pressedKeysByte = 6;
        }
        else if (pressedKeys.equals("LJ")) {
            pressedKeysByte = 7;
        }
        else if (pressedKeys.equals("LS")) {
            pressedKeysByte = 8;
        }
        else if (pressedKeys.equals("LJS")) {
            pressedKeysByte = 9;
        }
        else if (pressedKeys.equals("")) {
            pressedKeysByte = 0;
        }

        totalIntermediateReward += intermediateReward;

        for (byte i = 0; i < 5; i ++)
            predicted6IntermediateReward[i] = predicted6IntermediateReward[i+1];
        predicted6IntermediateReward[predicted6IntermediateReward.length-1] = 1 * intermediateReward + -0.0008 * lastTickDiffIR + 1.0001 * last6TicksDiffIR + 0.0001 * intermediateRewardPredicted12 + -0.0001 * intermediateRewardPredicted24 + -0.0058 * nearestEnemyLeft + -0.0068 * nearestEnemyLeftCoords[0] + -0.0039 * nearestEnemyLeftCoords[1] + 0.014  * nearestEnemyRight + -0.0106 * nearestEnemyRightCoords[0] + -0.0024 * nearestBlockLeft + -0.0032 * nearestBlockLeftCoords[0] + -0.0035 * nearestBlockLeftCoords[1] + 0.0044 * nearestBlockRight + -0.0042 * nearestBlockRightCoords[0] + 0.0033 * nearestBlockRightCoords[1] + -0.0007 * nearestCoinLeft + -0.0013 * nearestCoinLeftCoords[1] + 0.0012 * nearestCoinRight + -0.0016 * nearestCoinRightCoords[0] + 0.0002 * nearestCoinRightCoords[1] + 0.0307 * enemyNearRight + 0.013  * blockNearRight + 0.0167 * enemyAheadOnFloorHeight + -0.01   * blockAheadOnFloorHeight + -0.015  * ((this.isMarioOnGround)?1:0) + -0.0145 * ((this.isMarioAbleToJump)?1:0) + -0.0105 * enemyWasKilledBin + 0.0173 * coinsGainedLastTick + -0.0561 * pressedKeysByte + 0.384;
        totalPredicted6IntermediateReward += (long)(predicted6IntermediateReward[predicted6IntermediateReward.length-1]);

        for (byte i = 0; i < 11; i ++)
            predicted12IntermediateReward[i] = predicted12IntermediateReward[i+1];
        predicted12IntermediateReward[predicted12IntermediateReward.length-1] = -0.5811 * intermediateReward + -0.175  * lastTickDiffIR + -0.3572 * last6TicksDiffIR + 1.2906 * intermediateRewardPredicted6 + 0.29   * intermediateRewardPredicted24 + -0.0693 * nearestEnemyLeft + -0.3781 * nearestEnemyLeftCoords[0] + 0.1532 * nearestEnemyLeftCoords[1] + -0.1737 * nearestEnemyRight + 0.1261 * nearestEnemyRightCoords[0] + 0.1246 * nearestEnemyRightCoords[1] + 0.8084 * nearestBlockLeft + 0.6317 * nearestBlockLeftCoords[0] + 0.441  * nearestBlockLeftCoords[1] + -0.0942 * nearestBlockRightCoords[0] + 0.5381 * nearestCoinLeft + 0.4871 * nearestCoinLeftCoords[0] + 0.1769 * nearestCoinLeftCoords[1] + 0.0948 * nearestCoinRight + 0.9151 * enemyNearRight + -0.6406 * blockAheadOnFloorHeight + -1.2063 * ((this.isMarioOnGround)?1:0) + 0.5919 * ((this.isMarioAbleToJump)?1:0) + 0.8761 * coinsGainedLastTick + 0.538  * pressedKeysByte + -19.2973;
        totalPredicted12IntermediateReward += (long)(predicted12IntermediateReward[predicted12IntermediateReward.length-1]);

        for (byte i = 0; i < 23; i ++)
            predicted24IntermediateReward[i] = predicted24IntermediateReward[i+1];
        predicted24IntermediateReward[predicted24IntermediateReward.length-1] = -0.282  * intermediateReward + -0.4332 * last6TicksDiffIR + 1.2849 * intermediateRewardPredicted12 + 0.1646 * nearestEnemyLeft + -0.4056 * nearestEnemyLeftCoords[1] + -1.0682 * nearestEnemyRight + 0.7969 * nearestEnemyRightCoords[0] + -2.3371 * nearestBlockLeft + -0.6307 * nearestBlockLeftCoords[0] + -1.315  * nearestBlockLeftCoords[1] + -0.3964 * nearestBlockRight + 0.5082 * nearestBlockRightCoords[0] + -2.9267 * nearestCoinLeft + -0.3925 * nearestCoinLeftCoords[0] + -0.6596 * nearestCoinLeftCoords[1] + -0.428  * nearestCoinRight + 0.5277 * nearestCoinRightCoords[0] + -0.063  * nearestCoinRightCoords[1] + -1.36   * enemyNearRight + 2.7432 * enemyAheadOnFloorHeight + -0.6076 * blockAheadOnFloorHeight + -2.2931 * ((this.isMarioAbleToJump)?1:0) + 4.3648 * enemyWasKilledBin + -1.4951 * pressedKeysByte + 62.1584;
        totalPredicted24IntermediateReward += (long)(predicted24IntermediateReward[predicted24IntermediateReward.length-1]);

        if (tick >= 6) {
            // Comprobar predicción n+6
            if (predicted24IntermediateReward[0] == predicted24IntermediateReward[5])
                predicted6success++;
            else predicted6fail++;
            if (tick >= 12) {
                // Comprobar predicción n+12
                if (predicted24IntermediateReward[0] == predicted24IntermediateReward[11])
                    predicted12success++;
                else predicted12fail++;
                if (tick >= 24) {
                    // Comprobar predicción n+24
                    if (predicted24IntermediateReward[0] == predicted24IntermediateReward[23])
                        predicted24success++;
                    else predicted24fail++;
                }
            }
        }
        tick++;
        if (environment.getMarioStatus() != Mario.STATUS_RUNNING) {
            System.out.println("Fin de la partida. Se han ejecutado un total de "+tick+" ticks.");
            // Fin de la partida
            float p6porcent = (predicted6fail / (predicted6fail + predicted6success)) * 100;
            float p12porcent = (predicted12fail / (predicted12fail + predicted12success)) * 100;
            float p24porcent = (predicted24fail / (predicted24fail + predicted24success)) * 100;
            System.out.println(
                "\n######## Resultados de la regresion: #######\n\n"+
                "#### Predicted n + 6 ####\n"+
                "\tDe un total de "+tick+" ticks ha predicho correctamente "+predicted6success+" ticks ("+p6porcent+"%) y "+predicted6fail+" ticks erroneamente ("+(100-p6porcent)+"%).\n"+
                "\tLa acumulacion de intermediateReward predicha ha sido "+totalPredicted6IntermediateReward+" y el valor real es de "+totalIntermediateReward+", lo que nos da una deviacion de "+(Math.abs(totalPredicted6IntermediateReward-totalIntermediateReward))+"\n"+
                "\n#### Predicted n + 12 ####\n"+
                "\tDe un total de "+tick+" ticks ha predicho correctamente "+predicted12success+" ticks ("+p12porcent+"%) y "+predicted12fail+" ticks erroneamente ("+(100-p12porcent)+"%).\n"+
                "\tLa acumulacion de intermediateReward predicha ha sido "+totalPredicted12IntermediateReward+" y el valor real es de "+totalIntermediateReward+", lo que nos da una deviacion de "+(Math.abs(totalPredicted12IntermediateReward-totalIntermediateReward))+"\n"+
                "\n#### Predicted n + 24 ####\n"+
                "\tDe un total de "+tick+" ticks ha predicho correctamente "+predicted24success+" ticks ("+p24porcent+"%) y "+predicted24fail+" ticks erroneamente ("+(100-p24porcent)+"%).\n"+
                "\tLa acumulacion de intermediateReward predicha ha sido "+totalPredicted24IntermediateReward+" y el valor real es de "+totalIntermediateReward+", lo que nos da una deviacion de "+(Math.abs(totalPredicted24IntermediateReward-totalIntermediateReward))+"\n"
            );
        }
        // Obtenemos información sobre la posición de los obstáculos y los enemigos
        mergedObservation = environment.getMergedObservationZZ(2, 2);
        // Obtenemos información sobre Mario
        isMarioOnGround = environment.isMarioOnGround();
        isMarioAbleToJump = environment.isMarioAbleToJump();
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

        // Mario siempre tiene que ir hacia la derecha
        action[Mario.KEY_RIGHT] = true;
        // Si el salto está permitido compruebo si tengo enemigos u obstáculos cerca (2 casillas distancia)
        if (isMarioAbleToJump) {
            for (int i = 8; i < 10; i++) {
                for (int j = 10; j < 12; j++) {
                    if (mergedObservation[i][j] == -60 || mergedObservation[i][j] == 1)
                        // Entonces los salto
                        action[Mario.KEY_JUMP] = true;
                }
            }
//            // Nuevo (después entrega T3) Saltar fosos/acantilados y evitar caer al vacío
//            // Comprobamos la fila 12, 3 casillas por debajo de la horizontal de los pies de Mario
//            if (mergedObservation[12][10] == 0)
//                // Si no hay obstáculo (suelo), quizás es un foso/acantilado que debamos saltar
//                action[Mario.KEY_JUMP] = true;
        }
        // Si el salto está bloqueado y está la tecla de saltar presionada (se bloqueó el salto tras la primera pulsación)
        else if (action[Mario.KEY_JUMP]) {
            // "Canelamos" el salto mientras comprobamos si hemos pasado ya el obstáculo o no
            action[Mario.KEY_JUMP] = false;
            for (int i = 8; i < 11; i++) {
                for (int j = 10; j < 12; j++) {
                    if (mergedObservation[i][j] == -60 || mergedObservation[i][j] == 1)
                        // Si no lo hemos pasado, seguimos saltando
                        action[Mario.KEY_JUMP] = true;
                }
            }
//            // O en su defecto, comprobamos si hemos pasado el foso/acantilado o no
//            if (mergedObservation[11][10] == 0)
//                action[Mario.KEY_JUMP] = true;
            // Una vez en tierra, si el salto está bloqueado es porque todavía no hemos soltado
            // la tecla de saltar desde que la pulsamos originalmente para el anterior salto
            if (isMarioOnGround)
                // Así que la soltamos para estar preparados para el próximo salto que necesitemos
                action[Mario.KEY_JUMP] = false;
        }


        return action;
    }
}
