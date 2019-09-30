/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package qlearning;

import java.util.*;
import ch.idsia.tools.*;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author moises
 */
public class main 
{
    public static void main(String[] args) 
    {   
        List<Tupla> mapa  = new ArrayList<Tupla>();
        
        String[] acciones = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] estados  = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        
        double[] estado = {0};
        double[] estadoFinal = {99};
        double[] accion = {0};
        double refuerzo = 0;
        
        boolean encontrado;
        
        QLearning ql        = new QLearning(0, 0.15, 0.2, estados, acciones, estados.length, acciones.length);
        int ciclosMaximos   = 1000000;
        int ciclos          = 0;
        int posicion;

        //         this.clusters = new Cluster[10];
        // Cluster [] clusters = new Cluster[20];
        // clusters[0] = new Cluster(1099f,12.728f,0f,0f,6.403f,13f,14f,12.728f,0f,0f,6.325f,15f,7f,12.728f,0f,0f,8.062f,16f,5f,1f,3f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[1] = new Cluster(235f,12.728f,0f,0f,4.472f,13f,11f,4.472f,7f,5f,4.123f,13f,8f,6f,9f,3f,5.831f,14f,6f,1f,14f,0f,0f,0f,1f,1f,0f,0f,1f,0f,0f);
        // clusters[2] = new Cluster(9f,12.728f,0f,0f,4f,13f,9f,12.728f,0f,0f,12.728f,18f,18f,12.728f,0f,0f,12.728f,18f,18f,1f,0f,0f,0f,0f,1f,1f,0f,0f,1f,0f,0f);
        // clusters[3] = new Cluster(2217f,6f,9f,15f,8.485f,15f,15f,2.236f,7f,8f,9f,18f,9f,4.243f,6f,6f,12.728f,18f,18f,3f,7f,0f,0f,0f,0f,0f,0f,0f,1f,0f,0f);
        // clusters[4] = new Cluster(525f,12.728f,0f,0f,6.083f,15f,10f,4.472f,5f,7f,8f,17f,9f,5.657f,5f,5f,9.055f,18f,8f,1f,11f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // clusters[5] = new Cluster(1337f,12.728f,0f,0f,5.831f,14f,12f,1f,8f,9f,1f,10f,9f,12.728f,0f,0f,2.236f,11f,10f,1f,9f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[6] = new Cluster(1875f,6.325f,3f,11f,4.123f,13f,10f,5f,9f,4f,5.385f,11f,4f,8.602f,4f,2f,1f,10f,9f,2f,11f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // clusters[7] = new Cluster(59f,2f,9f,11f,2.828f,11f,11f,6.083f,8f,3f,5.657f,13f,5f,8.246f,7f,1f,12.728f,18f,18f,3f,2f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[8] = new Cluster(1129f,12.728f,0f,0f,5.099f,14f,10f,7f,2f,9f,8.602f,14f,2f,8.062f,1f,8f,5f,14f,9f,2f,7f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[9] = new Cluster(529f,12.728f,0f,0f,12.728f,18f,18f,12.728f,0f,0f,4f,13f,9f,12.728f,0f,0f,12.728f,18f,18f,0f,0f,0f,0f,0f,0f,1f,1f,0f,1f,0f,1f);
        // clusters[10] = new Cluster(331f,12.728f,0f,0f,3.606f,12f,11f,12.728f,0f,0f,12.728f,18f,18f,12.728f,0f,0f,12.728f,18f,18f,1f,0f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[11] = new Cluster(133f,12.728f,0f,0f,5.831f,14f,12f,12.728f,0f,0f,4.472f,13f,7f,12.728f,0f,0f,2.236f,11f,8f,1f,6f,0f,0f,0f,1f,1f,0f,0f,1f,0f,0f);
        // clusters[12] = new Cluster(421f,12.728f,0f,0f,2.236f,11f,10f,3.162f,8f,6f,7f,16f,9f,12.728f,0f,0f,8.062f,17f,8f,2f,5f,0f,0f,1f,1f,1f,0f,0f,1f,0f,0f);
        // clusters[13] = new Cluster(19f,12.728f,0f,0f,3.606f,12f,11f,12.728f,0f,0f,11.314f,17f,1f,12.728f,0f,0f,12.728f,18f,18f,2f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // clusters[14] = new Cluster(753f,6.083f,3f,10f,2f,11f,9f,1f,8f,9f,5f,14f,9f,12.728f,0f,0f,6.325f,15f,7f,2f,8f,1f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // clusters[15] = new Cluster(2317f,12.728f,0f,0f,2f,11f,9f,4f,5f,9f,5.099f,10f,4f,7f,9f,2f,8.062f,17f,8f,2f,9f,1f,0f,0f,1f,1f,1f,0f,1f,0f,0f);
        // clusters[16] = new Cluster(163f,1.414f,8f,10f,6f,15f,9f,4f,5f,9f,3f,12f,9f,12.728f,0f,0f,7.071f,16f,8f,3f,6f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f);
        // clusters[17] = new Cluster(993f,4.123f,5f,10f,5.099f,14f,10f,3f,9f,6f,3.162f,10f,6f,5f,9f,4f,1f,10f,9f,4f,10f,0f,0f,0f,1f,1f,1f,0f,1f,0f,1f);
        // clusters[18] = new Cluster(1205f,12.728f,0f,0f,2.828f,11f,11f,7.616f,2f,6f,4f,13f,9f,12.728f,0f,0f,12.728f,18f,18f,1f,0f,0f,0f,0f,0f,1f,0f,0f,1f,0f,0f);
        // clusters[19] = new Cluster(1141f,8.944f,1f,13f,9.22f,18f,11f,4.243f,6f,6f,4.472f,11f,5f,5.831f,6f,4f,1f,10f,9f,2f,15f,0f,0f,0f,1f,1f,1f,1f,1f,0f,1f);

