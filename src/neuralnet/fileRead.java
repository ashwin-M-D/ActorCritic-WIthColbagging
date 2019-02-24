package neuralnet;

import java.io.BufferedReader;
import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
//import java.nio.CharBuffer;

public class fileRead {// revised by k.yamauchi
	private final boolean DEBUG = false;
	BufferedReader in=null;
	
	public fileRead(FileReader fr) {
		this.in = new BufferedReader(fr);
	}
	
	public double readDouble() throws IOException{
		double arr=0D;
		String str;//read line is stored
		try {
			if ((str = in.readLine())==null) System.exit(1);// one line is read into str.
			this.Log("readDouble() " + str);
			int size1 = Integer.parseInt(str);
			for (int i=0; i<size1; i++) {
				if ((str = in.readLine())==null) System.exit(1);
				this.Log("readDouble() " + str);
				arr = Double.parseDouble(str);
			}
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return arr;
	}
	
	public double[] read1DArrayDouble() throws IOException{
		double[] arr=null; // = {{0,0,0}};
		String str;//read line is stored
		try {
			if ((str = in.readLine())==null) System.exit(1);// one line is read into str.
			this.Log("read1DArrayDouble() " + str);
			int size1 = Integer.parseInt(str);
			arr = new double[size1]; // allocate 2dim array
			for (int i=0; i<size1; i++) {
				if ((str = in.readLine())==null) System.exit(1);
				double data = Double.parseDouble(str);
				arr[i] = data;
			}
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return arr;
	}
			
	public double[][] read2DArrayDouble() throws IOException{
		double[][] arr=null; // = {{0,0,0}};
		String str;//read line is stored
		try {
			if ((str = in.readLine())==null) System.exit(1);// one line is read into str.
			this.Log("fileReader.read2DArrayDouble() " + str);
			int size1 = Integer.parseInt(str);
			if ((str = in.readLine())==null) System.exit(1);
			this.Log("fileReader.read2DArrayDouble() " + str);
			int size2 = Integer.parseInt(str);
			this.Log("fileReader.read2DArrayDouble() size1=" + size1 + ", size2=" + size2);
			arr = new double[size1][size2]; // allocate 2dim array
			for (int i=0; i<size1; i++) {
				for (int j=0; j<size2; j++) {
					if ((str = in.readLine())==null) {
						this.Log("fileReader.read2DArrayDouble() terminated at (i="+i+", j="+j+")");
						System.exit(1);
					}
					this.Log("fileReader.read2DArrayDouble() " + str);
					double data = Double.parseDouble(str);
					arr[i][j] = data;
				}
			}
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return arr;
	}
	
	void Log(String log) {
		if (this.DEBUG) System.out.println("fileRead." + log);
	}
}
