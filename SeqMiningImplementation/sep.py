import sys

infile = open(sys.argv[1],'r')
outfile = open(sys.argv[2], 'w')

edges = (infile.readline()).strip().split(",")
for edge in edges:
	outfile.write(edge.strip())
	outfile.write('\n')
