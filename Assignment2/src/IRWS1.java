import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IRWS1 {

	public static BufferedWriter bw;
	public static LinkedHashMap<String, Double> top3Map = new LinkedHashMap<String, Double>();

	public static void main(String[] args) {
		try {
			// TODO code application logic here
			String inputPath = new File(args[0]).getPath();
			List<String> input = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);
			// output file to write the measurements and the top three engines
			FileWriter outputFile = new FileWriter("OUTPUT1.txt");
			bw = new BufferedWriter(outputFile);
			// Calling the methods by sending the argument which is the list
			findPrecisionAndRecall(input);
			findPrecisionAtPosition5(input);
			findInterpolatedPrecision(input);
			// findPrecisionAtR05(input);
			findAveragePrecision(input);
			findMeanAveragePrecision(input);

			topThreeFile(top3Map);
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(IRWS1.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	// Method to find precision and recall using the formula RELRET/RET
	static void findPrecisionAndRecall(List<String> inputList) throws IOException {
		int inputSize = inputList.size();
		List<PrecisionRecallTable> resultList = new ArrayList<PrecisionRecallTable>();
		for (int i = 0; i < inputSize; i++) {
			String[] data = inputList.get(i).toString().split(";");
			// retrieved documents
			int RET = data[2].length();
			// Retrieved docs that are relevant
			int RELRET = RET - data[2].replace("R", "").length();
			// adding recall and precision to the list for printing output
			// data[3] - total no.of relevant docs
			resultList.add(new PrecisionRecallTable(data[1], data[0], (Double.valueOf(RELRET) / Double.valueOf(RET)),
					(Double.valueOf(RELRET) / Double.valueOf(data[3]))));
		}
		printPrecisionAndRecall(resultList);
	}

	// Method to find precision at position 5
	// P@5 is found by finding the number of relevant documents till 5
	static void findPrecisionAtPosition5(List<String> inputList) throws IOException {
		int inputSize = inputList.size();
		List<PrecisionRecallTable> PrecisionAtPosition5 = new ArrayList<PrecisionRecallTable>();
		for (int i = 0; i < inputSize; i++) {
			// Splitting the list to 3 parts and storing each in a string array
			// data[1] -run,data[2]-document list,data[3]-number.of relevant document
			String[] data = inputList.get(i).toString().split(";");
			int P = 5 - (data[2].substring(0, 5)).replace("R", "").length();
			PrecisionAtPosition5.add(new PrecisionRecallTable(data[1], data[0], (Double.valueOf(P) / 5), 0));
			// O is passed because recall is not needed
		}
		printPrecisionAt5(PrecisionAtPosition5);
	}

	// Method to find average precision of each run
	static void findAveragePrecision(List<String> inputList) {
		int inputSize = inputList.size();
		List<PrecisionRecallTable> AveragePrecision = new ArrayList<PrecisionRecallTable>();
		Double rel;
		for (int i = 0; i < inputSize; i++) {
			// Splitting the list to 3 parts and storing each in a string array
			// data[1] -run,data[2]-document list,data[3]-number.of relevant document
			String[] data = inputList.get(i).toString().split(";");
			rel = Double.valueOf(data[3]);
			double p = 0;
			int rCount = 0;
			for (int k = 1; k < data[2].length() + 1; k++) {
				if (String.valueOf(data[2].charAt(k - 1)).equalsIgnoreCase("R")) {
					rCount++;// no.of relevant documents that are retrieved
					p = p + Double.valueOf(rCount) / Double.valueOf(k);
				}
			}
			AveragePrecision.add(new PrecisionRecallTable(data[1], data[0], p / rel, 0));
			// finding the average by dividing precision by the total relevant-retrieved
			// document number

		}
		printAveragePrecision(AveragePrecision);
	}

	// Method to find mean average precision
	static void findMeanAveragePrecision(List<String> inputList) {
		int inputSize = inputList.size();
		List<PrecisionRecallTable> MeanavgPrecision = new ArrayList<PrecisionRecallTable>();
		HashMap<String, Double> meanMap = new HashMap<String, Double>();
		HashMap<String, HashMap<String, Double>> finalMap = new HashMap<String, HashMap<String, Double>>();
		double rel;
		for (int i = 0; i < inputSize; i++) {
			// Splitting the list to 3 parts and storing each in a string array
			// data[1] -run,data[2]-document list,data[3]-number.of relevant document
			final String[] data = inputList.get(i).toString().split(";");
			rel = Double.valueOf(data[3]); // relevant documents
			int rCount = 0;
			double p = 0;
			meanMap = new HashMap<String, Double>();
			for (int k = 1; k < data[2].length() + 1; k++) {
				if (String.valueOf(data[2].charAt(k - 1)).equalsIgnoreCase("R")) {
					rCount++;// no.of relevant documents that are retrieved
					p = p + Double.valueOf(rCount) / Double.valueOf(k);

				}
			}
			// finding the average by dividing precision by the total relevant-retrieved
			// document number
			meanMap.put(data[0].toString(), p / rel);
			HashMap<String, Double> existMap;
			if (finalMap.containsKey(data[1].toString())) {
				existMap = (HashMap<String, Double>) finalMap.get(data[1].toString());
				existMap.put(data[0].toString(), p / rel);
			} else {
				finalMap.put(data[1], meanMap);
			}
		}
		if (finalMap != null && finalMap.size() > 0) {
			Iterator<String> ite1 = finalMap.keySet().iterator();
			for (int k = 0; k < finalMap.size(); k++) {
				String key1 = (String) ite1.next();
				HashMap<String, Double> listMap = (HashMap<String, Double>) finalMap.get(key1);
				Iterator<String> ite2 = listMap.keySet().iterator();
				double engineSum = 0;
				for (int p = 0; p < listMap.size(); p++) {
					String key2 = (String) ite2.next();

					engineSum = engineSum + Double.valueOf(listMap.get(key2).toString());
				}

				// Mean average precision is found by dividing the
				// mean average precision is also used to find top3 docs
				top3Map.put(key1, engineSum / listMap.size());
				MeanavgPrecision.add(new PrecisionRecallTable(key1, "", engineSum / listMap.size(), 0));

			}

		}
		printMeanAveragePrecision(MeanavgPrecision);

	}

	// method to find interpolated precision- 11 precision points for each run
	static void findInterpolatedPrecision(List<String> inputList) {
		int inputSize = inputList.size();
		ArrayList<StringBuilder> precList = null;
		List<PrecisionRecallTable> interPolList = new ArrayList<PrecisionRecallTable>();
		StringBuilder posIndex = new StringBuilder();
		ArrayList<Double> precisionList = new ArrayList<Double>();
		ArrayList<Double> recallList = new ArrayList<Double>();
		ArrayList<Double> finalList = new ArrayList<Double>();
		// Standard recall points in percentage
		double graph[] = { 100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 0 };
		for (int i = 0; i < inputSize; i++) {
			// Splitting the list to 3 parts and storing each in a string array
			// data[1] -run,data[2]-document list,data[3]-number.of relevant document
			String[] data = inputList.get(i).toString().split(";");
			int rCount = 0;
			precList = new ArrayList<StringBuilder>();
			StringBuilder interString = new StringBuilder();
			interString.append("(");
			precisionList = new ArrayList<Double>();
			finalList = new ArrayList<Double>();
			recallList = new ArrayList<Double>();
			for (int k = 1; k < data[2].length() + 1; k++) {

				if (String.valueOf(data[2].charAt(k - 1)).equalsIgnoreCase("R")) {
					posIndex = new StringBuilder();
					rCount++;
					// to print the output as point co-ordinates

					interString.append(Double.valueOf(rCount) / Double.valueOf(k));

					if (k < data[2].length())
						interString.append(",");

					posIndex.append("(");
					posIndex.append(Double.valueOf(rCount) / Double.valueOf(k));
					precisionList.add(roundAvoid((Double.valueOf(rCount) / Double.valueOf(k)), 3));
					posIndex.append(",");
					posIndex.append(Double.valueOf(rCount) / Double.valueOf(data[3].toString()));
					recallList.add(roundAvoid(
							(Double.valueOf((100 * ((Double.valueOf(rCount) / Double.valueOf(data[3].toString())))))),
							2));
					posIndex.append(")");
					precList.add(posIndex);
				}
			}

			for (int g = 0; g < graph.length; g++) {
				if (g == 0) {
					if (findRecallbetweenRange(recallList, graph[0], graph[0], precisionList) == 0) {
						finalList.add(0.0);
					} else {
						finalList.add(precisionList.get(recallList
								.indexOf(findRecallbetweenRange(recallList, graph[0], graph[0], precisionList))));
					}
				} else {
					if (findRecallbetweenRange(recallList, graph[g], graph[g - 1], precisionList) == 0) {
						finalList.add(0.0);
					} else {
						finalList.add(precisionList.get(recallList
								.indexOf(findRecallbetweenRange(recallList, graph[g], graph[g - 1], precisionList))));
					}

				}
			}
			interString.append(")");

			interPolList.add(new PrecisionRecallTable(data[1], data[0], finalList));
		}
		// This function also print P@R=0.5, because it uses the concept of interpolated
		// precision.
		printInterPolatedandPatR(interPolList);
	}

	// Checks if there is a recall point between the standard range.
	static double findRecallbetweenRange(ArrayList<Double> recallList, double min, double max,
			ArrayList<Double> precisonList) {
		double recallValue = 0;
		for (int g = 0; g < recallList.size(); g++) {
			// rounding is done because sometimes without rounding, double values can have
			// issues

			// If each recall value is between the standard range
			if (roundAvoid(Double.valueOf(recallList.get(g).toString()), 2) >= (roundAvoid(min, 2))
					&& roundAvoid(Double.valueOf(recallList.get(g).toString()), 2) < (roundAvoid(max, 2))) {

				double maxValue = findRecallbetweenRangeMaximum(recallList, min, 100, precisonList);
				if (maxValue > recallValue) {
					recallValue = maxValue;
				}
			} else {
				double maxValue = findRecallbetweenRangeMaximum(recallList, min, 100, precisonList);
				if (maxValue > recallValue) {
					recallValue = maxValue;
				}

			}
		}
		return recallValue;
	}
        // to find the recall in the range
	static double findRecallbetweenRangeMaximum(ArrayList<Double> recallList, double min, double max,
			ArrayList<Double> precisonList) {
		double recallValue = 0;
		for (int g = 0; g < recallList.size(); g++) {
			if (roundAvoid(Double.valueOf(recallList.get(g).toString()), 2) >= (roundAvoid(min, 2))
					&& roundAvoid(Double.valueOf(recallList.get(g).toString()), 2) < (roundAvoid(max, 2))) {
				if (Double.valueOf(precisonList
						.get(recallList.indexOf(roundAvoid(Double.valueOf(recallList.get(g).toString()), 2)))
						.toString()) > recallValue) {
					recallValue = Double.valueOf(precisonList
							.get(recallList.indexOf(roundAvoid(Double.valueOf(recallList.get(g).toString()), 2)))
							.toString());
				}

			}
		}

		return recallValue == 0.0 ? 0.0 : Double.valueOf(recallList.get(precisonList.indexOf(recallValue)).toString());
	}

	// method to perform rounding
	public static double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	// Method to find top three files using mean average precision.
	static void topThreeFile(HashMap<String, Double> dataMap) {
		Set<Map.Entry<String, Double>> set = dataMap.entrySet(); // to map engines and their mean average precision
		List<Map.Entry<String, Double>> list = new ArrayList<>(set);
		// to get the top 3 from the map using Collections.sort implementation
		Collections.sort(list,
				(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) -> o2.getValue().compareTo(o1.getValue()));

		List<Map.Entry<String, Double>> reList = null;
		if (list.size() > 3) {
			reList = list.subList(0, 3);

		} else {
			reList = list.subList(0, list.size());
		}

		try {

		int i = 1;
		// prints top 3 engines in console and writes to file
		System.out.println();
		System.out.println("--------------TOP 3 ENGINES---------------------");
		System.out.println("  SLNO " + "     Engine   ");
		System.out.println("-----------------------------------");
		bw.newLine();
		bw.write("--------------TOP 3 ENGINES---------------------");
		bw.newLine();
		bw.write("  SLNO " + "     Engine   ");
		bw.newLine();
		bw.write("-----------------------------------");
		bw.newLine();
		for (int k = 0; k < reList.size(); k++) {
			System.out.println("     " + String.format("%0$-10s", i)
					+ String.format("%0$-10s", (((Map.Entry<String, Double>) reList.get(k)).getKey()).toString()));
			bw.write("     " + String.format("%0$-10s", i)
			+ String.format("%0$-10s", (((Map.Entry<String, Double>) reList.get(k)).getKey()).toString()));
	bw.newLine();
			i++;
		}
		
			bw.write("===============================================================");
			bw.newLine();
			System.out.println("===============================================================");
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// METHODS TO FORMAT AND PRINT THE OUTPUT TO CONSOLE AND TO WRITE TO FILE

	// Function to print Interpolated Precision and P@R=0.5
	static void printInterPolatedandPatR(List<PrecisionRecallTable> interList) {
		System.out.println("------------------------------------------------------------------------------------------------");
		System.out.println("  Engine " + "     Run   " + "          InterPolated Precision(in the order of recall 100% to 0%) ");
		System.out.println("------------------------------------------------------------------------------------------------");
		try {
			bw.write("-----------------------------------------------------------------------------------------------");

			bw.newLine();
			bw.write("  Engine " + "     Run   " + "          InterPolated Precision(in the order of recall 100% to 0%)");
			bw.newLine();
			bw.write("--------------------------------------------------------------------------------------------------");
			bw.newLine();
			for (PrecisionRecallTable re : interList) {
				System.out.println(
						"     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
								+ String.format("%0$-15s", String.valueOf(re.getInterPolatedList())));
				bw.write("     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
						+ String.format("%0$-15s", String.valueOf(re.getInterPolatedList())));
				bw.newLine();
			}
			bw.newLine();
			System.out.println("=================================================================================");
			System.out.println();
			bw.write("===========================================================================================");
			bw.newLine();
			bw.newLine();
			System.out.println("----------------P@R=0.5---------------------------");
			System.out.println("  Engine " + "     Run   " + "     P@R=.05");
			System.out.println("-------------------------------------------");
			bw.write("----------------P@R=0.5---------------------------");
			bw.newLine();
			bw.write("  Engine " + "     Run   " + "     P@R=.05");
			bw.newLine();
			bw.write("-------------------------------------------");
			bw.newLine();
			for (PrecisionRecallTable re : interList) {
				System.out.println(
						"     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
								+ String.format("%0$-15s", String.valueOf(re.getInterPolatedList().get(5))));
				bw.write("     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
						+ String.format("%0$-15s", String.valueOf(re.getInterPolatedList().get(5))));
				bw.newLine();
			}
			bw.newLine();
			bw.write("===============================================================");
			bw.newLine();
			System.out.println("===============================================================");
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// prints precision and recall
	static void printPrecisionAndRecall(List<PrecisionRecallTable> resultList) {

		try {
			bw.write("--------------------------------------------------------------");

			bw.newLine();
			bw.write("---------------------------Precision and Recall-----------------------------------");
			bw.newLine();
			bw.write("  Engine " + "     Run   " + "      Precision  " + "          Recall  ");
			bw.newLine();
			bw.write("--------------------------------------------------------------");
			bw.newLine();
			System.out.println("---------------------------Precision and Recall-----------------------------------");
			System.out.println("  Engine " + "     Run   " + "      Precision  " + "         Recall  ");
			System.out.println("--------------------------------------------------------------");
			for (PrecisionRecallTable re : resultList) {
				System.out.println(
						"     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
								+ String.format("%0$-15s", String.valueOf(re.getPrecision())) + "      "
								+ String.format("%0$-15s", String.valueOf(re.getRecall())));
				bw.write("     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
						+ String.format("%0$-15s", String.valueOf(re.getPrecision())) + "      "
						+ String.format("%0$-15s", String.valueOf(re.getRecall())));
				bw.newLine();
			}
			System.out.println("===============================================================");
			System.out.println();
			bw.write("===============================================================");
			bw.newLine();
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// prints precision at 5
	static void printPrecisionAt5(List<PrecisionRecallTable> PrecisionAtPosition5) {

		try {
			bw.write("--------------P@5---------------------");

			bw.newLine();
			bw.write("  Engine " + "     Run   " + "      P@5    ");
			bw.newLine();
			bw.write("-----------------------------------");
			bw.newLine();
			System.out.println("--------------P@5---------------------");
			System.out.println("  Engine " + "     Run   " + "      P@5    ");
			System.out.println("-----------------------------------");
			for (PrecisionRecallTable re : PrecisionAtPosition5) {
				System.out.println(
						"     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
								+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.write("     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
						+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.newLine();
			}
			System.out.println("===================================");
			System.out.println();
			bw.write("===================================");
			bw.newLine();
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// prints average precision
	static void printAveragePrecision(List<PrecisionRecallTable> AveragePrecision) {
		try {
			bw.write("------------------Average Precision-------------------------");

			bw.newLine();
			bw.write("  Engine " + "     Run   " + "     Average Precision");
			bw.newLine();
			bw.write("-------------------------------------------");
			bw.newLine();
			System.out.println("------------------Average Precision-------------------------");
			System.out.println("  Engine " + "     Run   " + "     Average Precision");
			System.out.println("-------------------------------------------");
			for (PrecisionRecallTable re : AveragePrecision) {
				System.out.println(
						"     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
								+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.write("     " + String.format("%0$-10s", re.getEngine()) + String.format("%0$-10s", re.getRun())
						+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.newLine();
			}
			System.out.println("===========================================");
			System.out.println();
			bw.write("===========================================");
			bw.newLine();
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// prints mean average precision
	static void printMeanAveragePrecision(List<PrecisionRecallTable> AveragePrecision) {
		try {
			bw.write("-------------------------------------------");

			bw.newLine();
			bw.write("  Engine " + "     Mean Average Precision");
			bw.newLine();
			bw.write("-------------------------------------------");
			bw.newLine();
			System.out.println("-------------------------------------------");
			System.out.println("  Engine " + "     Mean Average Precision");
			System.out.println("-------------------------------------------");
			for (PrecisionRecallTable re : AveragePrecision) {
				System.out.println("     " + String.format("%0$-10s", re.getEngine())
						+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.write("     " + String.format("%0$-10s", re.getEngine())
						+ String.format("%0$-15s", String.valueOf(re.getPrecision())));
				bw.newLine();
			}
			System.out.println("===========================================");
			System.out.println();
			bw.write("===========================================");
			bw.newLine();
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

//This class is used to set and get values for printing table like structure.
class PrecisionRecallTable {
	String engine;
	String run;
	double precision;
	double recall;
	ArrayList<Double> interPolatedList;

	PrecisionRecallTable(String engine, String run, double precision, double recall) {
		this.engine = engine;
		this.run = run;
		this.precision = precision;
		this.recall = recall;
	}

	PrecisionRecallTable(String engine, String run, ArrayList<Double> interPolatedList) {
		this.engine = engine;
		this.run = run;
		this.interPolatedList = interPolatedList;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public ArrayList<Double> getInterPolatedList() {
		return interPolatedList;
	}

	public void setInterPolatedList(ArrayList<Double> interPolatedList) {
		this.interPolatedList = interPolatedList;
	}

}           
