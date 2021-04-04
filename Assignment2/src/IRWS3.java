import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRWS3 {

	static public int partitionCount = 0;
	static LinkedHashMap<String, Double> liveScoreMap = new LinkedHashMap<String, Double>();
	static public String livefile;
	public static BufferedWriter bw;

	public static void main(String[] args) {
		try {
			// TODO code application logic here
			// reading files

			// trainingfile
			String inputPath = args[0];
			// number of sectors
			int secCount = Integer.valueOf(args[1]);
			// livefile
			livefile = args[2];
			List<String> input = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);
			int inputSize = input.size();
			// output file to write the top twenty engines after ProbFuse
			FileWriter outputFile = new FileWriter("OUTPUT3.txt");
			bw = new BufferedWriter(outputFile);
			HashMap<String, String> docMap = new HashMap<String, String>();
			for (int i = 0; i < inputSize; i++) {
				// Splitting the list to 3 parts and storing each in a string array and then put
				// in a hashmap
				// data[1] -run,data[2]-document list,data[3]-number.of relevant document
				String data[] = input.get(i).toString().split(";");
				docMap.put(data[0] + data[1], data[2]);
			}
			// probfuse method calling
			findProbfuse(docMap, secCount);
		} catch (IOException ex) {
			Logger.getLogger(IRWS3.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	// Probfuse method
	// partitionCount is the number of sectors that is accepted as command line
	// argument
	static void findProbfuse(HashMap<String, String> dataMap, int sectorCount) {
		try {
			Iterator<String> ite = dataMap.keySet().iterator();
			HashMap<Character, HashMap<Character, ArrayList<Double>>> segMap = new HashMap<Character, HashMap<Character, ArrayList<Double>>>();
			for (int c = 0; c < dataMap.size(); c++) {
				String key = (String) ite.next();
				HashMap<Character, ArrayList<Double>> existMap;
				int docListLength = dataMap.get(key).toString().length();
				//size of each segment/number of documents in each sector
				partitionCount = docListLength / sectorCount;
				if (segMap.containsKey(key.charAt(1))) {
					existMap = (HashMap<Character, ArrayList<Double>>) segMap.get(key.charAt(1));
					existMap.put(key.charAt(0),
							calculateSegIndex(regexSplit(dataMap.get(key).toString(), partitionCount)));
					// regexSplit method uses pattern matching for detection and splitting.
				} else {
					HashMap<Character, ArrayList<Double>> newMap = new HashMap<Character, ArrayList<Double>>();
					newMap.put(key.charAt(0),
							calculateSegIndex(regexSplit(dataMap.get(key).toString(), partitionCount)));
					segMap.put(key.charAt(1), newMap);
				}
			}
			// LinkedHashMap retains order of the result
			LinkedHashMap<Character, ArrayList<Double>> resultMap = new LinkedHashMap<Character, ArrayList<Double>>();
			ArrayList<Double> sumList = null;
			// calculation starts
			if (segMap != null && segMap.size() > 0) {
				Iterator<Character> ite1 = segMap.keySet().iterator();
				for (int k = 0; k < segMap.size(); k++) {
					Character key1 = (Character) ite1.next();
					HashMap<Character, ArrayList<Double>> listMap = (HashMap<Character, ArrayList<Double>>) segMap
							.get(key1);

					Iterator<Character> ite2 = listMap.keySet().iterator();
					int w = 0;
					sumList = new ArrayList<Double>();
					for (int p = 0; p < listMap.size(); p++) {
						Character key2 = (Character) ite2.next();

						if (sumList != null && sumList.size() > 0) {
							int listList = ((ArrayList<Double>) listMap.get(key2)).size();
							for (int n = 0; n < listList; n++) {
								ArrayList<Double> tempList = ((ArrayList<Double>) listMap.get(key2));
								sumList.set(n, (Double.valueOf(sumList.get(n).toString())
										+ Double.valueOf(tempList.get(n).toString())) / segMap.size());
							}
						} else {
							sumList = (ArrayList<Double>) listMap.get(key2);
						}
						w++;
					}
					if (w > 1) {
						resultMap.put(key1, sumList);
					}

				}
			}

			// To apply the ProbFuse to livefile
			List<String> live = Files.readAllLines(Paths.get(livefile), StandardCharsets.UTF_8);

			// Segmentation
			// Depending on the number of sectors, the documents are divided into segments
			int liveSize = live.size();
			for (int i = 0; i < liveSize; i++) {
				String data[] = live.get(i).toString().split(";");
				Iterator<Character> reSet = resultMap.keySet().iterator();
				for (int k = 0; k < resultMap.size(); k++) {
					Character key1 = (Character) reSet.next();
					if (key1.toString().equalsIgnoreCase(data[0].toString())) {
						String score[] = data[1].replaceAll("[\\[\\]]", "").split(",");
						ArrayList<Double> scoreList = (ArrayList<Double>) resultMap.get(key1);
						int start = 0, end = partitionCount;
						for (int m = 0; m < scoreList.size(); m++) {
							String[] array1 = Arrays.copyOfRange(score, start, end);
							for (int n = 0; n < array1.length; n++) {
								if (liveScoreMap.containsKey(array1[n])) {
									double sum = Double.valueOf(liveScoreMap.get(array1[n]).toString())
											+ (Double.valueOf(scoreList.get(m).toString()) / (m + 1));
									liveScoreMap.put(array1[n], sum);
								} else {
									liveScoreMap.put(array1[n], Double.valueOf(scoreList.get(m).toString()) / (m + 1));
								}
							}
							start = end;
							end = end + partitionCount;
						}
					}
				}

			}

			// calls the method to print result
			printResult();
			bw.flush();
			bw.close();

		} catch (IOException ex) {
			Logger.getLogger(IRWS3.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	// prints 20 documents after Probfuse
	static void printResult() {

		Set<Entry<String, Double>> set = liveScoreMap.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		Collections.sort(list,
				(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) -> o2.getValue().compareTo(o1.getValue()));
		List<Map.Entry<String, Double>> reList = null;

		// To list only top 20 documents
		if (list.size() > 20) {
			reList = list.subList(0, 20);

		} else {
			reList = list.subList(0, list.size());
		}
		try {
			bw.write("---------------Probfuse--------------------");
			bw.newLine();
			bw.write("-----------------------------------");
			bw.newLine();
			bw.write("  Rank   " + "   Document   " + "  Rating  ");
			bw.newLine();
			bw.write("-----------------------------------");
			bw.newLine();
			System.out.println("---------------Probfuse--------------------");
			System.out.println("-----------------------------------");
			System.out.println("  Rank   " + "   Document   " + "  Rating  ");
			System.out.println("-----------------------------------");
			for (int r = 0; r < list.size(); r++) {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// to split and to make document list
	public static List<String> regexSplit(String stringtToSplit, int split) {
		Matcher matcher = Pattern.compile(".{1," + split + "}").matcher(stringtToSplit);
		List<String> result = new ArrayList<>();
		while (matcher.find()) {
			result.add(stringtToSplit.substring(matcher.start(), matcher.end()));

		}
		return result;
	}

	// Steps for calculating scores in probfuse
	static ArrayList<Double> calculateSegIndex(List<String> segList) {
		int segSize = segList.size();
		ArrayList<Double> valueList = new ArrayList<Double>();
		for (int j = 0; j < segSize; j++) {

			// segLength gives the length of the segment
			int segLength = segList.get(j).toString().length();
			int rcount = segLength - segList.get(j).toString().replace("R", "").length();

			valueList.add((double) rcount / segLength);

		}
		return valueList;
	}

}
