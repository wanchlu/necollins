// args[0]: test feature file; args[1]: test label file
public class Test {
	public static void main(String[] args) {
		// Label test set
		//TestSet testSet = new TestSet ("./DLCoTrain/necollinssinger/all.test.ex");
		TestSet testSet = new TestSet (args[0]);
		//testSet.ReadTestSetLabels("./DLCoTrain/necollinssinger/all.test.y");
		testSet.ReadTestSetLabels(args[1]);
		DecisionList finalDL = new DecisionList("./DLCoTrain/finalDL.txt");
		testSet.LabelUsingDL(finalDL);
		System.out.println("\nTest result:");

		testSet.print();
		System.out.println("\nAccuracy: "+testSet.Accuracy());
	}
}
