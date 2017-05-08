package GraphAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;

public class TAPLoadGenerator {

	// public int NUM_USERS = 1000;
	// public int MEAN_USER_SET_SIZE = 30; //mean size of users in a set
	// public int NUM_ITEMS = 1000;
	// public int MEAN_ITEMS_SET_SIZE = 20; //mean size of users in a set
	public int NUM_PATTERNS = 1000;
	// // public int QUANTUM_SIZE = 80;
	// public int QUANTUM_SIZE = 30;
	public int NUM_STATES = 10; //Number of states assigned to edges.  
	// // public int NUMBER_OF_QUANTA = 50;
	// public int NUMBER_OF_QUANTA = 10;
	public int MIN_PATTERN_LENGTH = 3;
	public int MAX_PATTERN_LENGTH = 10;
	// public double x = 0.10; //minimum %age of transactions participating in a quantum in a pattern.. 

	public static int NUM_USERS ;
	public static int MEAN_USER_SET_SIZE ; //mean size of users in a set
	public static int NUM_ITEMS ;
	public static int MEAN_ITEMS_SET_SIZE ; //mean size of users in a set
	// public static int QUANTUM_SIZE = 80;
	public static int QUANTUM_SIZE ;
	// public static int NUMBER_OF_QUANTA = 50;
	public static int NUMBER_OF_QUANTA ;
	public double x = 0.10; //minimum %age of transactions participating in a quantum in a pattern.. 


	public ArrayList<ArrayList<Integer>> linkStructure; //links which user subset is buying which item subset;

	public Hashtable<Integer, HashSet<Integer>> USER_SETS;
	public Hashtable<Integer, HashSet<Integer>> ITEM_SETS;

	public FileOutputStream fos;
	public PrintStream pr;

	int totalEdgesCounter = 0;

	ArrayList<HashSet<String>> Quantums = new ArrayList<HashSet<String>>(); //Quantums contain the transaction in each quantum

