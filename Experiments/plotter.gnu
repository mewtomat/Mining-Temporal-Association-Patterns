set terminal png
set ylabel "Time(s)"
set autoscale
set style data linespoints

set datafile separator ","

set output 'num_users.png'
set xlabel "Number of Users"
set title "Time Taken Vs Number of Users"
plot "num_users" using 1:7 title "TAPFinder", "num_users" using 1:8 title "SeqMiner" 

set output 'mean_user_set_size.png'
set xlabel "Mean User Set Size"
set title "Time Taken Vs Mean User Set Size"
plot "mean_user_set_size" using 2:7 title "TAPFinder", "mean_user_set_size" using 2:8 title "SeqMiner" 

set output 'num_items.png'
set xlabel "Number of Items"
set title "Time Taken Vs Number of Items"
plot "num_items" using 3:7 title "TAPFinder", "num_items" using 3:8 title "SeqMiner" 

set output 'mean_items_set_size.png'
set xlabel "Mean Items Set Size"
set title "Time Taken Vs Mean Items Set Size"
plot "mean_items_set_size" using 4:7 title "TAPFinder", "mean_items_set_size" using 4:8 title "SeqMiner" 

set output 'quantum_size.png'
set xlabel "Quantum Size"
set title "Time Taken Vs Quantum Size"
plot "quantum_size" using 5:7 title "TAPFinder", "quantum_size" using 5:8 title "SeqMiner" 

set output 'number_quanta.png'
set xlabel "Number of Quanta"
set ylabel "Time(s)(log base 10)"
set logscale y 10
set title "Time Taken Vs Number of Quanta"
plot "number_quanta" using 6:7 title "TAPFinder", "number_quanta" using 6:8 title "SeqMiner" 