package ch.idsia.agents.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.ArrayList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.tools.Instance;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class P2BotAgent extends BasicMarioAIAgent implements Agent {

    int tick;
    private Random R = null;
    private byte jumpCounter, atascado;
    private int lastTickIntermediateReward, lastTickKillsTotal, lastTickCoinsGained, lastCellPosition_X;
    
    // Clases (situaciones) a las que pueden pertenecer las instancias
    private ArrayList <Instance> coinsNearClass;
    private ArrayList <Instance> enemiesNearClass;
    private ArrayList <Instance> coinsEnemiesClass;
    private ArrayList <Instance> marioOnAirClass;
    private ArrayList <Instance> defaultClass;

    // Instancia del tick actual del juego
    private Instance tickInstance;

    public P2BotAgent() {
        super("P2BotAgent");
        reset();
        jumpCounter = atascado = 0;
        lastCellPosition_X = 0;
        coinsNearClass = new ArrayList <Instance>();
        enemiesNearClass = new ArrayList <Instance>();
        coinsEnemiesClass = new ArrayList <Instance>();
        marioOnAirClass = new ArrayList <Instance>();
        defaultClass = new ArrayList <Instance>();
        loadKnowledgeBase();
    }

    public void reset() {
        // Dummy reset, of course, but meet formalities!
        R = new Random();
    }

    private void loadKnowledgeBase() {

        System.out.println("Loading knowledge base...");

        String csvFile = "weka/ejemplos_entrenamiento/knowledge_base.csv";
        String line = "\n";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int j = 0;

            while ((line = br.readLine()) != null) {

                String[] instance = line.split(cvsSplitBy);

                Instance in = new Instance(
                    Short.parseShort(instance[0]),
                    Short.parseShort(instance[1]),
                    Short.parseShort(instance[2]),
                    Short.parseShort(instance[3]),
                    Short.parseShort(instance[4]),
                    Short.parseShort(instance[5]),
                    Short.parseShort(instance[6]),
                    Short.parseShort(instance[7]),
                    Float.parseFloat(instance[8]),
                    Byte.parseByte(instance[9]),
                    Byte.parseByte(instance[10]),
                    Float.parseFloat(instance[11]),
                    Byte.parseByte(instance[12]),
                    Byte.parseByte(instance[13]),
                    Float.parseFloat(instance[14]),
                    Byte.parseByte(instance[15]),
                    Byte.parseByte(instance[16]),
                    Float.parseFloat(instance[17]),
                    Byte.parseByte(instance[18]),
                    Byte.parseByte(instance[19]),
                    Float.parseFloat(instance[20]),
                    Byte.parseByte(instance[21]),
                    Byte.parseByte(instance[22]),
                    Float.parseFloat(instance[23]),
                    Byte.parseByte(instance[24]),
                    Byte.parseByte(instance[25]),
                    Short.parseShort(instance[26]),
                    Short.parseShort(instance[27]),
                    Byte.parseByte(instance[28]),
                    Byte.parseByte(instance[29]),
                    Byte.parseByte(instance[30]),
                    Byte.parseByte(instance[31]),
                    Byte.parseByte(instance[32]),
                    Byte.parseByte(instance[33]),
                    Byte.parseByte(instance[34]),
                    Byte.parseByte(instance[35]),
                    Byte.parseByte(instance[36]),
                    Byte.parseByte(instance[37]),
                    Byte.parseByte(instance[38]),
                    Byte.parseByte(instance[39]),
                    Byte.parseByte(instance[40]),
                    Byte.parseByte(instance[41]),
                    Byte.parseByte(instance[42]),
                    Byte.parseByte(instance[43])
                );
                j++;
                addToKnowledgeBase(in);
            }
            float coinsNearClassPc = ((float)(coinsNearClass.size()) / (float)(j)) * 100.0f;
            float enemiesNearClassPc = ((float)(enemiesNearClass.size()) / (float)(j)) * 100.0f;
            float coinsEnemiesClassPc = ((float)(coinsEnemiesClass.size()) / (float)(j)) * 100.0f;
            float marioOnAirClassPc = ((float)(marioOnAirClass.size()) / (float)(j)) * 100.0f;
            float defaultClassPc = ((float)(defaultClass.size()) / (float)(j)) * 100.0f;
            DecimalFormat df = new DecimalFormat("##.000", DecimalFormatSymbols.getInstance(Locale.US));
            System.out.println("Knowledge base loaded succesfully: " + j + " instances");
            System.out.println("\tcoinsNearClass: " + coinsNearClass.size() + " instances (" + df.format(coinsNearClassPc) + "%)");
            System.out.println("\tenemiesNearClass: " + enemiesNearClass.size() + " instances (" + df.format(enemiesNearClassPc) + "%)");
            System.out.println("\tcoinsEnemiesClass: " + coinsEnemiesClass.size() + " instances (" + df.format(coinsEnemiesClassPc) + "%)");
            System.out.println("\tmarioOnAirClass: " + marioOnAirClass.size() + " instances (" + df.format(marioOnAirClassPc) + "%)");
            System.out.println("\tdefaultClass: " + defaultClass.size() + " instances (" + df.format(defaultClassPc) + "%)");

        } catch (Exception e) {
            System.err.println("Knowledge base loaded unsuccesfully");
            e.printStackTrace();
        }
        
    }

    /**
     * Esta función añade las instancias a la base de conocimiento, clasificadas por situaciones
     */
    private void addToKnowledgeBase(Instance i) {
        /*
        Situaciones:
        1: Hay monedas cerca de Mario y no hay enemigos (en cualquier posición de la matriz de observación)
        2: Hay enemigos cerca de Mario y no hay monedas (en cualquier posición de la matriz de observación)
        3: Hay enemigos cerca de Mario y hay monedas (en cualquier posición de la matriz de observación)
        4: Mario está en el aire
        5: Default (aquí entra cualquier otra, incluida hay obstáculo cerca )
        */
        switch (classify(i)) {
            case 1:
                coinsNearClass.add(i);
                break;
            case 2:
                enemiesNearClass.add(i);
                break;
            case 3:
                coinsEnemiesClass.add(i);
                break;
            case 4:
                marioOnAirClass.add(i);
                break;
            default: // 5
                defaultClass.add(i);
        }
    }

    /**
     * Esta función clasifica la situación de la instancia del tick actual
     * @return valor byte que identifica el arraylist (situación) a iterar en la función de evaluación
     */
    private byte classify(Instance i) {
        /*
        Situaciones:
        1: Hay monedas cerca de Mario y no hay enemigos (en cualquier posición de la matriz de observación)
        2: Hay enemigos cerca de Mario y no hay monedas (en cualquier posición de la matriz de observación)
        3: Hay enemigos cerca de Mario y hay monedas (en cualquier posición de la matriz de observación)
        4: Mario está en el aire
        5: Default (aquí entra cualquier otra, incluida hay obstáculo cerca )
        */
        if (i.isMarioOnGround == 0)
            return 4;
        if (i.numCoinsObserved > 0 && i.numEnemiesObserved <= 0)
            return 1;
        if (i.numCoinsObserved <= 0 && i.numEnemiesObserved > 0)
            return 2;
        if (i.numCoinsObserved > 0 && i.numEnemiesObserved > 0)
            return 3;
        return 5;
    }

    /**
     * Esta función implementa la función matemática de similitud, una euclídea con pesos
     * @return array con las N (20) instancias de la base de conocimiento más similares a la del tick actual
     */
    private Instance[] fSimilarity(Instance ia, byte situation) {
        // Declaracion de variables
        ArrayList <Instance> auxA;
        Instance[] mostSimilarInstances = new Instance[20];
        float[] mostSimilarInstancesDist = new float[20];
        float dist;

        // Inicializamos los floats al máximo valor posible para luego comparar menor distancia
        for (byte i = 0; i < mostSimilarInstancesDist.length; i++)
            mostSimilarInstancesDist[i] = Float.MAX_VALUE;
        
            // Identificamos la situación de la instanci actual y recorremos solo esa clase de instancias de la base de conocimiento
        switch (situation){
            case 1:
                auxA = coinsNearClass;
                break;
            case 2:
                auxA = enemiesNearClass;
                break;
            case 3:
                auxA = coinsEnemiesClass;
                break;
            case 4:
                auxA = marioOnAirClass;
                break;
            default: // 5
                auxA = defaultClass;
        }

        for (Instance ib:auxA) { // Todos tienen peso 1
            // Función matemática de similitud, euclídea con pesos
            dist =
                Math.abs(ia.nearestEnemyLeftDistance - ib.nearestEnemyLeftDistance) +
                Math.abs(ia.nearestEnemyLeft_X - ib.nearestEnemyLeft_X) +
                Math.abs(ia.nearestEnemyLeft_Y - ib.nearestEnemyLeft_Y) +
                Math.abs(ia.nearestEnemyRightDistance - ib.nearestEnemyRightDistance) +
                Math.abs(ia.nearestEnemyRight_X - ib.nearestEnemyRight_X) +
                Math.abs(ia.nearestEnemyRight_Y - ib.nearestEnemyRight_Y) +
                Math.abs(ia.nearestBlockLeftDistance - ib.nearestBlockLeftDistance) +
                Math.abs(ia.nearestBlockLeft_X - ib.nearestBlockLeft_X) +
                Math.abs(ia.nearestBlockLeft_Y - ib.nearestBlockLeft_Y) +
                Math.abs(ia.nearestBlockRightDistance - ib.nearestBlockRightDistance) +
                Math.abs(ia.nearestBlockRight_Y - ib.nearestBlockRight_Y) +
                Math.abs(ia.nearestBlockRight_X - ib.nearestBlockRight_X) +
                Math.abs(ia.nearestCoinLeftDistance - ib.nearestCoinLeftDistance) +
                Math.abs(ia.nearestCoinLeft_X - ib.nearestCoinLeft_X) +
                Math.abs(ia.nearestCoinLeft_Y - ib.nearestCoinLeft_Y) +
                Math.abs(ia.nearestCoinRightDistance - ib.nearestCoinRightDistance) +
                Math.abs(ia.nearestCoinRight_X - ib.nearestCoinRight_X) +
                Math.abs(ia.nearestCoinRight_Y - ib.nearestCoinRight_Y) +
                Math.abs(ia.numEnemiesObserved - ib.numEnemiesObserved) +
                Math.abs(ia.numCoinsObserved - ib.numCoinsObserved) +
                Math.abs(ia.enemyNearRight - ib.enemyNearRight) +
                Math.abs(ia.blockNearRight - ib.blockNearRight) +
                Math.abs(ia.enemyAheadOnFloorHeight - ib.enemyAheadOnFloorHeight) +
                Math.abs(ia.blockAheadOnFloorHeight - ib.blockAheadOnFloorHeight) +
                Math.abs(ia.abyssAhead - ib.abyssAhead) +
                Math.abs(ia.isMarioOnGround - ib.isMarioOnGround) +
                Math.abs(ia.isMarioAbleToJump - ib.isMarioAbleToJump) +
                Math.abs(ia.isMarioAbleToShoot - ib.isMarioAbleToShoot) +
                Math.abs(ia.isMarioCarrying - ib.isMarioCarrying) +
                Math.abs(ia.enemyWasKilledBin - ib.enemyWasKilledBin) +
                Math.abs(ia.marioWasInjuredBin - ib.marioWasInjuredBin) +
                Math.abs(ia.isSlopeDown - ib.isSlopeDown) +
                Math.abs(ia.marioMode - ib.marioMode) +
                Math.abs(ia.marioStatus - ib.marioStatus);

            for (byte i = 0; i < mostSimilarInstancesDist.length; i++) {
                // Añadimos a nuestra selección de las 20 instancias más parecidas aquellas con menor distancia
                if (dist < mostSimilarInstancesDist[i]) {
                    mostSimilarInstancesDist[i] = dist;
                    mostSimilarInstances[i] = ib;
                    break;
                }
                // Si entre nuestras más parecidas hay dos instancias con la misma distancia, priorizamos aquellas
                // en las que Mario salta para minimizar las situaciones en las que se queda atascado contra muros
                if (dist == mostSimilarInstancesDist[i] && (ib.actionKey == 1 || ib.actionKey == 3)) {
                    mostSimilarInstancesDist[i] = dist;
                    mostSimilarInstances[i] = ib;
                    break;
                }
            }
        }
        return mostSimilarInstances;
    }

    /**
     * Esta función implementa la función matemática de evaluación, una euclídea con pesos
     * Establece la situaciones a ejecutar en el array de acciones
     */
    private byte fEvaluation(Instance[] mostSimilarInstances) {
        float ev, bestEv = 0.0f, secondBestEv = 0.0f, actionWeight = 0.0f;
        byte bestInst = 0, secondBestInst = 0;

        for (byte i = 0; i < mostSimilarInstances.length; i++) {
            if (mostSimilarInstances[i] != null) {
                switch(mostSimilarInstances[i].actionKey) {
                    // Nota, por como hemos tomado las acciones de ejemplo que conforman nuestra base de conocimiento (ver memoria), no deberían nunca
                    // llegar instancias con acciones que incluyan la acción Speed (tecla S: Velocidad/Disparar), por lo que sus cases del switch son despreciables
                    case 0: // None
                        actionWeight = 0.0f;
                        break;
                    case 1: // Jump
                        actionWeight = 100.0f;
                        break;
                    case 2: // Right
                        actionWeight = 20.0f;
                        break;
                    case 3: // Right-Jump
                        actionWeight = 600.0f * mostSimilarInstances[i].timeSpent;
                        break;
                    case 4: // Right-Speed
                        actionWeight = 20.0f;
                        break;
                    case 5: // Right-Jump-Speed
                        actionWeight = 600.0f;
                        break;
                    case 6: // Left
                        actionWeight = -10.0f * mostSimilarInstances[i].timeSpent + 10000 * ((atascado > 10)? 1:0)* mostSimilarInstances[i].timeSpent;
                        break;
                    case 7: // Left-Jump
                        actionWeight = -10.0f * mostSimilarInstances[i].timeSpent + 10000 * ((atascado > 10)? 1:0)* mostSimilarInstances[i].timeSpent;
                        break;
                    case 8: // Left-Speed
                        actionWeight = -10.0f * mostSimilarInstances[i].timeSpent + 10000 * ((atascado > 10)? 1:0) * mostSimilarInstances[i].timeSpent;
                        break;
                    case 9: // Left-Jump-Speed
                        actionWeight = -10.0f * mostSimilarInstances[i].timeSpent + 10000 * ((atascado > 10)? 1:0)* mostSimilarInstances[i].timeSpent;
                }
                // Función matemática de evaluación, heurística con pesos
                ev =
                    (0.01f * mostSimilarInstances[i].intermediaRewardWonFuture6Ticks) +
                    (0.02f * mostSimilarInstances[i].intermediaRewardWonFuture12Ticks) +
                    (0.04f * mostSimilarInstances[i].intermediaRewardWonFuture24Ticks) +
                    actionWeight;
                
                // Comprobamos si la evaluación de la instancia actual ha resultado mejor que la mejor evaluación (mejor instancia)
                // que habíamos calculado hasta ahora para este set de instancias similares
                if (ev > bestEv) {
                    // Calculamos una segunda mejor instancia que cuya acción ejecutaremos si detectamos que Mario se queda atascado
                    if (bestEv != 0) {
                        secondBestEv = bestEv;
                        secondBestInst = bestInst;
                    }
                    bestEv = ev;
                    bestInst = i;
                }
            }
        }
        if (atascado < 24) // Si Mario no está atascado, ejecutar la acción de la instancia de la base de conocimiento con mayor evaluación
            return mostSimilarInstances[bestInst].actionKey;
        // Si Mario está atascado por al menos 1 segundo (24 ticks), ejecutar la acción de la instancia de la base de conocimiento con segunda mayor evaluación
        return mostSimilarInstances[secondBestInst].actionKey;
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
        switch(fEvaluation(fSimilarity(tickInstance, classify(tickInstance)))){
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

        // Para evitar quedarnos atascados contra muros
        if (atascado >= 48) { // Si Mario lleva más de 2 segundos (48 ticks) en la misma posición X
            // Intentamos saltar e ir a la derecha
            action[Mario.KEY_RIGHT] = true;
            action[Mario.KEY_JUMP] = true;
            atascado = 0;
        }
        return action;
    }
}
