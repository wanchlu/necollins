for i in 'A' 'B' 'C' 'D' 'E' 'F' 'G' 'H' 'I' 'J' 'K' 'L' 'M' 'N' 'O' 'P' 'Q' 'R' 'S' 'T' 'U' 'V' 'W' 'X' 'Y' 'Z'
do 
echo $i
grep "X0_$i" all.train.ex >"alphabets/$i.train.ex"
grep "X0_$i" all.train.ex  | wc -l
grep  "X0_$i" all.test.exy | cut -f1 >"alphabets/$i.test.y"
grep  "X0_$i" all.test.exy | cut -f2 >"alphabets/$i.test.ex"
cat "alphabets/$i.test.y" |wc -l
done
