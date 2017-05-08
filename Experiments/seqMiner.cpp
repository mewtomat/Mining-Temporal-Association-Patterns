#include <bits/stdc++.h>

using namespace std;

typedef std::chrono::high_resolution_clock::time_point TimeVar;

#define duration(a) std::chrono::duration_cast<std::chrono::milliseconds>(a).count()
#define timeNow() std::chrono::high_resolution_clock::now()

#define MIN_PATTERN_LENGTH 3
#define PATTERN_SUPPORT 4
#define EPOCH_LENGTH 4000

// trim from left end
static inline std::string &ltrim(std::string &s) {
    s.erase(s.begin(), std::find_if(s.begin(), s.end(),
            std::not1(std::ptr_fun<int, int>(std::isspace))));
    return s;
}

// trim from end
static inline std::string &rtrim(std::string &s) {
    s.erase(std::find_if(s.rbegin(), s.rend(),
            std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
    return s;
}

// trim from both ends
static inline std::string &trim(std::string &s) {
    return ltrim(rtrim(s));
}

vector<int> KMP(string S, string K)
{
    vector<int> T(K.size() + 1, -1);
	vector<int> matches;
    if(K.size() == 0)
    {
        matches.push_back(0);
        return matches;
    }
	for(int i = 1; i <= K.size(); i++)
	{
		int pos = T[i - 1];
		while(pos != -1 && K[pos] != K[i - 1]) pos = T[pos];
		T[i] = pos + 1;
	}
	int sp = 0;
	int kp = 0;
	while(sp < S.size())
	{
		while(kp != -1 && (kp == K.size() || K[kp] != S[sp])) kp = T[kp];
		kp++;
		sp++;
		if(kp == K.size()) matches.push_back(sp - K.size());
	}
	return matches;
}

vector<int> findPositions(vector<int>& candidate, const vector<set<int>* >& sequence )
{
	vector<int> results;
	for(int i=0;i<=sequence.size()-candidate.size();++i){
		bool found = true;
		for(int j=0;j<candidate.size();++j){
			if(sequence[i+j] == NULL || (sequence[i+j]->find(candidate[j]) == sequence[i+j]->end())){
				found = false;
				break;
			}
		}
		if(found){
			results.push_back(i);
		}
	}
	return results;
}

bool isPrefix(vector<int> & a, vector<int> & b)
{
	if(a.size()>b.size()) return false;
	for(int i=0;i<a.size();++i){
		if(a[i] != b[i]) return false;
	}
	return true;
}

bool isSuffix(vector<int>& a, vector<int>& b)
{
	if(a.size()>b.size()) return false;
	int bidx = b.size()-1;
	for(int i=a.size()-1;i>=0;--i){
		if(a[i] != b[bidx]) return false;
		--bidx;
	}
	return true;
}

int main(int argc, char* argv[]){
	TimeVar programStart = timeNow();
	if(argc<3){
		printf("Format: %s path_of_input_file input_file_name\n",argv[0]);
		exit(1);
	}

	ifstream input_file;
	string ifilename = string(argv[1])+string(argv[2]);
	try{
		input_file.open(ifilename.c_str(), ios::in);
	} catch(...) {
		printf("Error in reading file %s\n", ifilename.c_str());
		exit(2);
	}

	int ColorThresholds[] = {0,1, 2, 3, 4, 5, 6, 7, 8, 9};
	vector<string> Colors = {"RED", "YELLOW", "BLUE", "GREEN", "PURPLE", "BROWN", "BLACK", "WHITE", "ORANGE", "GRAY"};
	int NoOfCOLORS = Colors.size();

	vector<map<pair<int,int>, set<int> > > sequenceDatabase; 

	string line;
	int lineCounter=0;
	int EpochCounter = 0;
	sequenceDatabase.push_back(map<pair<int,int>, set<int> >());
	set<int> leftEntities, rightEntities;
	set<pair<int,int> > pairsAppearing;
	map<pair<int,int> , vector<set<int>* > > finalDatabase;
	vector<pair<int,int> > numberToPair;
	long int numQuanta;
	long int numPairs;
	TimeVar readStart = timeNow();
	while(getline(input_file, line)){
		lineCounter++;
		if(line.find("--")==std::string::npos){
			stringstream ssin(line);
			vector<string> words;
			while (ssin.good()){
				string word;
		        ssin >> word;
		        words.push_back(word);
	    	}
	    	if(words.size()>=3){
	    		int left_child, right_child, edge_val;
	    		try{
			    	left_child = stoi(words[0]);
			    	right_child = stoi(words[2]);
			    	edge_val = stoi(words[1]);
			    } catch(...){
			    	// printf("values of words are = %s, %s, %s\n",words[0].c_str(),words[1].c_str(),words[2].c_str());
			    }
		    	int color = NoOfCOLORS;
		    	for (int i=1;i<=NoOfCOLORS;i++) {
					if (ColorThresholds[i-1] == edge_val) {
						color = i;
						break;
					}
				}
				leftEntities.insert(left_child);
				rightEntities.insert(right_child);
				map<pair<int,int>, set<int> > & currentGraph = sequenceDatabase[EpochCounter];
				currentGraph[make_pair(left_child, right_child)].insert(color);
				finalDatabase[make_pair(left_child,right_child)] = vector<set<int>* > ();
				pairsAppearing.insert(make_pair(left_child,right_child));
			}
		} else {
			EpochCounter ++;
			sequenceDatabase.push_back(map<pair<int,int>, set<int> >());
		}
	}
	// printf("Read Complete in %ld secs.\n",duration(timeNow()-readStart));

	ofstream spfm_format;
	string tempFileName = "tempFile.txt";
	spfm_format.open(tempFileName, ios::out);
	numPairs = pairsAppearing.size();
	numQuanta = sequenceDatabase.size();
	// printf("Total pair of pair of entities found=%ld\n",numPairs );
	// printf("Total epochs:%ld\n",numQuanta );

	TimeVar finalDatabaseInsertion=timeNow();
	for(int i = 0; i<sequenceDatabase.size();++i){
		// printf("collecting from epoch %d\n",i);
		map<pair<int,int>, set<int> > & BipartiteGraph = sequenceDatabase[i];
		for(auto pair: pairsAppearing){
			if(BipartiteGraph.find(pair) != BipartiteGraph.end()){
				finalDatabase[pair].push_back(&BipartiteGraph[pair]);
			}
			else{ 
				finalDatabase[pair].push_back(NULL);
			}
		}
	}
	// printf("Completed inserting into finalDatabase in %ld\n", duration(timeNow()-finalDatabaseInsertion));
	TimeVar printStart = timeNow();
	for(auto pair: finalDatabase){
		for(auto itemSet: pair.second){
			if(itemSet!=NULL){
				for(auto item: *itemSet){
					spfm_format<<item<<" ";
				}
			}
			spfm_format<<"-1 ";
		}
		spfm_format<<"-2"<<endl;
	}
	spfm_format.close();
	input_file.close();
	// printf("Printing completed in %ld secs\n",duration(timeNow()-printStart) );
	system(("java -jar spmf.jar run PrefixSpan tempFile.txt out.txt "+ to_string((PATTERN_SUPPORT*1.0)/(pairsAppearing.size()*1.0))+" 200000 false").c_str());
	// int child = fork();
	// if(child ==0){
	// 	exec();
	// }
	ifstream freqSubSeqFile;
	freqSubSeqFile.open("out.txt", ios::in);
	string freqPattern;
	int lineNumb=0;
	vector<vector<int> > freqPatterns;
	while(getline(freqSubSeqFile,freqPattern)){
		lineNumb++;
		freqPattern = freqPattern.substr(0, freqPattern.find("#"));
		std::size_t nextMinusOne;
		vector<string> itemSets;
		bool multiple_items = false;
		while(freqPattern.find("-1")!=string::npos){
			string itemSet = freqPattern.substr(0,freqPattern.find("-1"));
			itemSets.push_back(trim(itemSet));
			stringstream ssin(trim(itemSet));
			vector<string> items;
			while (ssin.good()){
				string word;
		        ssin >> word;
		        items.push_back(word);
	    	}
	    	if(items.size()>1){
	    		multiple_items = true;
	    		break;
	    	}
			freqPattern=freqPattern.substr(freqPattern.find("-1")+2);
		}
		if(multiple_items)continue;
		if(trim(freqPattern) !=""){
			itemSets.push_back(trim(freqPattern));
			stringstream ssin(trim(freqPattern));
			vector<string> items;
			while (ssin.good()){
				string word;
		        ssin >> word;
		        items.push_back(word);
	    	}
	    	if(items.size()>1) continue;
		}
		vector<int> numberedItemSet;
		for(auto elem:itemSets){
			numberedItemSet.push_back(stoi(elem));
		}
		freqPatterns.push_back(numberedItemSet);
	}
	freqSubSeqFile.close();
	vector<vector<int> > TAPSeq;
	vector<int> TAPfreq;
	vector<int> TAPEpoch;
	vector<vector<int> > TAPSupport;

	// printf("total candidate sequences %ld\n",freqPatterns.size() );
	int candNum=0;
	for(auto& candidate: freqPatterns){
		// printf("\n");
		// printf("checking cand num %d:",candNum );
		candNum++;
		if(candidate.size()<MIN_PATTERN_LENGTH)continue;
		vector<vector<bool> > presentAtPos(numPairs,vector<bool>(numQuanta,false));
		int pairNo = 0;
		for(auto& pair_seq: finalDatabase){
			numberToPair.push_back(pair_seq.first);
			const vector<set<int>* >& sequence = pair_seq.second;
			vector<int> containedAt = findPositions(candidate, sequence);
			for(int j=0;j<containedAt.size();++j){
				presentAtPos[pairNo][containedAt[j]] = true;
			}
			pairNo++;
		}
		for(int i=0;i<numQuanta;++i){
			int support=0;
			// TAPSupport.push_back(vector<int>(0,0));
			vector<int> support_set;
			for(int j=0;j<numPairs;++j){
				if(presentAtPos[j][i]){
					support_set.push_back(j);
					support++;
				}
			}
			if(support>=PATTERN_SUPPORT){
				TAPSeq.push_back(candidate);
				TAPfreq.push_back(support);
				TAPEpoch.push_back(i);
				TAPSupport.push_back(support_set);
				// printf("%d ",i);
			}
		}
	}
	printf("\n");
	vector<bool> longestTap(TAPSeq.size(), true);
	for(int i=0;i<TAPSeq.size();++i){
		for(int j=i+1;j<TAPSeq.size();++j){
			if(TAPEpoch[i] == TAPEpoch[j]){
				if(isPrefix(TAPSeq[i], TAPSeq[j]) || isPrefix(TAPSeq[j], TAPSeq[i])) longestTap[i] = false;
			}
			if((TAPEpoch[i]+TAPSeq[i].size()-1 == TAPEpoch[j]+TAPSeq[j].size()-1) &&
				(TAPfreq[i] == TAPfreq[j])){
				if(isSuffix(TAPSeq[i], TAPSeq[j])||isSuffix(TAPSeq[j], TAPSeq[i])) longestTap[i] = false;
			}
		}	
	}
	int numFinalTAPs=0;
	ofstream TAPFile("TAPs.txt",ios::out);
	for(int i=0;i<TAPSeq.size();++i){
		if(!longestTap[i]) continue;
		numFinalTAPs++;
		auto taps=TAPSeq[i];
		for(auto item:taps){
			TAPFile<<item<<" ";
		}
		TAPFile<<", Epoch:"<<TAPEpoch[i]<<", Support: "<<TAPfreq[i]<<endl;
		// for(auto pairNum: TAPSupport[i]){
		// 	TAPFile<<numberToPair[pairNum].first<<"_"<<numberToPair[pairNum].second<<", ";
		// }
		// TAPFile<<endl;
	}
	TAPFile.close();
	// printf("TAP sequences found=%d\n", numFinalTAPs);
	cout<<"Time:"<<duration(timeNow()- programStart)/1000.0<<endl;
	return 0;
}