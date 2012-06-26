import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;


class TestExample {
	
}
class TrainSet {
	private List<TrainExample> trainexamples;
	public TrainSet (String filename) {
		trainexamples = new ArrayList<TrainExample> ();
		BufferedReader trainFile = null;
		try {
			trainFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
			Integer exampleid = 0;
			while (true) {
				String exampleLine = trainFile.readLine();
				if (exampleLine == null)
					break;
				trainexamples.add(new TrainExample(exampleid, exampleLine));
				exampleid ++;
			}
			trainFile.close();
			
		} catch (IOException ioe) {
            System.out.println("Problem reading file");
            System.exit(1);
        }	
	}
	// return how many examples got labeled
	public Integer LabelUsingDL (DecisionList dl) {
		Integer count = 0;
		for (int i = 0; i < trainexamples.size(); i++) {
			for (int j = 0; j < dl.size(); j++) {
				Rule rule = dl.getRule(j);
				
			}
		}
	}
}
class TrainExample {
	private Type type = null;
	private List<String> features;
	private Integer id;
	public TrainExample (Integer ii, String str) {
		id = ii;
		features = new ArrayList<String> ();
		String[] tokens = str.split("\\s+");	
		for (String t:tokens) {
			features.add(t);
		}
	}
	public Integer getId () { return id; }
	public List<String> getFeatures () { return features; }
	public void setType (String s) { type = Type.valueOf(s); }
	public Type getType () { return type; }
}

class Rule {
	private String feature;
	private Type type;
	private Double strength;
	public Rule (String str) {
		String[] tokens = str.split("\\s+");
		type = Type.valueOf(tokens[0]);
		feature = tokens[1];
		strength = Double.parseDouble(tokens[2]);
	}
	public String getFeature () { return feature; }
	public Type getType () { return type; }
	public Double getStrength () { return strength; } 
	public void print () {
		System.out.println(feature+" -> "+type+"\t"+strength);
	}
}

class DecisionList {
	private List<Rule> rulelist;
	public DecisionList (String filename) {
		rulelist = new ArrayList<Rule> ();
		BufferedReader seedFile = null;
		try {
			seedFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
			//seedFile.readLine();
			while (true) {
				String seedLine = seedFile.readLine();
				if (seedLine == null)
					break;
				rulelist.add(new Rule(seedLine));
			}
			seedFile.close();
			
		} catch (IOException ioe) {
            System.out.println("Problem reading file");
            System.exit(1);
        }	
	}
	public Rule getRule (Integer ii) {
		return rulelist.get(ii);
	}
	public Integer size () {
		return rulelist.size();
	}
	public void print () {
		for (int i = 0; i < rulelist.size(); i++) {
			rulelist.get(i).print();
		}
	}
}

public class DLCoTrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1. set n= = 5
		Integer n = 5;
		// 2. Initialization
		DecisionList spellingDL = new DecisionList(
				"/Users/wanchen/github/necollins/DLCoTrain/necollinssinger/all.seed.rules");
		//spellingDL.print();
		TrainSet trainSet = new TrainSet (
				"/Users/wanchen/github/necollins/DLCoTrain/necollinssinger/all.train.ex");
		// 3. Label the training set using the current set of spelling rules
		
	}

}
