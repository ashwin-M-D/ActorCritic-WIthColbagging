package neuralnet;

public class dataClass {
	
		private double[] input = new double[4]; //revised by k.yamauchi
		private double reward = 0; // revised by k.yamauchi
		private int jumpState = 0;
		private double[] trainData = new double[8];//revised by k.yamauchi
		private double[] testData =  new double[8];//revised by k.yamauchi
		
	
	public synchronized void setData(double[] input, double reward, int jumpState, int gameState) {// revised by k.yamauchi
		this.input = input;
		this.reward = reward;
		this.jumpState = jumpState;
		int offset = 0;
		trainData[offset] = this.input[0];//speed
		trainData[++offset] = this.input[1];//posX of dino
		trainData[++offset] = this.input[2];//width of cactus
		trainData[++offset] = this.input[3];//height of cactus
//		trainData[++offset] = this.input[4];//posY of dino
		if (this.jumpState == 1) {
			trainData[++offset] = 1;
		}else {
			trainData[++offset] = 0;
		}
		trainData[++offset] = this.reward;
		trainData[++offset] = gameState;

		testData[0] = this.input[0];
		testData[1] = this.input[1];
		testData[2] = this.input[2];
		testData[3] = this.input[3];
//		testData[4] = this.input[4];
		if (this.jumpState == 1) {
			testData[4] = 1;
		}else {
			testData[4] = 0;
		}
		testData[5] = this.reward;
		testData[6] = gameState;
		
	}
	
	public synchronized double[] getDataTest() {
		return testData;
	}
	
	public synchronized double[] getDataTrain() {
		return trainData;
	}
	
	public synchronized void setState(int jumpState){
		this.jumpState = jumpState;
	}
	
	public synchronized int getState() {
		return jumpState;
	}
	
}
