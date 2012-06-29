import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Global {
	public static final double pmin = 0.95;
	public static final int testSize = 877;
}
class CountHash {
	private Map<String,Integer> LCount= new HashMap<String,Integer>();	// = Count(key,L)
	private Map<String,Integer> PCount= new HashMap<String,Integer>(); // = Count(key,P)
	private Map<String,Integer> OCount= new HashMap<String,Integer>();	// = Count(key,O)
	private Map<String,Integer> TCount= new HashMap<String,Integer>(); // = Count(key)
	public CountHash (){
		
	}
	public CountHash (TrainSet trainSet) {
		 for (TrainExample e: trainSet.getTrainExamples()) {
			 for (String f: e.getFeatures()) {
				 LCount.put(f, 0);
				 PCount.put(f, 0);
				 OCount.put(f, 0);
				 TCount.put(f, 0);
			 }
		 }
		 for (TrainExample e: trainSet.getLabeledExamples()) {
			 Type t = e.getType();
			 for (String f: e.getFeatures()) {
				 TCount.put(f, TCount.get(f) + 1);
				 switch (t) {
				 	case L:	LCount.put(f, LCount.get(f) + 1); 
				 		break;
				 	case P: PCount.put(f, PCount.get(f) + 1); 
				 		break;
				 	case O: OCount.put(f, OCount.get(f) + 1); 
				 		break;
				 	default: break;
				 }
			 }
		 }
	}
	public void add (List<TrainExample> tel) {
		 for (TrainExample e: tel) {
			 Type t = e.getType();
			 for (String f: e.getFeatures()) {
				 TCount.put(f, TCount.get(f) + 1);
				 switch (t) {
				 	case L:	if (LCount.get(f) != null)
				 		LCount.put(f, LCount.get(f) + 1); 
				 		break;
				 	case P: if (PCount.get(f) != null)
				 		PCount.put(f, PCount.get(f) + 1); 
				 		break;
				 	case O: if (OCount.get(f) != null)
				 		OCount.put(f, OCount.get(f) + 1); 
				 		break;
				 	default: break;
				 }
			 }
		 }
	}
	public void deleteEntry (Type t, String f){
		switch (t) {
		case L: LCount.remove(f); break;
		case P: PCount.remove(f); break;
		case O: OCount.remove(f); break;
		default: TCount.remove(f); break;
		}
	}
	public void deleteMultipleEntry (DecisionList dl) {
		for (int i = 0; i < dl.size(); i++) {
			Rule r = dl.getRule(i);
			Type t = r.getType();
			String f = r.getFeature();
			this.deleteEntry(t, f);
		}
	}
	public Integer getTypeCount (Type t, String f) {
		switch (t) {
		case L:	return LCount.get(f);
		case P: return PCount.get(f);
		case O: return OCount.get(f);
		default: return 0; 	
		}
	}
	public Integer getTotalCount (String f) {
		return TCount.get(f);
	}
	public Iterator getTypeCountIterator (Type t) {
		switch (t) {
		case L: return LCount.entrySet().iterator();
		case P: return PCount.entrySet().iterator();
		case O: return OCount.entrySet().iterator();
		default: return TCount.entrySet().iterator();
		} 
	}
	public Iterator getTotalCountIterator() {
		return  TCount.entrySet().iterator();
	}
	public int size () {
		return LCount.size();
	}
}

