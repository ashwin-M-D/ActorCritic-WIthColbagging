package neuralnet;

import java.io.FileWriter;

public class filewrite {
	public static void datawrite(double args, FileWriter fw) throws Exception {
	    String strs = String.valueOf(args);
	    fw.write(strs + "\n");
	}
	public static void datawrite(double[] args, FileWriter fw) throws Exception {
		    String[] strs = new String[args.length];
		    
		    for (int i = 0; i < strs.length; i++) {
		      strs[i] = "";
		    }
		    
		    for (int i = 0; i < args.length; i++) {
		            strs[i] = strs[i] + String.valueOf(args[i])+ "\n";
		    }
		    fw.write(String.valueOf(strs.length) + "\n"); // <-- record the size of array
		    for (int i = 0; i < strs.length; i++) {
		      fw.write(strs[i]);
		    }
	}
	public static void datawrite(double[][] args, FileWriter fw) throws Exception {
		String[] strs = new String[args.length];
		
		for (int i = 0; i < strs.length; i++) {
			strs[i] = "";
		}
    
		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < args[0].length; j++) {
				strs[i] = strs[i] + String.valueOf(args[i][j])+ "\n";
			}
		}
		fw.write(String.valueOf(args.length) + "\n"); // <-- record the size of array
		fw.write(String.valueOf(args[0].length) + "\n"); // <-- record the size of array
		for (int i = 0; i < strs.length; i++) {
			fw.write(strs[i]);
		}
	}
  
	public static void datawrite(int data, FileWriter fw) throws Exception {
	  String str = "";
	  str = "" + data;
	  fw.write(str + "\n");
	}
	public static void datawrite(int[] args, FileWriter fw) throws Exception {
	    String[] strs = new String[args.length];
	    
	    for (int i = 0; i < strs.length; i++) {
	      strs[i] = "";
	    }
	    
	    for (int i = 0; i < args.length; i++) {
	            strs[i] = strs[i] + String.valueOf(args[i])+ "\n";
	    }
	    fw.write(String.valueOf(args.length)); // <-- record the size of array
	    for (int i = 0; i < strs.length; i++) {
	      fw.write(strs[i]);
	    }
	}
	public static void datawrite(int[][] args, FileWriter fw) throws Exception {
		String[] strs = new String[args.length];

		for (int i = 0; i < strs.length; i++) {
			strs[i] = "";
		}

		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < args[0].length; j++) {
				strs[i] = strs[i] + String.valueOf(args[i][j])+ "\n";
			}
		}
		fw.write(String.valueOf(args.length) + "\n"); // <-- record the size of array
		fw.write(String.valueOf(args[0].length) + "\n"); // <-- record the size of array
		for (int i = 0; i < strs.length; i++) {
			fw.write(strs[i]);
		}
	}
  
}