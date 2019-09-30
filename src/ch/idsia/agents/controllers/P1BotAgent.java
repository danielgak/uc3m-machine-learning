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

public class P1BotAgent extends BasicMarioAIAgent implements Agent {

    int tick;
    private Random R = null;
    private TrainingFile tf;

    public P1BotAgent() {
        super("P1BotAgent");
        reset();
        tick = 0;
        tf = new TrainingFile("P1BotAgent", (byte)1, false, false, false);
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
        tf.writeExample(environment, tick, action);

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
