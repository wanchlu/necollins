import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

//import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Global {
	public static final double pmin = 0.95;
}
class CountHash {
	private Map<String,Integer> LCount= new HashMap<String,Integer>();	// = Count(key,L)
	private Map<String,Integer> PCount= new HashMap<String,Integer>(); // = Count(key,P)
	private Map<String,Integer> OCount= new HashMap<String,Integer>();	// = Count(key,O)
	private Map<String,Integer> TCount= new HashMap<String,Integer>(); // = Count(key)
	public CountHash (TrainSet trainSet) {
		 for (TrainExample e: trainSet.getLabeledExamples()) {
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
	public void deleteEntry (Type t, String f){
		switch (t) {
		case L: LCount.remove(f); break;
		case P: PCount.remove(f); break;
		case O: OCount.remove(f); break;
		default: TCount.remove(f); break;
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
}

class TestExample {
	
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
	//return the iith example
	public TrainExample getTrainExample (int ii) {
		return trainexamples.get(ii);
	}
	// return how many examples got labeled
	public int LabelUsingDL (DecisionList dl) {
		int count = 0;
		labeledexamples = new ArrayList<TrainExample> ();		
		for (int i = 0; i < trainexamples.size(); i++) {	// for every example
			Boolean labeled = false;
			for (int j = 0; j < dl.size(); j++) {	// for every rule
				Rule rule = dl.getRule(j);
				for (String f:this.getTrainExample(i).getFeatures()){	// for every feature of the example
					if (f.equals(rule.getFeature()) ) {	// feature exists in a rule
						getTrainExample(i).setType(rule.getType());	// label the example
						labeledexamples.add(this.getTrainExample(i));
						labeled = true;
						count ++;
		/*				System.out.print("labeling ");
						getTrainExample(i).print();
						System.out.print("using rule ");
						rule.print();						
			*/		}
					if (labeled) break;
				}
				if (labeled) break;
			}
		}
		return count;
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
		System.out.println(feature+" -> "+type+"\t"+frequency+"\t"+strength);
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
	private DecisionList candidateRules (CountHash countHash, Type t, int n) {
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
	private DecisionList candidateRules (CountHash countHash, Type t, int n) {
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
	private DecisionList candidateRules (CountHash countHash, Type t, int n) {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1. set n= = 5
		int n = 5;
		// 2. Initialization
		SpellingDecisionList spellingDL = new SpellingDecisionList(
				"/Users/wanchen/github/necollins/DLCoTrain/necollinssinger/all.seed.rules");
		//spellingDL.print();
		TrainSet trainSet = new TrainSet (
				"/Users/wanchen/github/necollins/DLCoTrain/necollinssinger/all.train.ex");
		// 3. Label the training set using the current set of spelling rules
		System.out.println("\nTotally labeled "+trainSet.LabelUsingDL(spellingDL)+" examples");
		// 4. Use labeled examples to induce contextual DL
		ContextualDecisionList contextualDL = new ContextualDecisionList();
		CountHash countHash = new CountHash (trainSet);
		//System.out.println(countHash.getTypeCount(Type.valueOf("P"), "X2_Mr.")+" "+countHash.getTotalCount( "X2_Mr."));
		contextualDL.induceUsingLabeledSet(n, countHash).print();
	}

}