        // Tuple read aux data

        Instance in;
        Byte action;
        Instance in12;
        Float reinforcement;

        //Reading

        String csvFile = "../weka/ejemplos_entrenamiento/ejemplo_entrenamiento_baseline.csv";
        String line = "\n";
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            while ((line = br.readLine()) != null) {

                String[] instance = line.split(csvSplitBy);
                if (!instance[0].equalsIgnoreCase("")) {
                    in = new Instance(
                        Short.parseShort(instance[0]),     // timeSpent;
                        Short.parseShort(instance[1]),     // timeLeft;
                        Short.parseShort(instance[2]),     // intermediateReward;
                        Short.parseShort(instance[3]),     // intermediateRewardWonLastTick; 
                        Short.parseShort(instance[4]),     // intermediateRewardWonLast6Ticks;
                        Short.parseShort(instance[5]),     // intermediaRewardWonFuture6Ticks;
                        Short.parseShort("0"),             // intermediaRewardWonFuture12Ticks;
                        Short.parseShort("0"),             // intermediaRewardWonFuture24Ticks;
                        Float.parseFloat(instance[6]),     // nearestEnemyLeftDistance;
                        Byte.parseByte(instance[7]),       // nearestEnemyLeft_X;
                        Byte.parseByte(instance[8]),       // nearestEnemyLeft_Y;
                        Float.parseFloat(instance[9]),     // nearestEnemyRightDistance;
                        Byte.parseByte(instance[10]),      // nearestEnemyRight_X;
                        Byte.parseByte(instance[11]),      // nearestEnemyRight_Y;
                        Float.parseFloat(instance[12]),    // nearestBlockLeftDistance;
                        Byte.parseByte(instance[13]),      // nearestBlockLeft_X;
                        Byte.parseByte(instance[14]),      // nearestBlockLeft_Y;
                        Float.parseFloat(instance[15]),    // nearestBlockRightDistance;
                        Byte.parseByte(instance[16]),      // nearestBlockRight_X;
                        Byte.parseByte(instance[17]),      // nearestBlockRight_Y;
                        Float.parseFloat(instance[18]),    // nearestCoinLeftDistance;
                        Byte.parseByte(instance[19]),      // nearestCoinLeft_X;
                        Byte.parseByte(instance[20]),      // nearestCoinLeft_Y;
                        Float.parseFloat(instance[21]),    // nearestCoinRightDistance;
                        Byte.parseByte(instance[22]),      // nearestCoinRight_X;
                        Byte.parseByte(instance[23]),      // nearestCoinRight_Y;
                        Short.parseShort(instance[24]),    // numEnemiesObserved;
                        Short.parseShort(instance[25]),    // numCoinsObserved;
                        Byte.parseByte(instance[26]),      // enemyNearRight;
                        Byte.parseByte(instance[27]),      // blockNearRight;
                        Byte.parseByte(instance[28]),      // enemyAheadOnFloorHeight;
                        Byte.parseByte(instance[29]),      // blockAheadOnFloorHeight;
                        Byte.parseByte(instance[30]),      // abyssAhead;
                        Byte.parseByte(instance[31]),      // isMarioOnGround;
                        Byte.parseByte(instance[32]),      // isMarioAbleToJump;
                        Byte.parseByte(instance[33]),      // isMarioAbleToShoot;
                        Byte.parseByte(instance[34]),      // isMarioCarrying;
                        Byte.parseByte(instance[35]),      // enemyWasKilledBin;
                        Byte.parseByte(instance[36]),      // marioWasInjuredBin;
                        Byte.parseByte(instance[37]),      // isSlopeDown;
                        Byte.parseByte(instance[38]),      // coinsGainedLastTick;
                        Byte.parseByte(instance[39]),      // marioMode;
                        Byte.parseByte(instance[40]),      // marioStatus;
                        Byte.parseByte(instance[41])       // actionKey;
                    );
                    action =  Byte.parseByte(instance[41]);
                    
                    in12 = new Instance(
                        Short.parseShort(instance[42]),    // timeSpent;
                        Short.parseShort(instance[43]),    // timeLeft;
                        Short.parseShort(instance[44]),    // intermediateReward;
                        Short.parseShort(instance[45]),    // intermediateRewardWonLastTick; 
                        Short.parseShort(instance[46]),    // intermediateRewardWonLast6Ticks;
                        Short.parseShort("0"),             // intermediaRewardWonFuture6Ticks;
                        Short.parseShort("0"),             // intermediaRewardWonFuture12Ticks;
                        Short.parseShort("0"),             // intermediaRewardWonFuture24Ticks;
                        Float.parseFloat(instance[47]),    // nearestEnemyLeftDistance;
                        Byte.parseByte(instance[48]),      // nearestEnemyLeft_X;
                        Byte.parseByte(instance[49]),      // nearestEnemyLeft_Y;
                        Float.parseFloat(instance[50]),    // nearestEnemyRightDistance;
                        Byte.parseByte(instance[51]),      // nearestEnemyRight_X;
                        Byte.parseByte(instance[52]),      // nearestEnemyRight_Y;
                        Float.parseFloat(instance[53]),    // nearestBlockLeftDistance;
                        Byte.parseByte(instance[54]),      // nearestBlockLeft_X;
                        Byte.parseByte(instance[55]),      // nearestBlockLeft_Y;
                        Float.parseFloat(instance[56]),    // nearestBlockRightDistance;
                        Byte.parseByte(instance[57]),      // nearestBlockRight_X;
                        Byte.parseByte(instance[58]),      // nearestBlockRight_Y;
                        Float.parseFloat(instance[59]),    // nearestCoinLeftDistance;
                        Byte.parseByte(instance[60]),      // nearestCoinLeft_X;
                        Byte.parseByte(instance[61]),      // nearestCoinLeft_Y;
                        Float.parseFloat(instance[62]),    // nearestCoinRightDistance;
                        Byte.parseByte(instance[63]),      // nearestCoinRight_X;
                        Byte.parseByte(instance[64]),      // nearestCoinRight_Y;
                        Short.parseShort(instance[65]),    // numEnemiesObserved;
                        Short.parseShort(instance[66]),    // numCoinsObserved;
                        Byte.parseByte(instance[67]),      // enemyNearRight;
                        Byte.parseByte(instance[68]),      // blockNearRight;
                        Byte.parseByte(instance[69]),      // enemyAheadOnFloorHeight;
                        Byte.parseByte(instance[70]),      // blockAheadOnFloorHeight;
                        Byte.parseByte(instance[71]),      // abyssAhead;
                        Byte.parseByte(instance[72]),      // isMarioOnGround;
                        Byte.parseByte(instance[73]),      // isMarioAbleToJump;
                        Byte.parseByte(instance[74]),      // isMarioAbleToShoot;
                        Byte.parseByte(instance[75]),      // isMarioCarrying;
                        Byte.parseByte(instance[76]),      // enemyWasKilledBin;
                        Byte.parseByte(instance[77]),      // marioWasInjuredBin;
                        Byte.parseByte(instance[78]),      // isSlopeDown;
                        Byte.parseByte(instance[79]),      // coinsGainedLastTick;
                        Byte.parseByte(instance[80]),      // marioMode;
                        Byte.parseByte(instance[81]),      // marioStatus;
                        Byte.parseByte("0")                // actionKey;
                    );
                    
                    // reinforcement = Float.parseFloat(instance[82]);
                    reinforcement =
                        0.4f * ((in.intermediaRewardWonFuture6Ticks + in12.intermediateRewardWonLast6Ticks) / 2) +
                        0.1f * (in12.intermediateReward - in.intermediateReward);
                    // reinforcement += reinforcement * ((in.isMarioOnGround + 1) % 2) * 0.2f;
                    // Eliminar refuerzos negativos leidos de fichero
                    if (reinforcement < 0)
                        reinforcement = reinforcement / -100;
                    if (action == 2)
                        reinforcement += reinforcement * 0.15f;
                    if (action == 3)
                        reinforcement += reinforcement * 0.9f;

                    mapa.add(
                        new Tupla(
                            calculateSituation(in),
                            action,
                            calculateSituation(in12),
                            reinforcement
                        )
                    );
                }
            }
        }catch (Exception e) {
            System.err.println("There was an error loading training instances");
            e.printStackTrace();
        }  
                
        while (ciclos < ciclosMaximos)
        {
            for (int i = 0; i < mapa.size(); i++)
                ql.actualizarTablaQ(mapa.get(i));
            
            ciclos++;
        }
        
        ql.mostrarTablaQ();
        // boolean salir = false;
        // while (estado[0] != estadoFinal[0] && !salir)
        // {
        //     accion      = ql.obtenerMejorAccion(estado);
        //     refuerzo    = ql.obtenerFuncionQMax(estado);
        //     posicion    = 0;
        //     encontrado  = false;
            
        //     while (!encontrado && !salir)
        //     {
        //         if ((mapa.get(posicion).getEstado(0) == estado[0]) && (mapa.get(posicion).getAccion(0) == accion[0])) 
        //         {
        //             System.out.print("Transito de " + estado[0]);
                            
        //             estado[0] = mapa.get(posicion).getEstadoSiguiente(0);
                    
        //             System.out.println(" a " + estado[0] + " con " + accion[0] + "(" + (double)Math.round(refuerzo * 100)/100 + ")");
                    
        //             encontrado = true;
        //         }
                
        //         posicion++;

		//         if(posicion >= mapa.size()) salir = true;
        //     }
        // }
        
    }
    
    public static int getBestCluster(Instance in, Cluster [] clusters){
        int cluster = -1;
        float min_distance = Float.MAX_VALUE, distance;
        
        for (int i = 0; i < clusters.length; i++) {
            
            distance = clusters[i].calculateSimilitude(in);

            if (distance < min_distance) {
                cluster = i;
                min_distance = distance;
            }
        }
        return cluster;
    }

    public static int calculateSituation(Instance instance) {

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
                    // El enemigo y el obstáculo están a distinta misma altura que Mario
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
}


