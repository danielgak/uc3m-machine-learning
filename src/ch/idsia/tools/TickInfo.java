package ch.idsia.tools;

/**
 * Esta clase representa la información de entrenamiento recogida en un tick
 */
public class TickInfo {

	private int coinsGained;
	private int killsTotal;
	private String example[];

	public TickInfo(int coinsGained, int killsTotal) {
		this.coinsGained = coinsGained;
		this.killsTotal = killsTotal;
		this.example = new String[]{"", ""};
	}

	public TickInfo(int coinsGained, int killsTotal, String example[]) {
		this.coinsGained = coinsGained;
		this.killsTotal = killsTotal;
		this.example = example;
	}

	public void setExample(String example[]) {
		this.example = example;
	}

	public String getExampleString() {
		return example[0] + example[1];
	}

	public void setFutureTickInfo(int coinsGained, int killsTotal) {
		int coinsGainedN = coinsGained - this.coinsGained;
		int killsTotalN = killsTotal - this.killsTotal;
		example[0] += "," + coinsGainedN + "," + killsTotalN;
	}
}