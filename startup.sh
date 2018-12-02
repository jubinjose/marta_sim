## Please remember to kill the process manually after running using ps -ef 
kill $(ps aux | grep '[S]imulationService' | awk '{print $2}')
cd /home/student/git/cs6310/app
java -cp "./target/classes:./target/classes/lib/*" SimulationService "../test/test0_instruction_demo.txt" "../test/probabilities/test_evening_distibution.csv" &
x-www-browser http://localhost:4567/view.html 


