package ch.idsia.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
// import ch.idsia.tools.TickInfo;
/**
 * Esta clase sirve de manejador del fichero de instancias de entrenamiento.
 */
public class TrainingFile {

    // Constantes
    private final int UPDATE_TICKS = 5;
    private final double MAX_DISTANCE = Math.sqrt(162);
    private final byte MARIO_MERGE_POS = 9;

    // Variables globales
    private String agentNameLowerCase;
    private File tf;
    private FileWriter tfw;
    private PrintWriter pw;
    private boolean marioCanGoBack, captureJumpLonely, captureStay;
    private byte frecuencia, atascado;
    private int lastTickIntermediateReward, lastTickCoinsGained, lastTickKillsTotal, lastCellPosition_X;
    private int[] last6ticksIntermediateReward;
//    private int[] last24ticksIntermediateReward;
//    private boolean[] lastTickKeys;
    private DecimalFormat df;

    private ArrayList<String[]> last12ticksInstances;
    private ArrayList<Integer> last12ticksReward;
    private ArrayList<Integer> last12ticksPosition;

    public TrainingFile(String agentName, byte frecuencia, boolean marioCanGoBack, boolean captureJumpLoney, boolean captureStay) {
        this.agentNameLowerCase = agentName.toLowerCase();
        this.frecuencia = frecuencia;
        this.marioCanGoBack = marioCanGoBack;
        this.captureJumpLonely = captureJumpLoney;
        this.captureStay = captureStay;
        lastTickIntermediateReward = 0;
        lastTickCoinsGained = 0;
        lastTickKillsTotal = 0;
        atascado = 0;
        lastCellPosition_X = 0;
        last6ticksIntermediateReward = new int[6];

        last12ticksInstances = new ArrayList<String[]>();
        last12ticksReward = new ArrayList<Integer>();
        last12ticksPosition = new ArrayList<Integer>();

        df = new DecimalFormat("##.000", DecimalFormatSymbols.getInstance(Locale.US));
        tf = new File("weka/ejemplos_entrenamiento/" + agentName + "_" + frecuencia + ".arff");

        if (!tf.exists()) {
            // Si el fichero no existe le añadimos la cabecera de weka
            System.out.println("TrainigFile.java: File weka/ejemplos_entrenamiento/" + agentName +
                                "_" + frecuencia + ".arff doesn't exist. Generating .arff header.");
            // Comprobamos si existe la ruta, en caso contrario la creamos
            File dir = new File("weka/ejemplos_entrenamiento/");
            if (!dir.exists())
                dir.mkdirs();
            // Abrimos el fichero para escribir en él
            try {
                tfw = new FileWriter(tf, true);
                pw = new PrintWriter(tfw);
            } catch (Exception e) {

                e.printStackTrace();
            }
            // // Escribimos la cabecera de los ficheros .arff de weka
            // String attrMergeObs = "";
            // for (int i = 0; i < 19; i++)
            //     for (int j = 0; j < 19; j++)
            //         attrMergeObs += "  @ATTRIBUTE mergeObservation_" + i + "_" + j + " NUMERIC\n";
            // int N = 6;
            // String attrFuture = "";
            // while (N <= 24) {
            //     attrFuture += "  @ATTRIBUTE coinsFuture" + N + "Ticks NUMERIC\n  @ATTRIBUTE killsFuture" + N + "Ticks NUMERIC\n";
            //     N *= 2;
            // }
            String timeStamp = new SimpleDateFormat("ddMM_HHmmss").format(Calendar.getInstance().getTime());
            String ticksAttr = "", clasifierClass = "", ticksAttr12 = "", clasifierClass12 = "";
            if (frecuencia == 1) {
                ticksAttr =
                "  @ATTRIBUTE intermediateReward NUMERIC\n" +
                "  @ATTRIBUTE intermediateRewardWonLastTick NUMERIC\n" +
                "  @ATTRIBUTE intermediateRewardWonLast6Ticks NUMERIC\n" +
                "  @ATTRIBUTE intermediaRewardWonFuture6Ticks NUMERIC\n";
                ticksAttr12 =
                "  @ATTRIBUTE intermediateReward12 NUMERIC\n" +
                "  @ATTRIBUTE intermediateRewardWonLastTick12 NUMERIC\n" +
                "  @ATTRIBUTE intermediateRewardWonLast6Ticks12 NUMERIC\n";
            }
            if (marioCanGoBack && captureJumpLonely && captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // N, J, R, RJ, RS, RJS, L, LJ, LS, LJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // N, J, R, RJ, RS, RJS, L, LJ, LS, LJS
            }
            else if (marioCanGoBack && captureJumpLonely && !captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {1, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // J, R, RJ, RS, RJS, L, LJ, LS, LJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {1, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // J, R, RJ, RS, RJS, L, LJ, LS, LJS
            }
            else if (marioCanGoBack && !captureJumpLonely && captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {0, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // N, R, RJ, RS, RJS, L, LJ, LS, LJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {0, 2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // N, R, RJ, RS, RJS, L, LJ, LS, LJS
            }
            else if (marioCanGoBack && !captureJumpLonely && !captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // R, RJ, RS, RJS, L, LJ, LS, LJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {2, 3, 4, 5, 6, 7, 8, 9}\n\n"; // R, RJ, RS, RJS, L, LJ, LS, LJS
            }
            else if (!marioCanGoBack && captureJumpLonely && captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {0, 1, 2, 3, 4, 5}\n\n"; // N, J, R, RJ, RS, RJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {0, 1, 2, 3, 4, 5}\n\n"; // N, J, R, RJ, RS, RJS
            }
            else if (!marioCanGoBack && captureJumpLonely && !captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {1, 2, 3, 4, 5}\n\n"; // J, R, RJ, RS, RJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {1, 2, 3, 4, 5}\n\n"; // J, R, RJ, RS, RJS
            }
            else if (!marioCanGoBack && !captureJumpLonely && captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {0, 2, 3, 4, 5}\n\n"; // N, R, RJ, RS, RJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {0, 2, 3, 4, 5}\n\n"; // N, R, RJ, RS, RJS
            }
            else if (!marioCanGoBack && !captureJumpLonely && !captureStay) {
                clasifierClass = "  @ATTRIBUTE actionKey {2, 3, 4, 5}\n\n"; // R, RJ, RS, RJS
                clasifierClass12 = "  @ATTRIBUTE actionKey12 {2, 3, 4, 5}\n\n"; // R, RJ, RS, RJS
            }
            pw.println(
                "@RELATION " + agentName + "-" + "freq_" + frecuencia + "-" + timeStamp + "-training\n\n" +
                // Atributos para ayudar a localizar o identificar la instancia de entrenamiento
                "  @ATTRIBUTE timeSpent NUMERIC\n" +
                "  @ATTRIBUTE timeLeft NUMERIC\n" +
                // Atributos relacionados con la intermediateReward
                ticksAttr +
                // Atributos relacionados con la matriz de observación mergeObs
                "  @ATTRIBUTE nearestEnemyLeftDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestEnemyLeft_X NUMERIC\n" +
                "  @ATTRIBUTE nearestEnemyLeft_Y NUMERIC\n" +
                "  @ATTRIBUTE nearestEnemyRightDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestEnemyRight_X NUMERIC\n" +
                "  @ATTRIBUTE nearestEnemyRight_Y NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockLeftDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockLeft_X NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockLeft_Y NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockRightDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockRight_X NUMERIC\n" +
                "  @ATTRIBUTE nearestBlockRight_Y NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinLeftDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinLeft_X NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinLeft_Y NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinRightDistance NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinRight_X NUMERIC\n" +
                "  @ATTRIBUTE nearestCoinRight_Y NUMERIC\n" +
                "  @ATTRIBUTE numEnemiesObserved NUMERIC\n" +
                "  @ATTRIBUTE numCoinsObserved NUMERIC\n" +
                // Atributos booleanos o binarios
                "  @ATTRIBUTE enemyNearRight {0, 1}\n" +
                "  @ATTRIBUTE blockNearRight {0, 1}\n" +
                "  @ATTRIBUTE enemyAheadOnFloorHeight {0, 1}\n" +
                "  @ATTRIBUTE blockAheadOnFloorHeight {0, 1}\n" +
                "  @ATTRIBUTE abyssAhead {0, 1}\n" +
                "  @ATTRIBUTE isMarioOnGround {0, 1}\n" +
                "  @ATTRIBUTE isMarioAbleToJump {0, 1}\n" +
                "  @ATTRIBUTE isMarioAbleToShoot {0, 1}\n" +
                "  @ATTRIBUTE isMarioCarrying {0, 1}\n" +
                ((frecuencia == 1)? "  @ATTRIBUTE enemyWasKilledBin {0, 1}\n" : "") +
                ((frecuencia == 1)? "  @ATTRIBUTE marioWasInjuredBin {0, 1}\n" : "") +
                "  @ATTRIBUTE isSlopeDown {0, 1}\n" +
                // Otros atributos
                ((frecuencia == 1)? "  @ATTRIBUTE coinsGainedLastTick NUMERIC\n" : "") +
                "  @ATTRIBUTE marioMode NUMERIC\n" +
                "  @ATTRIBUTE marioStatus NUMERIC\n" +
                // Clase. Clasificamos por acción
                clasifierClass +
                /****************** TICK N+12 ******************/
                 // Atributos para ayudar a localizar o identificar la instancia de entrenamiento
                 "  @ATTRIBUTE timeSpent12 NUMERIC\n" +
                 "  @ATTRIBUTE timeLeft12 NUMERIC\n" +
                 // Atributos relacionados con la intermediateReward
                 ticksAttr12 +
                 // Atributos relacionados con la matriz de observación mergeObs
                 "  @ATTRIBUTE nearestEnemyLeftDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestEnemyLeft_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestEnemyLeft_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestEnemyRightDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestEnemyRight_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestEnemyRight_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockLeftDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockLeft_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockLeft_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockRightDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockRight_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestBlockRight_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinLeftDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinLeft_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinLeft_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinRightDistance12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinRight_X12 NUMERIC\n" +
                 "  @ATTRIBUTE nearestCoinRight_Y12 NUMERIC\n" +
                 "  @ATTRIBUTE numEnemiesObserved12 NUMERIC\n" +
                 "  @ATTRIBUTE numCoinsObserved12 NUMERIC\n" +
                 // Atributos booleanos o binarios
                 "  @ATTRIBUTE enemyNearRight12 {0, 1}\n" +
                 "  @ATTRIBUTE blockNearRight12 {0, 1}\n" +
                 "  @ATTRIBUTE enemyAheadOnFloorHeight12 {0, 1}\n" +
                 "  @ATTRIBUTE blockAheadOnFloorHeight12 {0, 1}\n" +
                 "  @ATTRIBUTE abyssAhead12 {0, 1}\n" +
                 "  @ATTRIBUTE isMarioOnGround12 {0, 1}\n" +
                 "  @ATTRIBUTE isMarioAbleToJump12 {0, 1}\n" +
                 "  @ATTRIBUTE isMarioAbleToShoot12 {0, 1}\n" +
                 "  @ATTRIBUTE isMarioCarrying12 {0, 1}\n" +
                 ((frecuencia == 1)? "  @ATTRIBUTE enemyWasKilledBin12 {0, 1}\n" : "") +
                 ((frecuencia == 1)? "  @ATTRIBUTE marioWasInjuredBin12 {0, 1}\n" : "") +
                 "  @ATTRIBUTE isSlopeDown12 {0, 1}\n" +
                 // Otros atributos
                 ((frecuencia == 1)? "  @ATTRIBUTE coinsGainedLastTick12 NUMERIC\n" : "") +
                 "  @ATTRIBUTE marioMode12 NUMERIC\n" +
                 "  @ATTRIBUTE marioStatus12 NUMERIC\n" +
                 // Clase. Clasificamos por acción
                 clasifierClass12 +
                 "  @ATTRIBUTE reinforcement NUMERIC\n" +
                "@DATA"
            );
        } else {
            // Abrimos el fichero para escribir en el mismo
            try {
                tfw = new FileWriter(tf, true);
                pw = new PrintWriter(tfw);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeFile() {
        try {
            tfw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeExample(Environment env, int ticks, boolean[] pressedKeysArray) {
        if (frecuencia == 1)
            writeExampleEveryTick(env, ticks, pressedKeysArray);
        else writeExampleEveryNTicks(env, ticks, pressedKeysArray);
    }

    private void writeExampleEveryTick(Environment env, int ticks, boolean[] pressedKeysArray)
    {
        // Creamos el string con la información de nuestra instancia de entrenamiento
        String pressedKeys="";

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

        // ************ Calculamos el ejemplo de entrenamiento ************

        // Solicitamos la información de evaluación y sobre el entorno necesaria
        int evaluationInfo[]=env.getEvaluationInfoAsInts();
        int intermediateReward=env.getIntermediateReward();
        byte[][] mergeObs=env.getMergedObservationZZ(1, 1);

//        if (ticks == 0) {
//            lastTickKeys = pressedKeysArray;
//        }

        // Comprobamos si el bot se ha quedado atascado sin poder avanzar
        if (agentNameLowerCase.contains("bot")) {
            if (evaluationInfo[0] == lastCellPosition_X) {
                atascado++;
                if (atascado == 15) {
                    System.err.println("Mario se ha quedado atascado. Abortando toma de ejemplos para el nivel actual.");
                    closeFile();
                    System.exit(-1);
                }
            } else {
                lastCellPosition_X = evaluationInfo[0];
                atascado = 0;
            }
        }


        // Calculamos las diferencias de intermediateReward
        if (ticks > 0) {
            lastTickDiffIR = intermediateReward - lastTickIntermediateReward;
            if (ticks > 5)
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
                        if (env.isMarioOnGround()) {
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
        for (int i = 0; i < pressedKeysArray.length; i++) { // TODO quizás hacerlo con lastTickKeys en lugar de pressedKeysArray. Buscar código lastTickKeys comentado
            if (pressedKeysArray[i]) { // TODO quizás hacerlo con lastTickKeys en lugar de pressedKeysArray. Buscar código lastTickKeys comentado
                // Si la tecla i está pulsada
                if (Mario.KEY_RIGHT == i)
                // Está pulsada la tecla ir hacia la derecha
                    pressedKeys += "R";
               else if (Mario.KEY_LEFT == i && !pressedKeysArray[Mario.KEY_RIGHT]) // TODO quizás hacerlo con lastTickKeys en lugar de pressedKeysArray. Buscar código lastTickKeys comentado
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
            if (captureJumpLonely)
                pressedKeysByte = 1;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario solo salta
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
            if (marioCanGoBack)
                pressedKeysByte = 6;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario va a la izquierda
        }
        else if (pressedKeys.equals("LJ")) {
            if (marioCanGoBack)
                pressedKeysByte = 7;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario va a la izquierda
        }
        else if (pressedKeys.equals("LS")) {
            if (marioCanGoBack)
                pressedKeysByte = 8;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario va a la izquierda
        }
        else if (pressedKeys.equals("LJS")) {
            if (marioCanGoBack)
                pressedKeysByte = 9;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario va a la izquierda
        }
        else if (pressedKeys.equals("")) {
            if (captureStay)
                pressedKeysByte = 0;
            else return; // Ignoramos la instancia, ya que no contemplamos casos en los que Mario se queda quieto
        }

        // ************ Generamos el ejemplo de entrenamiento ************
        String example[]=new String[2];
        example[0]=example[1]="";

        example[0] += evaluationInfo[12] +                // timeSpent
        "," + evaluationInfo[11] +                        // timeLeft
        "," + intermediateReward +                        // intermediateReward
        "," + lastTickDiffIR +                            // intermediateRewardWonLastTick
        "," + last6TicksDiffIR;                           // intermediateRewardWonLast6Ticks
        // Más tarde, será incluida la información de intermediateReward de futuros ticks justo aquí

        example[1] += "," + df.format(nearestEnemyLeft) + // nearestEnemyLeftDistance
        "," + nearestEnemyLeftCoords[0] +                 // nearestEnemyLeft_X
        "," + nearestEnemyLeftCoords[1] +                 // nearestEnemyLeft_Y
        "," + df.format(nearestEnemyRight) +              // nearestEnemyRightDistance
        "," + nearestEnemyRightCoords[0] +                // nearestEnemyRight_X
        "," + nearestEnemyRightCoords[1] +                // nearestEnemyRight_Y
        "," + df.format(nearestBlockLeft) +               // nearestBlockLeftDistance
        "," + nearestBlockLeftCoords[0] +                 // nearestBlockLeft_X
        "," + nearestBlockLeftCoords[1] +                 // nearestBlockLeft_Y
        "," + df.format(nearestBlockRight) +              // nearestBlockRightDistance
        "," + nearestBlockRightCoords[0] +                // nearestBlockRight_X
        "," + nearestBlockRightCoords[1] +                // nearestBlockRight_Y
        "," + df.format(nearestCoinLeft) +                // nearestCoinLeftDistance
        "," + nearestCoinLeftCoords[0] +                  // nearestCoinLeft_X
        "," + nearestCoinLeftCoords[1] +                  // nearestCoinLeft_Y
        "," + df.format(nearestCoinRight) +               // nearestCoinRightDistance
        "," + nearestCoinRightCoords[0] +                 // nearestCoinRight_X
        "," + nearestCoinRightCoords[1] +                 // nearestCoinRight_Y
        "," + numEnemiesObserved+                         // numEnemiesObserved
        "," + numCoinsObserved +                          // numCoinsObservated
        "," + enemyNearRight +                            // enemyNearRight
        "," + blockNearRight +                            // blockNearRight
        "," + enemyAheadOnFloorHeight +                   // enemyAheadOnFloorHeight
        "," + blockAheadOnFloorHeight +                   // blockAheadOnFloorHeight
        "," + abyssAhead +                                // abyssAhead
        "," + ((env.isMarioOnGround())? "1" : "0") +      // isMarioOnGround
        "," + ((env.isMarioAbleToJump())? "1" : "0") +    // isMarioAbleToJump
        "," + ((env.isMarioAbleToShoot())? "1" : "0") +   // isMarioAbleToShoot
        "," + ((env.isMarioCarrying())? "1" : "0") +      // isMarioCarrying
        "," + enemyWasKilledBin +                         // enemyWasKilledBin
        "," + marioWasInjuredBin +                        // marioWasInjuredBin
        "," + isSlopeDown +                               // isSlopeDown
        "," + coinsGainedLastTick +                       // coinsGainedLastTick
        "," + evaluationInfo[7] +                         // marioMode
        "," + evaluationInfo[8] +                         // marioStatus
        "," + pressedKeysByte;                            // actionKey (class)

        // Actualizamos las pressed keys
        // lastTickKeys = pressedKeysArray;

        // Como necesitamos para el tick T información del tick T+N (en este caso N = 12)
        // imprimimos con N ticks de retraso, mientras vamos almacenando en un ArrayList a modo de cola
        last12ticksInstances.add(example);
        last12ticksReward.add(intermediateReward);
        last12ticksPosition.add(evaluationInfo[1]);

        if (last12ticksInstances.size() > 6) {

            // Tick 6 en adelante
            String exampleFull[] = last12ticksInstances.get(last12ticksInstances.size()-6);
            exampleFull[0] += "," + intermediateReward;
            last12ticksInstances.set(last12ticksInstances.size()-6, exampleFull);

            if (last12ticksInstances.size() > 12) {

                exampleFull = last12ticksInstances.remove(0);
                int r = last12ticksReward.remove(0);
                int lp = last12ticksPosition.remove(0);
                int reinforcement = (intermediateReward + evaluationInfo[1]) - (r + lp);

                // Imprimimos el ejemplo de entrenamiento a fichero
                pw.println(exampleFull[0] + exampleFull[1] + "," + example[0] + example[1] + "," + reinforcement);

            }
        }

    }

    private void writeExampleEveryNTicks(Environment env, int ticks, boolean[] pressedKeysArray) {
        //BORRADO el 06/may
    }
}
