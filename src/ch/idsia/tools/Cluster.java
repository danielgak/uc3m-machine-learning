package ch.idsia.tools;
import ch.idsia.tools.Instance;

public class Cluster {
    public final float intermediateReward;
    public final float nearestEnemyLeftDistance;
    public final float nearestEnemyLeft_X;
    public final float nearestEnemyLeft_Y;
    public final float nearestEnemyRightDistance;
    public final float nearestEnemyRight_X;
    public final float nearestEnemyRight_Y;
    public final float nearestBlockLeftDistance;
    public final float nearestBlockLeft_X;
    public final float nearestBlockLeft_Y;
    public final float nearestBlockRightDistance;
    public final float nearestBlockRight_X;
    public final float nearestBlockRight_Y;
    public final float nearestCoinLeftDistance;
    public final float nearestCoinLeft_X;
    public final float nearestCoinLeft_Y;
    public final float nearestCoinRightDistance;
    public final float nearestCoinRight_X;
    public final float nearestCoinRight_Y;
    public final float numEnemiesObserved;
    public final float numCoinsObserved;
    public final float enemyNearRight;
    public final float blockNearRight;
    public final float enemyAheadOnFloorHeight;
    public final float blockAheadOnFloorHeight;
    public final float abyssAhead;
    public final float isMarioOnGround;
    public final float isMarioAbleToJump;
    public final float isMarioAbleToShoot;
    public final float isMarioCarrying;
    public final float isSlopeDown;
    
    public Cluster(float intermediateReward, float nearestEnemyLeftDistance, float nearestEnemyLeft_X, float nearestEnemyLeft_Y,
        float nearestEnemyRightDistance, float nearestEnemyRight_X, float nearestEnemyRight_Y, float nearestBlockLeftDistance,
        float nearestBlockLeft_X, float nearestBlockLeft_Y, float nearestBlockRightDistance, float nearestBlockRight_X,
        float nearestBlockRight_Y, float nearestCoinLeftDistance, float nearestCoinLeft_X, float nearestCoinLeft_Y,
        float nearestCoinRightDistance, float nearestCoinRight_X, float nearestCoinRight_Y, float numEnemiesObserved,
        float numCoinsObserved, float enemyNearRight, float blockNearRight, float enemyAheadOnFloorHeight,
        float blockAheadOnFloorHeight, float abyssAhead, float isMarioOnGround, float isMarioAbleToJump, float isMarioAbleToShoot,
        float isMarioCarrying, float isSlopeDown)
    {
        this.intermediateReward=intermediateReward;
        this.nearestEnemyLeftDistance=nearestEnemyLeftDistance;
        this.nearestEnemyLeft_X=nearestEnemyLeft_X;
        this.nearestEnemyLeft_Y=nearestEnemyLeft_Y;
        this.nearestEnemyRightDistance=nearestEnemyRightDistance;
        this.nearestEnemyRight_X=nearestEnemyRight_X;
        this.nearestEnemyRight_Y=nearestEnemyRight_Y;
        this.nearestBlockLeftDistance=nearestBlockLeftDistance;
        this.nearestBlockLeft_X=nearestBlockLeft_X;
        this.nearestBlockLeft_Y=nearestBlockLeft_Y;
        this.nearestBlockRightDistance=nearestBlockRightDistance;
        this.nearestBlockRight_X=nearestBlockRight_X;
        this.nearestBlockRight_Y=nearestBlockRight_Y;
        this.nearestCoinLeftDistance=nearestCoinLeftDistance;
        this.nearestCoinLeft_X=nearestCoinLeft_X;
        this.nearestCoinLeft_Y=nearestCoinLeft_Y;
        this.nearestCoinRightDistance=nearestCoinRightDistance;
        this.nearestCoinRight_X=nearestCoinRight_X;
        this.nearestCoinRight_Y=nearestCoinRight_Y;
        this.numEnemiesObserved=numEnemiesObserved;
        this.numCoinsObserved=numCoinsObserved;
        this.enemyNearRight=enemyNearRight;
        this.blockNearRight=blockNearRight;
        this.enemyAheadOnFloorHeight=enemyAheadOnFloorHeight;
        this.blockAheadOnFloorHeight=blockAheadOnFloorHeight;
        this.abyssAhead=abyssAhead;
        this.isMarioOnGround=isMarioOnGround;
        this.isMarioAbleToJump=isMarioAbleToJump;
        this.isMarioAbleToShoot=isMarioAbleToShoot;
        this.isMarioCarrying=isMarioCarrying;
        this.isSlopeDown=isSlopeDown;
    }

