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

import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.tools.TrainingFile;

public class P1J48BotAgent extends BasicMarioAIAgent implements Agent {

    int tick;
    private Random R = null;
    private TrainingFile tf;
    private final byte MARIO_MERGE_POS = 9;
    private final double MAX_DISTANCE = Math.sqrt(162);
    private double nearestBlockLeftDistance, nearestBlockRightDistance;
    private double nearestEnemyLeftDistance, nearestEnemyRightDistance;
    private double nearestCoinLeftDistance, nearestCoinRightDistance;
    private int nearestBlockLeft_X, nearestBlockLeft_Y, nearestBlockRight_X, nearestBlockRight_Y;
    private int nearestEnemyLeft_X, nearestEnemyLeft_Y, nearestEnemyRight_X, nearestEnemyRight_Y;
    private int nearestCoinLeft_X, nearestCoinLeft_Y, nearestCoinRight_X, nearestCoinRight_Y;
    private byte blockNearRight, enemyNearRight, blockAheadOnFloorHeight, isSlopeDown, enemyAheadOnFloorHeight, isMarioOnGroundAbleToJump;
    private int jumpCounter;

    public P1J48BotAgent() {
        super("P1J48BotAgent");
        reset();
        tick = jumpCounter= 0;
        blockNearRight = enemyNearRight = blockAheadOnFloorHeight = enemyAheadOnFloorHeight = isSlopeDown = 0;
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
        // tf.writeExample(environment, tick, action, false);

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
        // System.out.println("######### Tick: " + tick + " #########");
        nearestEnemyLeftDistance = MAX_DISTANCE; nearestEnemyRightDistance = MAX_DISTANCE;
        nearestEnemyLeft_X = nearestEnemyLeft_Y = 0;
        nearestEnemyRight_X = nearestEnemyRight_Y = 18;
        nearestBlockLeftDistance = MAX_DISTANCE; nearestBlockRightDistance = MAX_DISTANCE;
        nearestBlockLeft_X = nearestBlockLeft_Y = 0;
        nearestBlockRight_X = nearestBlockRight_Y = 18;
        nearestCoinLeftDistance = MAX_DISTANCE; nearestCoinRightDistance = MAX_DISTANCE;
        nearestCoinLeft_X = nearestCoinLeft_Y = 0;
        nearestCoinRight_X = nearestCoinRight_Y = 18;
//        isMarioOnGroundAbleToJump = (byte)(((environment.isMarioAbleToJump())? 1: 0) + ((environment.isMarioOnGround())? 1: 0) - 1);
//        System.out.println("isMarioAbleToJump: "+environment.isMarioAbleToJump()+", isMarioOnGround: "+environment.isMarioOnGround()+", isMarioOnGroundAbleToJump: "+isMarioOnGroundAbleToJump);
        byte mergeObs[][] = environment.getMergedObservationZZ(1,1);
        double aux;
        for (byte i = 0; i < mergeObs.length; i++) {
            for (byte j = 0; j < mergeObs[i].length; j++) {
                if ((mergeObs[i][j] == -24 || mergeObs[i][j] == -60 || mergeObs[i][j] == -62 || mergeObs[i][j] == -85) && i <= MARIO_MERGE_POS) {
                    // Obstáculo (bloque/ladrillo)
                    // Solo tenemos en cuenta los que están a la altura de los pies de Mario o por encima
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestBlockLeftDistance) {
                            nearestBlockLeftDistance = aux;
                            nearestBlockLeft_X = j;
                            nearestBlockLeft_Y = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestBlockRightDistance) {
                            nearestBlockRightDistance = aux;
                            nearestBlockRight_X = j;
                            nearestBlockRight_Y = i;
                        }
                    }
                } else if (mergeObs[i][j] == 2) {
                    // Moneda
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestCoinLeftDistance) {
                            nearestCoinLeftDistance = aux;
                            nearestCoinLeft_X = j;
                            nearestCoinLeft_Y = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestCoinRightDistance) {
                            nearestCoinRightDistance = aux;
                            nearestCoinRight_X = j;
                            nearestCoinRight_Y = i;
                        }
                    }
                } else if (mergeObs[i][j] == 80) {
                    // Enemigo
                    aux = Math.sqrt(Math.pow(i-MARIO_MERGE_POS, 2) + Math.pow(j-MARIO_MERGE_POS, 2));
                    if (j <= MARIO_MERGE_POS) {
                        // A la izquierda
                        if (aux <= nearestEnemyLeftDistance) {
                            nearestEnemyLeftDistance = aux;
                            nearestEnemyLeft_X = j;
                            nearestEnemyLeft_Y = i;
                        }
                    } else {
                        // A la derecha
                        if (aux <= nearestEnemyRightDistance) {
                            nearestEnemyRightDistance = aux;
                            nearestEnemyRight_X = j;
                            nearestEnemyRight_Y = i;
                        }
                    }
                }
            }
        }
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
                        if (this.isMarioOnGround) {
                            isSlopeDown = 1;
                        }
                    }
                }
            }
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
        // Árbol j48:
        // ***************************** Primer Arbol hecho por Gak  *****************************
        // isMarioAbleToJump = 0
        // |   nearestBlockRightDistance <= 2
        // |   |   nearestBlockLeftDistance <= 1.414: 3 (772.0/196.0)
        // |   |   nearestBlockLeftDistance > 1.414
        // |   |   |   isMarioOnGround = 0
        // |   |   |   |   nearestEnemyLeftDistance <= 2.828: 2 (725.0/262.0)
        // |   |   |   |   nearestEnemyLeftDistance > 2.828
        // |   |   |   |   |   nearestEnemyLef_X <= 8: 3 (938.0/305.0)
        // |   |   |   |   |   nearestEnemyLef_X > 8: 2 (346.0/166.0)
        // |   |   |   isMarioOnGround = 1: 2 (290.0/56.0)
        // |   nearestBlockRightDistance > 2
        // |   |   enemyNearRight = 0
        // |   |   |   isMarioOnGround = 0
        // |   |   |   |   nearestBlockLeftDistance <= 1: 2 (553.0/35.0)
        // |   |   |   |   nearestBlockLeftDistance > 1
        // |   |   |   |   |   enemyAheadOnFloorHeight = 0
        // |   |   |   |   |   |   blockAheadOnFloorHeight = 0: 2 (7011.0/1225.0)
        // |   |   |   |   |   |   blockAheadOnFloorHeight = 1
        // |   |   |   |   |   |   |   nearestEnemyLeft_Y <= 12
        // |   |   |   |   |   |   |   |   nearestBlockRightDistance <= 2.828: 3 (208.0/101.0)
        // |   |   |   |   |   |   |   |   nearestBlockRightDistance > 2.828: 2 (2319.0/690.0)
        // |   |   |   |   |   |   |   nearestEnemyLeft_Y > 12: 3 (387.0/169.0)
        // |   |   |   |   |   enemyAheadOnFloorHeight = 1
        // |   |   |   |   |   |   nearestEnemyLef_X <= 6
        // |   |   |   |   |   |   |   nearestBlockRight_Y <= 6: 2 (209.0/97.0)
        // |   |   |   |   |   |   |   nearestBlockRight_Y > 6: 3 (233.0/94.0)
        // |   |   |   |   |   |   nearestEnemyLef_X > 6: 2 (235.0/68.0)
        // |   |   |   isMarioOnGround = 1: 2 (1131.0/79.0)
        // |   |   enemyNearRight = 1
        // |   |   |   nearestEnemyLef_X <= 5: 3 (394.0/154.0)
        // |   |   |   nearestEnemyLef_X > 5: 2 (213.0/85.0)
        // isMarioAbleToJump = 1: 2 (8965.0/238.0)
        
        // ***************************** Primer Arbol hecho por Gak  *****************************

        // if(!this.isMarioAbleToJump){
        //     if(this.nearestBlockRightDistance <=2){
        //         if(this.nearestBlockLeftDistance <= 1.414){
        //             action[Mario.KEY_JUMP]= true;
        //             action[Mario.KEY_RIGHT] = true;
        //         }else{
        //             if(!this.isMarioOnGround){
        //                 if(this.nearestEnemyLeftDistance <= 2.82){
        //                     action[Mario.KEY_RIGHT] = true;
        //                 }else{
        //                     if(this.nearestEnemyLeft_X <= 8){
        //                         action[Mario.KEY_JUMP]= true;
        //                         action[Mario.KEY_RIGHT] = true;
        //                     }else{
        //                         action[Mario.KEY_RIGHT] = true;
        //                     }
        //                 }
        //             }else{
        //                 action[Mario.KEY_RIGHT] = true;
        //             }
        //         }
        //     }else{
        //         if(this.enemyNearRight==0){
        //             if(!this.isMarioOnGround){
        //                 if(this.nearestBlockLeftDistance <=1){
        //                     action[Mario.KEY_RIGHT] = true;
        //                 }else{
        //                     if(this.enemyAheadOnFloorHeight ==0){
        //                         if(this.blockAheadOnFloorHeight == 0){
        //                             action[Mario.KEY_RIGHT] = true;
        //                         }else{
        //                             if(this.nearestEnemyLeft_Y <= 12){
        //                                 if(this.nearestBlockRightDistance <= 2.828){
        //                                     action[Mario.KEY_JUMP]= true;
        //                                     action[Mario.KEY_RIGHT] = true;
        //                                 }else{
        //                                     action[Mario.KEY_RIGHT] = true;
        //                                 }
        //                             }else{
        //                                 action[Mario.KEY_JUMP]= true;
        //                                 action[Mario.KEY_RIGHT] = true;
        //                             }
        //                         }
        //                     }else{
        //                         if(this.nearestBlockLeft_X <= 6){
        //                             if(this.nearestBlockRight_Y <=6){
        //                                 action[Mario.KEY_RIGHT] = true;
        //                             }else{
        //                                 action[Mario.KEY_JUMP]= true;
        //                                 action[Mario.KEY_RIGHT] = true;
        //                             }
        //                         }else{
        //                             action[Mario.KEY_RIGHT] = true;
        //                         }
        //                     }
        //                 }
        //             }else{
        //                 action[Mario.KEY_RIGHT] = true;
        //             }
        //         }else{
        //             if(this.nearestEnemyLeft_X <= 5){
        //                 action[Mario.KEY_JUMP]= true;
        //                 action[Mario.KEY_RIGHT] = true;
        //             }else{
        //                 action[Mario.KEY_RIGHT] = true;
        //             }
        //         }
        //     }
        // }else{
        //     action[Mario.KEY_RIGHT] = true;
        // }

        // ***************************** Segundo Arbol hecho por Aitor *****************************
        // isMarioAbleToJump = 0
        // |   nearestBlockRightDistance <= 2
        // |   |   nearestBlockLeft_X <= 7
        // |   |   |   isMarioOnGround = 0
        // |   |   |   |   nearestEnemyLeftDistance <= 2.828: 2 (596.0/204.0)
        // |   |   |   |   nearestEnemyLeftDistance > 2.828
        // |   |   |   |   |   nearestEnemyLef_X <= 3
        // |   |   |   |   |   |   nearestBlockRightDistance <= 1: 3 (401.0/158.0)
        // |   |   |   |   |   |   nearestBlockRightDistance > 1: 2 (215.0/92.0)
        // |   |   |   |   |   nearestEnemyLef_X > 3: 3 (461.0/149.0)
        // |   |   |   isMarioOnGround = 1: 2 (249.0/51.0)
        // |   |   nearestBlockLeft_X > 7: 3 (1066.0/298.0)
        // |   nearestBlockRightDistance > 2
        // |   |   enemyNearRight = 0
        // |   |   |   isMarioOnGround = 0
        // |   |   |   |   blockAheadOnFloorHeight = 0
        // |   |   |   |   |   enemyAheadOnFloorHeight = 0: 2 (7248.0/1377.0)
        // |   |   |   |   |   enemyAheadOnFloorHeight = 1
        // |   |   |   |   |   |   nearestBlockLeft_Y <= 8: 3 (345.0/159.0)
        // |   |   |   |   |   |   nearestBlockLeft_Y > 8: 2 (232.0/60.0)
        // |   |   |   |   blockAheadOnFloorHeight = 1
        // |   |   |   |   |   nearestEnemyLeft_Y <= 12
        // |   |   |   |   |   |   nearestBlockRightDistance <= 2.828: 3 (216.0/95.0)
        // |   |   |   |   |   |   nearestBlockRightDistance > 2.828: 2 (2734.0/775.0)
        // |   |   |   |   |   nearestEnemyLeft_Y > 12: 3 (311.0/118.0)
        // |   |   |   isMarioOnGround = 1: 2 (1116.0/87.0)
        // |   |   enemyNearRight = 1
        // |   |   |   nearestBlockLeft_X <= 7
        // |   |   |   |   nearestBlockLeftDistance <= 3: 2 (205.0/87.0)
        // |   |   |   |   nearestBlockLeftDistance > 3: 3 (220.0/104.0)
        // |   |   |   nearestBlockLeft_X > 7: 3 (207.0/77.0)
        // isMarioAbleToJump = 1: 2 (9017.0/267.0)

        // ***************************** Segundo Arbol hecho por Aitor *****************************
        if (!this.isMarioAbleToJump) {
            if (nearestBlockRightDistance <= 2) {
                if (nearestBlockLeft_X <= 7) {
                    if (!this.isMarioOnGround) {
                        if (nearestEnemyLeftDistance <= 2.828) {
                            action[Mario.KEY_RIGHT] = true;
                        } else {
                            if (nearestEnemyLeft_X <= 3) {
                                if (nearestBlockRightDistance <= 1) {
                                    action[Mario.KEY_RIGHT] = true;
                                    action[Mario.KEY_JUMP] = true;
                                } else {
                                    action[Mario.KEY_RIGHT] = true;
                                }
                            } else {
                                action[Mario.KEY_RIGHT] = true;
                                action[Mario.KEY_JUMP] = true;
                            }
                        }
                    } else {
                        action[Mario.KEY_RIGHT] = true;
                    }
                } else {
                    action[Mario.KEY_RIGHT] = true;
                    action[Mario.KEY_JUMP] = true;
                }
            } else {
                if (enemyNearRight == 0) {
                    if (!this.isMarioOnGround) {
                        if (blockAheadOnFloorHeight == 0) {
                            if (enemyAheadOnFloorHeight == 0) {
                                action[Mario.KEY_RIGHT] = true;
                            } else {
                                if (nearestBlockLeft_Y <= 8) {
                                    action[Mario.KEY_RIGHT] = true;
                                    action[Mario.KEY_JUMP] = true;
                                } else {
                                    action[Mario.KEY_RIGHT] = true;
                                }
                            }
                        } else {
                            if (nearestEnemyLeft_Y <= 12) {
                                if (nearestBlockRightDistance <= 2.828) {
                                    action[Mario.KEY_RIGHT] = true;
                                    action[Mario.KEY_JUMP] = true;
                                } else {
                                    action[Mario.KEY_RIGHT] = true;
                                }
                            } else {
                                action[Mario.KEY_RIGHT] = true;
                                action[Mario.KEY_JUMP] = true;
                            }
                        }
                    } else {
                        action[Mario.KEY_RIGHT] = true;
                    }
                } else {
                    if (nearestBlockLeft_X <= 7) {
                        if (nearestBlockLeftDistance <= 3) {
                            action[Mario.KEY_RIGHT] = true;
                        } else {
                            action[Mario.KEY_RIGHT] = true;
                            action[Mario.KEY_JUMP] = true;
                        }
                    } else {
                        action[Mario.KEY_RIGHT] = true;
                        action[Mario.KEY_JUMP] = true;
                    }
                }
            }
        } else {
            action[Mario.KEY_RIGHT] = true;
        }

        // Para desbloquear salto
        if (action[Mario.KEY_JUMP]) {
            jumpCounter++;
            if (jumpCounter >= 10){
                if (!this.isMarioOnGround) {
                    action[Mario.KEY_JUMP] = false;
                    jumpCounter = 0;
                }
            }
        } else jumpCounter = 0;

        return action;
    }
}