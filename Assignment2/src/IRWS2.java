
import java.io.BufferedWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRWS2 {

	public static int slNo = 0;
	public static BufferedWriter bw;

	public static void main(String[] args) {
		try {
			// Reading files
			// first argument-file
			String inputPath = args[0];
			List<String> input = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);

			// second argument- weight file
			String weightPath = args[1];
			List<String> weightInput = Files.readAllLines(Paths.get(weightPath), StandardCharsets.UTF_8);
			int weightSize = weightInput.size();

			// output file to write top 100 docs of each fuson technique
			FileWriter outputFile = new FileWriter("OUTPUT2.txt");
			bw = new BufferedWriter(outputFile);
			HashMap<String, String> weightMap = new HashMap<String, String>();

			// storing weights
			for (int i = 0; i < weightSize; i++) {

				String weightData[] = weightInput.get(i).toString().split("\t");
				for (String weightData1 : weightData) {

					String[] innerData = weightData1.toString().split(";");
					weightMap.put(innerData[0].toString(), innerData[1]);
				}
			}
			bw.write("-------------TOP 100 FROM EACH FUSION TEHCNIQUE----------------");
			bw.newLine();
			bw.newLine();

			System.out.println("-------------TOP 100 FROM EACH FUSION TEHCNIQUE----------------");
			System.out.println();

			// Calling the methods
			findInterLeaving(input);
			findCombSum(input);
			findLCM(input, weightMap);
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(IRWS2.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// Interleaving method for fusion
	static void findInterLeaving(List<String> inputList) {
		try {
			System.out.println("----------Interleaving------------");
			System.out.println("  SLNO      " + "   Document   ");

			bw.write("----------Interleaving------------");
			bw.newLine();
			bw.write("  SLNO      " + "   Document   ");
			bw.newLine();
			int inputSize = inputList.size();
			int topSize = 0;
			// LinkedHashMap retains the order
			LinkedHashMap<String, ArrayList<String>> interMap = new LinkedHashMap<String, ArrayList<String>>();
			for (int i = 0; i < inputSize; i++) {

				String data[] = inputList.get(i).toString().split("\t");
				for (int k = 0; k < data.length; k++) {
					if (data[k].toString() != null && data[k].toString().length() > 0) {
						String innerData[] = data[k].toString().split(";");
						if (interMap.containsKey(innerData[0].toString())) {
							ArrayList<String> dataList = (ArrayList<String>) interMap.get(innerData[0].toString());
							dataList.add(innerData[1].toString());
							topSize = dataList.size();
						} else {
							ArrayList<String> newData = new ArrayList<String>();
							newData.add(innerData[1].toString());
							interMap.put(innerData[0].toString(), newData);

						}
					}
				}

			}
			int keySize = interMap.size();
			ArrayList<Object> finalList = new ArrayList<Object>();
			int top = 0;
			for (int k = 0; k < topSize; k++) {
				if (top != 100) {
					Iterator<String> ite1 = interMap.keySet().iterator();
					for (int j = 0; j < keySize; j++) {
						// to print top 100 for Interleaving
						if (top < 100) {
							String key1 = (String) ite1.next();
							ArrayList<String> dataList = ((ArrayList<String>) interMap.get(key1));
							if (dataList.size() > k) {
								if (finalList.contains(dataList.get(k))) {
									// to check if the document already exists
									finalList.add(checkExist(finalList, k, dataList));
								} else {
									finalList.add(dataList.get(k));
									slNo++;
									System.out.println("   " + String.format("%0$-5s", (slNo)) + "     "
											+ String.format("%0$-13s", dataList.get(k)));
									bw.write("   " + String.format("%0$-5s", slNo) + "     "
											+ String.format("%0$-13s", dataList.get(k)));
									bw.newLine();
								}
							}
							top++;
						} else {
							break;
						}
					}
				} else {
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// for interleaving, to check if the document exists or not.
	static String checkExist(ArrayList<Object> list, int index, ArrayList<String> doc) {
		int listSize = list.size();
		for (int c = 0; c < listSize; c++) {
			if (index < doc.size()) {
				if (list.contains(doc.get(index))) {
					index++;
				} else {
					slNo++;
					// to print top 100 for Interleaving
					System.out.println("   " + String.format("%0$-5s", (slNo)) + "     "
							+ String.format("%0$-13s", doc.get(index).toString()));
					try {
						bw.write("   " + String.format("%0$-5s", slNo) + "     "
								+ String.format("%0$-13s", doc.get(index).toString()));

						bw.newLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return doc.get(index).toString();
				}
			}
		}
		return null;
	}

	// To fuse using CombSum fusion technique
	static void findCombSum(List<String> inputList) {
		int inputSize = inputList.size();
		// LinkedHashMap is used to maintain the order
		LinkedHashMap<String, HashMap<String, Double>> combsum = new LinkedHashMap<String, HashMap<String, Double>>();
		for (int i = 0; i < inputSize; i++) {

			HashMap<String, Double> existMap;
			String data[] = inputList.get(i).toString().split("\t");
			for (int k = 0; k < data.length; k++) {
				String innerData[] = data[k].toString().split(";");
				if (combsum.containsKey(innerData[0].toString())) {
					// temporary map to update engine run
					existMap = (HashMap<String, Double>) combsum.get(innerData[0].toString());
					existMap.put(innerData[1].toString(), Double.valueOf(innerData[2].toString()));

				} else {
					HashMap<String, Double> newMap = new HashMap<String, Double>();
					newMap.put(innerData[1].toString(), Double.valueOf(innerData[2].toString()));
					combsum.put(innerData[0].toString(), newMap);

				}
			}
		}
		// Results are pushed to finalMap in the end
		HashMap<String, Double> finalMap = new HashMap<String, Double>();
		if (combsum.size() > 0) {
			Iterator<String> ite1 = combsum.keySet().iterator();
			for (int k = 0; k < combsum.size(); k++) {
				String key1 = (String) ite1.next();
				HashMap<String, Double> listMap = (HashMap<String, Double>) combsum.get(key1);

				List<Double> values = new ArrayList<Double>(listMap.values());
				Collections.sort(values);
				Iterator<String> ite2 = listMap.keySet().iterator();
				for (int p = 0; p < listMap.size(); p++) {
					String key2 = (String) ite2.next();
					// score normalization
					double normalizeScore = (Double.valueOf(listMap.get(key2).toString())
							- Double.valueOf(values.get(0).toString()))
							/ (Double.valueOf(values.get(values.size() - 1).toString())
									- Double.valueOf(values.get(0).toString()));

					// Result is finally pushed to finalmap
					if (finalMap.containsKey(key2)) {

						double normalizeScore1 = (Double.valueOf(listMap.get(key2).toString())
								- Double.valueOf(values.get(0).toString()))
								/ (Double.valueOf(values.get(values.size() - 1).toString())
										- Double.valueOf(values.get(0).toString()));

						// COMBSUM CALCULATION
						double sum = Double.valueOf(finalMap.get(key2).toString()) + normalizeScore1;
						finalMap.put(key2, sum);

					} else {
						finalMap.put(key2, normalizeScore);
					}
				}

			}
		}
		// prints top 100
		top100File(finalMap, "COMBSUM");

	}

	// Method to fuse documents using LCM fusion technique
	static void findLCM(List<String> inputList, HashMap<String, String> wList) {
		int inputSize = inputList.size();
		LinkedHashMap<String, HashMap<String, Double>> lcmMap = new LinkedHashMap<String, HashMap<String, Double>>();
		for (int i = 0; i < inputSize; i++) {

			HashMap<String, Double> existMap;
			// Splitting the data
			String data[] = inputList.get(i).toString().split("\t");
			for (int k = 0; k < data.length; k++) {

				String innerData[] = data[k].toString().split(";");
				// Storing in map
				if (lcmMap.containsKey(innerData[0].toString())) {
					existMap = (HashMap<String, Double>) lcmMap.get(innerData[0].toString());
					existMap.put(innerData[1].toString(), Double.valueOf(innerData[2].toString()));

				} else {
					HashMap<String, Double> newMap = new HashMap<String, Double>();
					newMap.put(innerData[1].toString(), Double.valueOf(innerData[2].toString()));
					lcmMap.put(innerData[0].toString(), newMap);
				}
			}
		}
		// results are pushed to finalMap in the end
		HashMap<String, Double> finalMap = new HashMap<String, Double>();
		if (lcmMap.size() > 0) {
			Iterator<String> ite1 = lcmMap.keySet().iterator();
			for (int k = 0; k < lcmMap.size(); k++) {
				String key1 = (String) ite1.next();
				HashMap<String, Double> listMap = (HashMap<String, Double>) lcmMap.get(key1);

				List<Double> values = new ArrayList<Double>(listMap.values());
				Collections.sort(values);
				Iterator<String> ite2 = listMap.keySet().iterator();
				for (int p = 0; p < listMap.size(); p++) {
					String key2 = (String) ite2.next();
					// score normalization
					double normalizeScore = (Double.valueOf(listMap.get(key2).toString())
							- Double.valueOf(values.get(0).toString()))
							/ (Double.valueOf(values.get(values.size() - 1).toString())
									- Double.valueOf(values.get(0).toString()));

					if (finalMap.containsKey(key2)) {

						double normalizeScore1 = (Double.valueOf(listMap.get(key2).toString())
								- Double.valueOf(values.get(0).toString()))
								/ (Double.valueOf(values.get(values.size() - 1).toString())
										- Double.valueOf(values.get(0).toString()));

						// LCM CALCULATION
						double sum = Double.valueOf(finalMap.get(key2).toString())
								+ (normalizeScore1 * Double.valueOf(wList.get(key1).toString()));
						finalMap.put(key2, sum);

					} else {

						finalMap.put(key2, normalizeScore * Double.valueOf(wList.get(key1).toString()));
					}
				}

			}
		}
		// prints top 100 documents
		top100File(finalMap, "LCM");

	}

	// this method is used to generate top 100 for LCM and CombSum and to display
	// the results in console and write to file
	static void top100File(HashMap<String, Double> dataMap, String name) {
		Set<Map.Entry<String, Double>> set = dataMap.entrySet();
		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(set);
		// to get the top 3 from the map using Collections.sort implementation
		Collections.sort(list,
				(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) -> o2.getValue().compareTo(o1.getValue()));
		List<Map.Entry<String, Double>> reList = null;
		if (list.size() > 100) {
			reList = list.subList(0, 100);

		} else {
			reList = list.subList(0, list.size());
		}
		// writes to file and prints to console
		try {
			bw.write("---------------" + name + "--------------------");
			bw.newLine();
			bw.write("-----------------------------------");
			bw.newLine();
			bw.write("  Rank   " + "   Document   " + "  Rating  ");
			bw.newLine();
			bw.write("-----------------------------------");
			bw.newLine();
			System.out.println("---------------" + name + "--------------------");
			System.out.println("-----------------------------------");
			System.out.println("  Rank   " + "   Document   " + "  Rating  ");
			System.out.println("-----------------------------------");
			for (int r = 0; r < reList.size(); r++) {
				System.out.println("   " + String.format("%0$-5s", (r + 1)) + "     "
						+ String.format("%0$-13s", (((Map.Entry<String, Double>) reList.get(r)).getKey()).toString())
						+ String.format("%0$-1s", (((Map.Entry<String, Double>) reList.get(r)).getValue()).toString()));
				bw.write("   " + String.format("%0$-5s", (r + 1)) + "     "
						+ String.format("%0$-13s", (((Map.Entry<String, Double>) reList.get(r)).getKey()).toString())
						+ String.format("%0$-1s", (((Map.Entry<String, Double>) reList.get(r)).getValue()).toString()));
				bw.newLine();
			}
			System.out.println("==================================");
			bw.newLine();
			bw.write("==================================");
			bw.newLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
