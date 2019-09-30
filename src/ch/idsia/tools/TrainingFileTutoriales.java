package ch.idsia.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.tools.TickInfo;
/**
 * Esta clase sirve de manejador del fichero de entrenamiento.
 */
public class TrainingFileTutoriales {

    private File tf;
    private FileWriter tfw;
    private PrintWriter pw;
    private ArrayList<TickInfo> futureTicks; // Cola en la que almacenar la información de 24 ticks hasta tener n+24
    private int lastTicks[][];
    // Por eficiencia en cache a la hora de recorrer la matriz al actualizar valores:
    // lastTicks[0][] = hace 5 ticks, lastTicks[5][] = tick actual
    // lastTicks[X][0] = num_monedas, lastTicks[X][1] = num_enemigos_muertos

    public TrainingFileTutoriales(String agentName) {
        lastTicks = new int[6][2];
        futureTicks = new ArrayList<TickInfo>();
        tf = new File("weka/ejemplos_entrenamiento/" + agentName + ".arff");
        if (!tf.exists()) {
            // Si el fichero no existe le añadimos la cabecera de weka
            System.out.println("TrainigFile.java: File weka/ejemplos_entrenamiento/" + agentName +
                               ".arff doesn't exist. Generating .arff header.");
            // Comprobamos si existe la ruta, en caso contrario la creamos
            File dir = new File("weka/ejemplos_entrenamiento/");
            if (!dir.exists())
                dir.mkdirs();
            // Abrimos el fichero para escribir en el mismo
            try {
                tfw = new FileWriter(tf, true);
                pw = new PrintWriter(tfw);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Escribimos la cabecera de los ficheros .arff de weka
            String attrMergeObs = "";
            for (int i = 0; i < 19; i++)
                for (int j = 0; j < 19; j++)
                    attrMergeObs += "  @ATTRIBUTE mergeObservation_" + i + "_" + j + " NUMERIC\n";
            int N = 6;
            String attrFuture = "";
            while (N <= 24) {
                attrFuture += "  @ATTRIBUTE coinsFuture" + N + "Ticks NUMERIC\n  @ATTRIBUTE killsFuture" + N + "Ticks NUMERIC\n";
                N *= 2;
            }
            pw.println(
                "@RELATION " + agentName + "-training\n\n" +
                "  @ATTRIBUTE marioXpos NUMERIC\n" +
                "  @ATTRIBUTE marioYpos NUMERIC\n" +
                "  @ATTRIBUTE intermediateReward NUMERIC\n" +
                attrMergeObs +
                "  @ATTRIBUTE coinsObserved NUMERIC\n" +
                "  @ATTRIBUTE blocksObserved NUMERIC\n" +
                "  @ATTRIBUTE enemiesObserved NUMERIC\n" +
                "  @ATTRIBUTE coins5TicksAgo NUMERIC\n" +
                "  @ATTRIBUTE kills5TicksAgo NUMERIC\n" +
                attrFuture +
                "  @ATTRIBUTE flowersDevoured NUMERIC\n" +
                "  @ATTRIBUTE killsByFire NUMERIC\n" +
                "  @ATTRIBUTE killsByShell NUMERIC\n" +
                "  @ATTRIBUTE killsByStomp NUMERIC\n" +
                "  @ATTRIBUTE killsTotal NUMERIC\n" +
                "  @ATTRIBUTE marioMode NUMERIC\n" +
                "  @ATTRIBUTE marioStatus NUMERIC\n" +
                "  @ATTRIBUTE mushroomsDevoured NUMERIC\n" +
                "  @ATTRIBUTE coinsGained NUMERIC\n" +
                "  @ATTRIBUTE timeLeft NUMERIC\n" +
                "  @ATTRIBUTE timeSpent NUMERIC\n" +
                "  @ATTRIBUTE hiddenBlocksFound NUMERIC\n" +
                "  @ATTRIBUTE distancePassedCells NUMERIC\n" +
                "  @ATTRIBUTE distancePassedPhys NUMERIC\n\n" +
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

    public void writeExample(Environment env) {
        writeExample(env, -1);
    }

    public void writeExample(Environment env, int ticks) {
        // Creamos el string con la información de nuestra línea de ejemplo de entrenamiento
        String example[] = {"", ""};

        // Solicitamos la información de evaluación y sobre el entorno necesaria
        int evaluationInfo[] = env.getEvaluationInfoAsInts();
        float marioFloatPos[] = env.getMarioFloatPos();
        int intermediateReward = env.getIntermediateReward();
        byte[][] mergeObs = env.getMergedObservationZZ(2, 2);
        byte coinsObs = 0, blocksObs = 0, enemiesObs = 0;

        // Almacenamos el número de monedas recogidas y enemigos eliminados en los últimos 5 ticks
        for (int i = 0; i < lastTicks.length-1; i++)
            for (int j = 0; j < lastTicks[i].length; j++)
                lastTicks[i][j] = lastTicks[i+1][j];
        lastTicks[5][0] = evaluationInfo[10]; // coinsGained
        lastTicks[5][1] = evaluationInfo[6]; // killsTotal
        
        // ************ Generamos el ejemplo de entrenamiento ************

        // Posicion de Mario y recompensa
        example[0] += marioFloatPos[0] + "," + marioFloatPos[1] + "," + intermediateReward;
        // Matriz de observacion
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                example[0] += "," + mergeObs[i][j];
                if (mergeObs[i][j] == -60)
                    blocksObs++;
                else if (mergeObs[i][j] == 2)
                    coinsObs++;
                else if (mergeObs[i][j] == 1)
                    enemiesObs++;
            }
        }
        // Número de monedas, bloques y enemigos observados. Monedas ganadas y enemigos muertos hace 5 ticks
        example[0] += "," + coinsObs + "," + blocksObs + "," + enemiesObs + "," + lastTicks[0][0] + "," + lastTicks[0][1];
        // Información de evaluación
        for (int i = 2; i < evaluationInfo.length; i++)
            // Ojo, solo de la posición 2 a la final. Las posiciones 0 y 1 están al final de la linea de ejemplo de entrenamiento
            example[1] += "," + evaluationInfo[i];
        example[1] += "," + evaluationInfo[0] + "," + evaluationInfo[1];

        // ************ Imprimimos el ejemplo de entrenamiento a fichero ************
        if (ticks == -1)
            // Si no necesitamos información futura imprimimos sin retardo
            pw.println(example[0] + example[1]);
        else {
            // En caso contrario, si necesitamos para el tick T información del tick T+N (en este caso N = 24)
            // imprimimos con N ticks de retraso, mientras vamos almacenando en un ArrayList a modo de cola
            TickInfo tickInfo = new TickInfo(evaluationInfo[10], evaluationInfo[6], example);
            futureTicks.add(tickInfo);
            if (ticks >= 5) {
                // Tick 6 en adelante
                tickInfo = futureTicks.get(futureTicks.size()-6);
                tickInfo.setFutureTickInfo(evaluationInfo[10], evaluationInfo[6]);
                futureTicks.set(futureTicks.size()-6, tickInfo);
                if (ticks >= 11) {
                    // Tick 12 en adelante
                    tickInfo = futureTicks.get(futureTicks.size()-12);
                    tickInfo.setFutureTickInfo(evaluationInfo[10], evaluationInfo[6]);
                    futureTicks.set(futureTicks.size()-12, tickInfo);
                    if (ticks >= 23) {
                        // Tick 23 en adelante
                        // Como ya tenemos la informacion que necesitamos, podemos desencolar
                        tickInfo = futureTicks.remove(0);
                        tickInfo.setFutureTickInfo(evaluationInfo[10], evaluationInfo[6]);
                        // Imprimimos el ejemplo de entrenamiento a fichero
                        pw.println(tickInfo.getExampleString());
                    }
                }
            }
        }
    }
}