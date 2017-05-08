package GraphAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class TemporalShiftCount {
	
	BufferedReader br;
	File f;
	public static int lembda = 4;
	
	Hashtable <String, Integer> patternCache;
	
	
	public TemporalShiftCount (String file) {
		
		f = new File (file);
		try {
			br = new BufferedReader (new FileReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		patternCache = new Hashtable<String, Integer>();
	}
	
	public void countTemporalPatterns () {
		
		String line;
		String[] tokens;
		Integer count;
		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf("Epoch") > 0) {
					tokens = line.split(",");
					count = (Integer)patternCache.get(tokens[0]);
					if (count == null) {
						patternCache.put(tokens[0], new Integer (1));
					}
					else {
						patternCache.put(tokens[0], new Integer (count.intValue() + 1));
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printTemporalPatterns();
		
	}
	
	public void printTemporalPatterns() {
		
		Set<String> keySet = patternCache.keySet();
		Iterator it = keySet.iterator();
		String key;
		int recurringPatterns = 0;
		Integer count;
		int maxRecurring=0;
		String maxKey=null;
		for (int i=0;i<keySet.size();i++) {
			
			key = (String)it.next();
			count = (Integer) patternCache.get(key);
			if (count.intValue() >1) {
				recurringPatterns++;
				System.out.println("Recurring Pattern:" + key + ", count:" + count.intValue());
				if (count.intValue() > maxRecurring) {
					maxKey = key;
					maxRecurring = count.intValue();
				}
			}
		}
		
		findSpatialDeviation (keySet);
		
		System.out.println("Total patterns:" + keySet.size());
		System.out.println("Recurring Patterns:" + recurringPatterns);
		System.out.println("Max times a pattern recurred:" + maxRecurring + ", pattern:" + maxKey);
		
	}
	
	
	public void findSpatialDeviation (Set<String> keySet) {
		
		String key1, key2;
		ArrayList<String> keyList = new ArrayList<String>();
		Iterator it = keySet.iterator();
		int spatialCounter = 0;
		
		for (int i=0;i<keySet.size();i++) {
			keyList.add((String)it.next());
		}
		
		for (int i=0;i<keyList.size();i++) {
			key1 = keyList.get(i);
			for (int j=i+1;j<keyList.size();j++) {
				key2 = keyList.get(j);
				if ( minDistance(key1, key2) < lembda) {
					System.out.println(key1 + ":" + key2);
					spatialCounter++;
				}
			}
		}
		System.out.println("Total edges:" + spatialCounter);
	}
	
	
	public int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length(); 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
		return dp[len1][len2];
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long time = System.currentTimeMillis(); 
		
		//TemporalShiftCount tsc = new TemporalShiftCount ("D:\\manoj\\3gBI\\Novartis\\paper\\data\\output_LongPatterns_dummyData_2_Dense_5_5_4000_50_0_4_3.txt");
		TemporalShiftCount tsc = new TemporalShiftCount ("E:\\manoj\\3gBI\\Novartis\\data\\TimeVariantBG\\output_ShortPattern_Names_ds_sales_new_1_4_3_8000_5.txt");
		tsc.countTemporalPatterns();
		
		System.out.println("Time taken:" + (System.currentTimeMillis() - time));
		System.out.println("Done");
	}
}