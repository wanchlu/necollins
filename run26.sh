javac -Xlint -cp DLCoTrain/bin DLCoTrain/src/DLCoTrain.java
javac -Xlint -cp DLCoTrain/bin DLCoTrain/src/Test.java
#for i in 'A' 'B' 'C' 'D' 'E' 'F' 'G' 'H' 'I' 'J' 'K' 'L' 'M' 'N' 'O' 'P' 'Q' 'R' 'S' 'T' 'U' 'V' 'W' 'X' 'Y' 'Z'
for i in 'A' 
do 
echo $i
java -cp DLCoTrain/bin DLCoTrain  "DLCoTrain/necollinssinger/alphabets/$i.train.ex" "DLCoTrain/necollinssinger/alphabets/$i.seed.rules"
java -cp DLCoTrain/bin Test "DLCoTrain/necollinssinger/alphabets/$i.test.ex" "DLCoTrain/necollinssinger/alphabets/$i.test.y"
done