class TestSet {
	private List<TestExample> testexamples;
	public TestSet (String filename) {
		testexamples = new ArrayList<TestExample> ();
		BufferedReader testFile = null;
		try {
			testFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
			int exampleid = 0;
			while (true) {
				String exampleLine = testFile.readLine();
				if (exampleLine == null)
					break;
				testexamples.add(new TestExample(exampleid, exampleLine));
				exampleid ++;
			}
			testFile.close();			
		} catch (IOException ioe) {
            System.out.println("Problem reading file");
            System.exit(1);
        }	
	}
	public List<TestExample> getTestExamples () {
		return testexamples;
	}
	public TestExample getTestExample (int ii) {
		return testexamples.get(ii);
	}
	public void ReadTestSetLabels (String filename) {
		BufferedReader testLabelFile = null;
		try {
			testLabelFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
			int exampleid = 0;
			while (true) {
				String line = testLabelFile.readLine();
				if (line == null)
					break;
				if (!line.equals("?"))
					testexamples.get(exampleid).setRealType(line);
				exampleid ++;
			}
			testLabelFile.close();			
		} catch (IOException ioe) {
            System.out.println("Problem reading file");
            System.exit(1);
        }	
		
	}
	public void LabelUsingDL (DecisionList dl) {
		for (int i = 0; i< testexamples.size(); i++) {
			Boolean labeled = false;
			for (int j = 0; j < dl.size(); j++) {
				Rule rule = dl.getRule(j);
				for (String f:this.getTestExample(i).getFeatures()) {
					if (f.equals(rule.getFeature())) {
						getTestExample(i).setPredType(rule.getType());
						labeled = true;
					}
					if (labeled) break;
				}
				if (labeled) break;
			}
			if (!labeled)
				getTestExample(i).setPredType("O");	// default O
				
		}
	}
	public void print (PrintWriter pw) {
		for (TestExample e: testexamples) {
			e.print(pw);
		}
	}
	public void print () {
		for (TestExample e: testexamples) {
			e.print();
		}
	}	public double Accuracy () {
		int correctCnt = 0;
		for (TestExample e: testexamples) {
			if (e.predIsCorrect()) {
				correctCnt ++;
			}
		}
		return (double) correctCnt/ (double) Global.testSize;
	}
}
class TrainSet {
	private List<TrainExample> trainexamples;
	private List<TrainExample> labeledexamples;
	public TrainSet (String filename) {
		trainexamples = new ArrayList<TrainExample> ();
		labeledexamples = new ArrayList<TrainExample> ();
		BufferedReader trainFile = null;
		try {
			trainFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
			int exampleid = 0;
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
	public List<TrainExample> getLabeledExamples () {
		return labeledexamples;
	}
	public List<TrainExample> getTrainExamples () {
		return trainexamples;
	}
	//return the iith example
	public TrainExample getTrainExample (int ii) {
		return trainexamples.get(ii);
	}
	// return list of examples newly labeled
	public List<TrainExample> LabelUsingDL (DecisionList dl) {
		List<TrainExample> newlylabeled = new ArrayList<TrainExample> ();
		for (int i = 0; i < trainexamples.size(); i++) {	// for every example
			Boolean labeled = trainexamples.get(i).isLabeled();
			if (labeled) {
				continue;
			}
			for (int j = 0; j < dl.size(); j++) {	// for every rule
				Rule rule = dl.getRule(j);
				for (String f:this.getTrainExample(i).getFeatures()){	// for every feature of the example
					if (f.equals(rule.getFeature()) ) {	// feature exists in a rule
						getTrainExample(i).setType(rule.getType());	// label the example
						labeledexamples.add(this.getTrainExample(i));
						labeled = true;
						newlylabeled.add(this.getTrainExample(i));
					}
					if (labeled) break;
				}
				if (labeled) break;
			}
		}
		return newlylabeled;
	}
}
class TrainExample {
	private Type type = null;
	private List<String> features;
	private String featureString;
	private int id;
	public TrainExample (int ii, String str) {
		id = ii;
		features = new ArrayList<String> ();
		String[] tokens = str.split("\\s+");	
		for (String t:tokens) {
			features.add(t);
		}
		featureString = str;
	}
	public int getId () { return id; }
	public List<String> getFeatures () { return features; }
	public String getFeature (int ii) {
		return features.get(ii);
	}
	public void setType (String s) { type = Type.valueOf(s); }
	public void setType (Type t) { type = t; }
	public Type getType () { return type; }
	public void print () {
		System.out.println("TrainExample "+id+": "+type+" "+featureString);
	}
	public boolean isLabeled () {
		return type!=null;
	}
}

class TestExample {
	private Type realType = null;
	private Type predType = null;
	private List<String> features;
	private String featureString;
	private int id;
	public TestExample (int ii, String str) {
		id = ii;
		features = new ArrayList<String> ();
		String[] tokens = str.split("\\s+");	
		for (String t:tokens) {
			features.add(t);
		}
		featureString = str;
	}
	public int getId () { return id; }
	public List<String> getFeatures () { return features; }
	public String getFeature (int ii) {
		return features.get(ii);
	}
	public void setRealType (String s) { realType = Type.valueOf(s); }
	public void setRealType (Type t) { realType = t; }
	public Type getRealType () { return realType; }
	public void setPredType (String s) { predType = Type.valueOf(s); }
	public void setPredType (Type t) { predType = t; }
	public Type getPredType () { return predType; }
	public boolean predIsCorrect () {
		return (realType != null && predType != null && realType == predType);
	}
	public void print () {
		System.out.println(realType+" "+predType+" "+featureString);
	}
	public void print (PrintWriter pw) {
		pw.println(realType+" "+predType+" "+featureString);
	}	
}
class Rule {
	private String feature;
	private Type type;
	private Double strength;
	private int frequency;
	public Rule (String str) {
		String[] tokens = str.split("\\s+");
		type = Type.valueOf(tokens[0]);
		feature = tokens[1];
		strength = Double.parseDouble(tokens[2]);
		frequency = 0;
	}
	public Rule (String f, Type t, Double s, int fr) {
		feature = f;
		type = t;
		strength = s;
		frequency = fr;
	}
	public String getFeature () { return feature; }
	public Type getType () { return type; }
	public Double getStrength () { return strength; } 
	public int getFrequency () { return frequency; }
	public void print () {
		System.out.println(type+" "+feature+" "+frequency+"\t"+strength);
	}
	public void print (PrintWriter pw) {
		pw.println(type+" "+feature+" "+frequency+"\t"+strength);
	}
}
class RuleStrengthComparator implements Comparator<Rule> {
	public int compare(Rule r1, Rule r2) {
		return (int) (r2.getStrength() - r1.getStrength());	// descending order
	}
}
class RuleFrequencyComparator implements Comparator<Rule> {
	public int compare(Rule r1, Rule r2) {
		return (int) (r2.getFrequency() - r1.getFrequency());	// descending order
	}
}
class DecisionList {
	protected List<Rule> rulelist;
	public DecisionList () {
		rulelist = new ArrayList<Rule> ();		
	}
	public DecisionList (List<Rule> rl) {
		rulelist = new ArrayList<Rule> ();		
		for (Rule r:rl){
			rulelist.add(r);
		}
	}
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
	public void sortByStrength () {
		Collections.sort(rulelist, new RuleStrengthComparator());
	}
	public Rule getRule (int ii) {
		return rulelist.get(ii);
	}
	public int size () {
		return rulelist.size();
	}
	public void print () {
		for (int i = 0; i < rulelist.size(); i++) {
			rulelist.get(i).print();
		}
	}
	public void print (PrintWriter pw) {
		for (int i = 0; i < rulelist.size(); i++) {
			rulelist.get(i).print(pw);
		}
	}
	public void combineDL (DecisionList dl1, DecisionList dl2) {
		rulelist = new ArrayList<Rule> ();
		rulelist.addAll(dl1.rulelist);
		rulelist.addAll(dl2.rulelist);
	}
	public void appendDL (DecisionList dl) {
		rulelist.addAll(dl.rulelist);
	}
	// return a DL of new induced rules, add to the original rulelist
	public DecisionList induceUsingLabeledSet (int n, CountHash countHash) {
		DecisionList newDL = new DecisionList();
		newDL.appendDL(candidateRules (countHash, Type.valueOf("L"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("P"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("O"), n));
		return newDL;
	}
	protected DecisionList candidateRules (CountHash countHash, Type t, int n) {
		Iterator iter = countHash.getTypeCountIterator(t);
		List<Rule> allCandidates = new ArrayList<Rule> ();
		List<Rule> firstnCandidates = new ArrayList<Rule> ();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry= (Entry<String, Integer>) iter.next();
			String f = entry.getKey();
			Integer c = entry.getValue();
			Integer Tc = countHash.getTotalCount(f);
			double strength = c.doubleValue()/Tc.doubleValue();
			if (strength > Global.pmin) {
				Rule newrule = new Rule(f,t,strength,c);
				allCandidates.add(newrule);
			}
		}
		Collections.sort(allCandidates, new RuleFrequencyComparator());
		for (int i = 0; i < allCandidates.size() && i < n; i++) {
			firstnCandidates.add(allCandidates.get(i));
			countHash.deleteEntry(t, allCandidates.get(i).getFeature());
		}
		return new DecisionList(firstnCandidates);
	}
	
}
class SpellingDecisionList extends DecisionList {
	public SpellingDecisionList (String filename) {
		rulelist = new ArrayList<Rule> ();
		BufferedReader seedFile = null;
		try {
			seedFile = new BufferedReader(new FileReader (filename));
		} catch (FileNotFoundException fnfe) {
            System.out.println("Problem opening file");
            System.exit(1);
        }
		try {
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
	// only induce contextual rules
	public SpellingDecisionList() {
		rulelist = new ArrayList<Rule> ();
	}
	public ContextualDecisionList induceUsingLabeledSet (int n, CountHash countHash) {
		ContextualDecisionList newDL = new ContextualDecisionList();
		newDL.appendDL(candidateRules (countHash, Type.valueOf("L"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("P"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("O"), n));
		return newDL;
	}
	protected DecisionList candidateRules (CountHash countHash, Type t, int n) {
		Iterator iter = countHash.getTypeCountIterator(t);
		List<Rule> allCandidates = new ArrayList<Rule> ();
		List<Rule> firstnCandidates = new ArrayList<Rule> ();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry= (Entry<String, Integer>) iter.next();
			String f = entry.getKey();
			if (f.matches("^X11_.*") || f.matches("^X01_.*")) {
				Integer c = entry.getValue();
				Integer Tc = countHash.getTotalCount(f);
				double strength = c.doubleValue()/Tc.doubleValue();
				if (strength > Global.pmin) {
					Rule newrule = new Rule(f,t,strength,c);
					allCandidates.add(newrule);
				}
			}
		}
		Collections.sort(allCandidates, new RuleFrequencyComparator());
		for (int i = 0; i < allCandidates.size() && i < n; i++) {
			firstnCandidates.add(allCandidates.get(i));
			countHash.deleteEntry(t, allCandidates.get(i).getFeature());
		}
		return new DecisionList(firstnCandidates);
	}
}
class ContextualDecisionList extends DecisionList {
	public ContextualDecisionList() {
		rulelist = new ArrayList<Rule> ();
	}
	// only induce spelling rules
	public SpellingDecisionList induceUsingLabeledSet (int n, CountHash countHash) {
		SpellingDecisionList newDL = new SpellingDecisionList();
		newDL.appendDL(candidateRules (countHash, Type.valueOf("L"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("P"), n));
		newDL.appendDL(candidateRules (countHash, Type.valueOf("O"), n));
		return newDL;
	}
	protected DecisionList candidateRules (CountHash countHash, Type t, int n) {
		Iterator iter = countHash.getTypeCountIterator(t);
		List<Rule> allCandidates = new ArrayList<Rule> ();
		List<Rule> firstnCandidates = new ArrayList<Rule> ();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry= (Entry<String, Integer>) iter.next();
			String f = entry.getKey();
			if (f.matches("^X0_.*") || f.matches("^X2_.*") || f.matches("^X5_.*") 
					|| f.matches("^X6_.*") || f.matches("^X7_.*")) {
				Integer c = entry.getValue();
				Integer Tc = countHash.getTotalCount(f);
				double strength = c.doubleValue()/Tc.doubleValue();
				if (strength > Global.pmin) {
					Rule newrule = new Rule(f,t,strength,c);
					allCandidates.add(newrule);
				}
			}
		}
		Collections.sort(allCandidates, new RuleFrequencyComparator());
		for (int i = 0; i < allCandidates.size() && i < n; i++) {
			firstnCandidates.add(allCandidates.get(i));
			countHash.deleteEntry(t, allCandidates.get(i).getFeature());
		}
		return new DecisionList(firstnCandidates);
	}
}

public class DLCoTrain {

	public static void main(String[] args) {
	
		// 1. set n= = 5
		int n = 5;
		int it = 0;
		// 2. Initialization
		SpellingDecisionList spellingDL = new SpellingDecisionList();
		ContextualDecisionList contextualDL = new ContextualDecisionList();
		ContextualDecisionList newContextualDL = new ContextualDecisionList();
		SpellingDecisionList newSpellingDL = new SpellingDecisionList(
				"./DLCoTrain/necollinssinger/all.seed.rules");		
		TrainSet spellingLabeledTrainSet = new TrainSet (
				"./DLCoTrain/necollinssinger/all.train.ex");
		TrainSet contextualLabeledTrainSet = new TrainSet (
				"./DLCoTrain/necollinssinger/all.train.ex");
		CountHash countSpellingHash = new CountHash(spellingLabeledTrainSet);
		CountHash countContextualHash = new CountHash(contextualLabeledTrainSet);
		do {
			System.out.println("\nIteration "+it+", n = "+n);
			// 3. Label the training set using the current set of spelling rules
			List<TrainExample> newlyLabeledSpellingExamples = spellingLabeledTrainSet.LabelUsingDL(newSpellingDL);
			System.out.println("\nLabeled "+newlyLabeledSpellingExamples.size()+" examples using spelling rules");
			
			// 4. Use labeled examples to induce contextual DL
			countSpellingHash.add(newlyLabeledSpellingExamples);
			newContextualDL = newSpellingDL.induceUsingLabeledSet(n, countSpellingHash);
			newContextualDL.sortByStrength();
			System.out.println("Induced "+newContextualDL.size()+" contextual rules");
			//contextualDL.print();
			
			// 5. Label the training set using the current set of contextual rules
			List<TrainExample> newlyLabeledContextualExamples = contextualLabeledTrainSet.LabelUsingDL(newContextualDL);
			System.out.println("\nLabeled "+newlyLabeledContextualExamples.size()+" examples using contextual rules");
			
			// 6. On the new labeled set, induce spelling rules
			countContextualHash.add(newlyLabeledContextualExamples);
			newSpellingDL = newContextualDL.induceUsingLabeledSet(n, countContextualHash);
			newSpellingDL.sortByStrength();
			System.out.println("Induced "+newSpellingDL.size()+" spelling rules");
			//newSpellingDL.print();
			
			// set the spelling rules to be the seed set plus the newSpellingDL
			spellingDL.appendDL(newSpellingDL);
			contextualDL.appendDL(newContextualDL);
			// 7. If n < 2500 set n = n+ 5 and return to step 3.
			n = n+ 5;
			it ++;
		} while (n < 2500);
		
		// Otherwise label the training data with the combined spelling/contextual decision list, then
		// induce a final decision list from the labeled examples where all rules are added to the DL
		DecisionList combinedDL = new DecisionList();
		combinedDL.combineDL(spellingDL, contextualDL);
		System.out.println("\nLabeled "+spellingLabeledTrainSet.LabelUsingDL(contextualDL).size()+" examples using combined rules");
		CountHash countHash = new CountHash (spellingLabeledTrainSet);
		combinedDL.appendDL(combinedDL.induceUsingLabeledSet(n, countHash));
		combinedDL.sortByStrength();
//		System.out.println("Final DL:");
//		combinedDL.print();
		try {
			FileWriter outFile = new FileWriter("./DLCoTrain/finalDL.txt");
			PrintWriter out = new PrintWriter(outFile);			
			combinedDL.print(out);

			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}	
		// Label test set
		TestSet testSet = new TestSet ("./DLCoTrain/necollinssinger/all.test.ex");
		testSet.ReadTestSetLabels("./DLCoTrain/necollinssinger/all.test.y");
		DecisionList finalDL = new DecisionList("./DLCoTrain/finalDL.txt");
		testSet.LabelUsingDL(finalDL);
		System.out.println("\nTest result:");

		testSet.print();
		System.out.println("\nAccuracy: "+testSet.Accuracy());
		
	}

}

