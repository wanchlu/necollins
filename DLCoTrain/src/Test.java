
public class Test {
	public static void main(String[] args) {
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