	public TAPLoadGenerator (String[] args) {

		NUM_USERS = Integer.parseInt(args[0]);
		MEAN_USER_SET_SIZE = Integer.parseInt(args[1]); //mean size of users in a set
		NUM_ITEMS = Integer.parseInt(args[2]);
		MEAN_ITEMS_SET_SIZE= Integer.parseInt(args[3]) ; //mean size of users in a set
		QUANTUM_SIZE = Integer.parseInt(args[4]);
		NUMBER_OF_QUANTA = Integer.parseInt(args[5]);

		USER_SETS = new Hashtable<Integer, HashSet<Integer>>();

		ITEM_SETS = new Hashtable<Integer, HashSet<Integer>>();

		linkStructure = new ArrayList<ArrayList<Integer>>();

		try {
			// fos = new FileOutputStream (new File ("data_" + MEAN_USER_SET_SIZE + "_" + MEAN_ITEMS_SET_SIZE + "_" + QUANTUM_SIZE + "_" + NUMBER_OF_QUANTA + "_" + x + ".txt"));
			String fileName = "data";
			for(int i=0;i<args.length;++i){
				fileName = fileName + "_" +args[i];
			}
			// fileName = fileName + "_" + x + ".txt";
			fos=new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pr = new PrintStream(fos);

	}

	public void generateSets (int startRange, int endRange, boolean user) { //This function takes as input a range, and generate the random sets by picking the number from this range

		//double rand = Math.random();
		int items=0, itemId;

		int meanSize;

		if (user)
			meanSize = MEAN_USER_SET_SIZE;
		else
			meanSize = MEAN_ITEMS_SET_SIZE;

		HashSet<Integer> itemSet;

		for (int i=0;i<NUM_PATTERNS;i++) {

			items = (int)Math.ceil(Math.random()*(2*meanSize-1));

			itemSet = new HashSet<Integer>();

			for (int j=0;j<items;j++) {
				itemId = (int)Math.floor(Math.random()*(endRange - startRange));
				itemSet.add(new Integer(itemId));
			}
			if (user)
				USER_SETS.put(new Integer(i), itemSet);
			else
				ITEM_SETS.put(new Integer(i), itemSet);

		}
		//linkSets();
	}


	//link which user set user which item set

	public void linkSets () {

		int userSetId, itemSetId, state;
		ArrayList<Integer> link;
		for (int i=0;i<NUM_PATTERNS;i++) {

			userSetId = (int)Math.floor(Math.random()*NUM_PATTERNS);
			itemSetId = (int)Math.floor(Math.random()*NUM_PATTERNS);
			state = (int)Math.floor(Math.random()*NUM_STATES);
			link = new ArrayList<Integer>();
			link.add(new Integer (userSetId));
			link.add(new Integer (itemSetId));
			link.add(new Integer (state));

			linkStructure.add(link);

		}

	}

	/*
	 * In this function, we generate all the quanta simultaneously..
	 * Some transactions participates in a pattern and some not..
	 * There is a minimum transaction length.. and a maximum transaction length
	 * The patterns starting at quantum i will definitely we spread upto quantum i+MinLength. After they uniformly decay so that in quantum i+Max, no transaction exist..
	 * The steady state number of transactions who participate in a pattern is x*((Min+Max -1)/2) where x% is percentage of new transactions, participating in a pattern, starting in a quantum.
	 * so if x=10%, min=3, max = 10, in steady state, we'll have a total of 60% transactions in a quantum participating in one pattern or another..
	 * Therefore, we need to set min, max and x so that the total number of participatery transaction don't exceed the quantum size (or don't exceed 100%)..  
	 */

	public void generateQuantums (boolean dense) {


		int tx_in_Pattern = (int)((double)(x*(double)QUANTUM_SIZE)), counter; //
		int patternLength;

		for (int i=0;i<NUMBER_OF_QUANTA;i++) {

			Quantums.add(new HashSet<String>());
		}

		int patternId;
		ArrayList<Integer> link; //get the link, linking user 

		for (int i=0;i<NUMBER_OF_QUANTA;i++) {

			counter = tx_in_Pattern;

			while (counter > 0) {

				patternLength = MIN_PATTERN_LENGTH + (int)Math.floor(Math.random()*(MAX_PATTERN_LENGTH - MIN_PATTERN_LENGTH + 1));
				link = linkStructure.get((int)Math.floor(Math.random()*NUM_PATTERNS)); //get a random pair of users and items

				if (i+patternLength > NUMBER_OF_QUANTA) {
					patternLength = NUMBER_OF_QUANTA - i;
				}

				if (dense)
					counter -= fillQuantumsDense(link, patternLength, i);
				else
					counter -= fillQuantums(link, patternLength, i);

			}

			counter = QUANTUM_SIZE - ((HashSet<String>)Quantums.get(i)).size();

			while (counter > 0) {
				patternLength = 1 + (int)Math.floor(Math.random()*(MIN_PATTERN_LENGTH-1));
				link = linkStructure.get((int)Math.floor(Math.random()*NUM_PATTERNS)); //get a random pair of users and items

				if (i+patternLength > NUMBER_OF_QUANTA) {
					patternLength = NUMBER_OF_QUANTA - i;
				}

				if (dense)
					counter -= fillQuantumsDense(link, patternLength, i);
				else
					counter -= fillQuantums(link, patternLength, i);

			}
			printTx (i);
			pr.println("--------------------------------------");
		}

	}

	public void printTx (int quantum) {

		HashSet<String> qu = (HashSet<String>)Quantums.get(quantum);
		Iterator it = qu.iterator();
		for (int i=0;i<qu.size();i++) {
			pr.println(it.next());
		}
		qu.clear();
	}

	/*
	 * Each user is connected to at most one item, if users are more than items..
	 * Similarly, each  item is connected to at most one user, it items are more than the user
	 * Implying that the graph will have low density...
	 */

	public int fillQuantums (ArrayList<Integer> link, int patternLength, int quantum) {

		HashSet<Integer> userSet = USER_SETS.get((Integer)link.get(0));
		HashSet<Integer> itemSet = ITEM_SETS.get((Integer)link.get(1));
		int state = (Integer)link.get(2).intValue();

		Iterator<Integer> itUser, itItem, it;

		HashSet<String> txEdges;
		int userId, itemId, counter=0;
		it = userSet.iterator();
		for (int i=0;i<userSet.size();i++) {
			//System.out.println(it.next().intValue());
		}

		//System.out.println("-------------------------");

		it = itemSet.iterator();
		for (int i=0;i<itemSet.size();i++) {
			//System.out.println(it.next().intValue());
		}

		//System.out.println("-------------------------");

		for (int j = quantum; j < quantum + patternLength;j++) {
			txEdges = Quantums.get(j);

			itUser = userSet.iterator();
			itItem = itemSet.iterator();

			if (userSet.size() > itemSet.size()) {
				for (int k=0;k<userSet.size();k++) {

					if (k<itemSet.size()) {
						userId = itUser.next().intValue();
						itemId = itItem.next().intValue();
						System.out.println(userId + ", " + itemId);
						txEdges.add( userId + " " + state + " " + itemId);
					}
					else {
						itItem = itemSet.iterator();
						userId = itUser.next().intValue();
						itemId = itItem.next().intValue();
						System.out.println(userId + ", " + itemId);
						txEdges.add( userId + " " + state + " " + itemId);
					}
				}
			}
			else {
				for (int k=0;k<itemSet.size();k++) {
					if (k<userSet.size()) {
						userId = itUser.next().intValue();
						itemId = itItem.next().intValue();
						System.out.println(userId + ", " + itemId);
						txEdges.add( userId + " " + state + " " + itemId);

					}
					else {
						itUser = userSet.iterator();
						userId = itUser.next().intValue();
						itemId = itItem.next().intValue();
						System.out.println(userId + ", " + itemId);
						txEdges.add(userId + " " + state + " " + itemId);
					}
				}
			}

		}

		if (userSet.size() > itemSet.size()) 

			return userSet.size();
		else

			return itemSet.size();

	}

	/*
	 * Each item is connected with each user in the set..
	 */

	public int fillQuantumsDense (ArrayList<Integer> link, int patternLength, int quantum) {

		HashSet<Integer> userSet = USER_SETS.get((Integer)link.get(0));
		HashSet<Integer> itemSet = ITEM_SETS.get((Integer)link.get(1));
		int state = (Integer)link.get(2).intValue();

		Iterator<Integer> itUser, itItem, it;

		HashSet<String> txEdges;
		int userId, itemId, counter=0;
		it = userSet.iterator();
		for (int i=0;i<userSet.size();i++) {
			//System.out.println(it.next().intValue());
		}

		//System.out.println("-------------------------");

		it = itemSet.iterator();
		for (int i=0;i<itemSet.size();i++) {
			//System.out.println(it.next().intValue());
		}

		System.out.println("-------------------------");

		for (int j = quantum; j < quantum + patternLength;j++) {
			txEdges = Quantums.get(j);
			
			counter = txEdges.size();

			System.out.println("Current quantum" + j + " size:" + txEdges.size());

			itUser = userSet.iterator();
			itItem = itemSet.iterator();

			for (int k=0;k<userSet.size();k++) {
				userId = itUser.next().intValue();
				for (int l=0;l<itemSet.size();l++) {
					itemId = itItem.next().intValue();
					//System.out.println(userId + ", " + itemId);
					txEdges.add( userId + " " + state + " " + itemId);
					//System.out.println(userId + " " + state + " " + itemId);
					//counter++;
				}
				itItem = itemSet.iterator();
			}
			totalEdgesCounter+= (txEdges.size() - counter);
			System.out.println("No. of edges added:" + counter + ", user set size:" + userSet.size() + ", item set size:" + itemSet.size() + ", pattern length:" + patternLength);
			System.out.println("Updated Quantum size:" + txEdges.size());
			System.out.println("Total edges so far:" +totalEdgesCounter);
			counter = 0;
		}

		if (userSet.size() > itemSet.size()) 

			return userSet.size();
		else

			return itemSet.size();

	}


	public static void main (String[] args) {
		
		boolean dense = true;

		TAPLoadGenerator tlg = new TAPLoadGenerator(args);
		tlg.generateSets(0, 1000, true); //generating user sets
		tlg.generateSets(1000, 2000, false); //generating item sets
		tlg.linkSets();
		tlg.generateQuantums(dense);
		System.out.println("Done");
	}

}