    public float calculateSimilitude(Instance i){
        return 
            Math.abs(this.intermediateReward - i.intermediateReward) +
            Math.abs(this.nearestEnemyLeftDistance - i.nearestEnemyLeftDistance) +
            Math.abs(this.nearestEnemyLeft_X - i.nearestEnemyLeft_X) +
            Math.abs(this.nearestEnemyLeft_Y - i.nearestEnemyLeft_Y) +
            Math.abs(this.nearestEnemyRightDistance - i.nearestEnemyRightDistance) +
            Math.abs(this.nearestEnemyRight_X - i.nearestEnemyRight_X) +
            Math.abs(this.nearestEnemyRight_Y - i.nearestEnemyRight_Y) +
            Math.abs(this.nearestBlockLeftDistance - i.nearestBlockLeftDistance) +
            Math.abs(this.nearestBlockLeft_X - i.nearestBlockLeft_X) +
            Math.abs(this.nearestBlockLeft_Y - i.nearestBlockLeft_Y) +
            Math.abs(this.nearestBlockRightDistance - i.nearestBlockRightDistance) +
            Math.abs(this.nearestBlockRight_X - i.nearestBlockRight_X) +
            Math.abs(this.nearestBlockRight_Y - i.nearestBlockRight_Y) +
            Math.abs(this.nearestCoinLeftDistance - i.nearestCoinLeftDistance) +
            Math.abs(this.nearestCoinLeft_X - i.nearestCoinLeft_X) +
            Math.abs(this.nearestCoinLeft_Y - i.nearestCoinLeft_Y) +
            Math.abs(this.nearestCoinRightDistance - i.nearestCoinRightDistance) +
            Math.abs(this.nearestCoinRight_X - i.nearestCoinRight_X) +
            Math.abs(this.nearestCoinRight_Y - i.nearestCoinRight_Y) +
            Math.abs(this.numEnemiesObserved - i.numEnemiesObserved) +
            Math.abs(this.numCoinsObserved - i.numCoinsObserved) +
            Math.abs(this.enemyNearRight - i.enemyNearRight) +
            Math.abs(this.blockNearRight - i.blockNearRight) +
            Math.abs(this.enemyAheadOnFloorHeight - i.enemyAheadOnFloorHeight) +
            Math.abs(this.blockAheadOnFloorHeight - i.blockAheadOnFloorHeight) +
            Math.abs(this.abyssAhead - i.abyssAhead) +
            Math.abs(this.isMarioOnGround - i.isMarioOnGround) +
            Math.abs(this.isMarioAbleToJump - i.isMarioAbleToJump) +
            Math.abs(this.isMarioAbleToShoot - i.isMarioAbleToShoot) +
            Math.abs(this.isMarioCarrying - i.isMarioCarrying) +
            Math.abs(this.isSlopeDown - i.isSlopeDown);
    }

        // public int getBestCluster(Intsnace in, Cluster [] clusters){
        // int min = 0;
        // float min_result = 0.0f;
        // float aux = 0.0f;
        
        // for (int i = 0; i < clusters.length; i++) {
            
        //     aux = clusters[i].calculateSimilitude(in);

        //     if (aux < min) {
        //         min = i;
        //         min_result = aux;
        //     }
        // }
        // return min;
}
