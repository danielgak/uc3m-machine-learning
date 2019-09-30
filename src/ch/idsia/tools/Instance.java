package ch.idsia.tools;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.Locale;
/**
 * Esta clase representa los atributos de una instancia de entrenamiento como un objeto
 */
public class Instance {
    public final short timeSpent;
    public final short timeLeft;
    public final short intermediateReward;
    public final short intermediateRewardWonLastTick; 
    public final short intermediateRewardWonLast6Ticks;
    public final short intermediaRewardWonFuture6Ticks; //
    public final short intermediaRewardWonFuture12Ticks; // 
    public final short intermediaRewardWonFuture24Ticks; //
    public final float nearestEnemyLeftDistance;
    public final byte nearestEnemyLeft_X;
    public final byte nearestEnemyLeft_Y;
    public final float nearestEnemyRightDistance;
    public final byte nearestEnemyRight_X;
    public final byte nearestEnemyRight_Y;
    public final float nearestBlockLeftDistance;
    public final byte nearestBlockLeft_X;
    public final byte nearestBlockLeft_Y;
    public final float nearestBlockRightDistance;
    public final byte nearestBlockRight_X;
    public final byte nearestBlockRight_Y;
    public final float nearestCoinLeftDistance;
    public final byte nearestCoinLeft_X;
    public final byte nearestCoinLeft_Y;
    public final float nearestCoinRightDistance;
    public final byte nearestCoinRight_X;
    public final byte nearestCoinRight_Y;
    public final short numEnemiesObserved;
    public final short numCoinsObserved;
    public final byte enemyNearRight;
    public final byte blockNearRight;
    public final byte enemyAheadOnFloorHeight;
    public final byte blockAheadOnFloorHeight;
    public final byte abyssAhead;
    public final byte isMarioOnGround;
    public final byte isMarioAbleToJump;
    public final byte isMarioAbleToShoot;
    public final byte isMarioCarrying;
    public final byte enemyWasKilledBin;
    public final byte marioWasInjuredBin;
    public final byte isSlopeDown;
    public final byte coinsGainedLastTick;
    public final byte marioMode;
    public final byte marioStatus;
    public final byte actionKey; //

    public Instance(short timeSpent, short timeLeft, short intermediateReward, short intermediateRewardWonLastTick,
        short intermediateRewardWonLast6Ticks,short intermediaRewardWonFuture6Ticks, short intermediaRewardWonFuture12Ticks,
        short intermediaRewardWonFuture24Ticks, float nearestEnemyLeftDistance, byte nearestEnemyLeft_X, byte nearestEnemyLeft_Y,
        float nearestEnemyRightDistance, byte nearestEnemyRight_X, byte nearestEnemyRight_Y, float nearestBlockLeftDistance,
        byte nearestBlockLeft_X, byte nearestBlockLeft_Y, float nearestBlockRightDistance, byte nearestBlockRight_X,
        byte nearestBlockRight_Y, float nearestCoinLeftDistance, byte nearestCoinLeft_X, byte nearestCoinLeft_Y,
        float nearestCoinRightDistance, byte nearestCoinRight_X, byte nearestCoinRight_Y, short numEnemiesObserved,
        short numCoinsObserved, byte enemyNearRight, byte blockNearRight, byte enemyAheadOnFloorHeight,
        byte blockAheadOnFloorHeight, byte abyssAhead, byte isMarioOnGround, byte isMarioAbleToJump, byte isMarioAbleToShoot,
        byte isMarioCarrying, byte enemyWasKilledBin, byte marioWasInjuredBin, byte isSlopeDown, byte coinsGainedLastTick,
        byte marioMode, byte marioStatus, byte actionKey)
    {
        this.timeSpent=timeSpent;
        this.timeLeft=timeLeft;
        this.intermediateReward=intermediateReward;
        this.intermediateRewardWonLastTick=intermediateRewardWonLastTick;
        this.intermediateRewardWonLast6Ticks=intermediateRewardWonLast6Ticks;
        this.intermediaRewardWonFuture6Ticks=intermediaRewardWonFuture6Ticks;
        this.intermediaRewardWonFuture12Ticks=intermediaRewardWonFuture12Ticks;
        this.intermediaRewardWonFuture24Ticks=intermediaRewardWonFuture24Ticks;
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
        this.enemyWasKilledBin=enemyWasKilledBin;
        this.marioWasInjuredBin=marioWasInjuredBin;
        this.isSlopeDown=isSlopeDown;
        this.coinsGainedLastTick=coinsGainedLastTick;
        this.marioMode=marioMode;
        this.marioStatus=marioStatus;
        this.actionKey=actionKey;
    }
}