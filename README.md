# Information-Retrieval-and-Web-Search
IRWS Scripts developed with Java

The project consists of 3 Tasks. The concepts of evaluation or fusion of search engines in Information Retrieval is applied in each of the tasks. 
The source code for each task is written in core java and the tasks run without the need of an external library. 

The project consists of :

IRWS1.java- source code for Task1 - Evaluation measures
IRWS2.java- source code for Task2 - Interleaving, CombSum, LCM
IRWS3.java- source code for Task3 - ProbFuse 



Requirements to run the Java Source code of each Tasks:

The programs were created in Windows 10 Operating System with a Java 8 version. However, the program works well in any operating system since java 
is platform independent.There are no specific requirements since external libraries or packages are not used. 
There are no such constraints on the RAM space requirements.

A zipped file of all the source codes along with the README file is submitted for grading. 

Motivation:

The programs were created for the requirement of Master's in Big Data Management and Analytics, Semester 1, Information Retrieval and Web Search Assignment 2.
Each of the tasks does certain evaluation and fusion steps of the Information Retrieval process. 

Tests:
Sample inputs were given as a part of the assignment. The Task 1 and Task 2 source codes were tested with the sample inputs given for Task1, Task2 to
check the correctness of the programs. The test input for Task 3 input was created, as it was not provided as part of the assignment. 
These tests can be done on command prompt as the main method of each task accepts inputs as command line arguments. 

Output

The output of each task is printed in the console and also written to output files in the same directory. Outputs are properly formatted and are easily 
understandable. 

The filenames of the output files are as follows for each task :

Task 1-    OUTPUT1.txt(also contains Top 3 Engines for Task 1)
Task 2-    OUTPUT2.txt
Task 3-    OUTPUT3.txt





TASK 1 - 

Script calculates the performance of engines using the following techniques to compare : 
1) Precision
2) Recall 
3) P@5
4) P@R=0.5
5) Average Precision
6) Mean Average precision 
7) Interpolate precision output as a list of 11 Precision values, 1 at each of the recall thresholds. 

Top Three Engines are also listed as output in console and also clearly laid out in the end of file OUTPUT1.txt.

Mean Average Precision is used to find top three engines, and the 3 engines with highest Mean Average Precision are selected as top three engines. 
This is because,  Mean Average Precision gives a good indication of performance within a single metric as it is averaging the results over multiple queries.
It works well for simple search cases. This is advantageous for system which returns relevant documents early in the result set, 
as precision for these engines will be higher. Relevant documents that are not returned gets a precision value of zero, there
systems with low recall will be given less merits, so this also indirectly considers recall.This makes it a good metric.
Mean Average Precision ranks engines with good precision and this helps to satisfy the user information need with the Information Retrieval System.
It is because of these reasons, Mean Average Precision was selected as a useful metric and used for ranking the engines for the purpose of the assignment.
 

P@R=0.5 is found using the function that finds Interpolated Precision. PrecisionRecallTable class is used for getting and setting values 
for the purpose of printing results in a proper format.

The script runs from command line and or shell prompt and accepts one argument which is a text file. The output is printed in the console
and also written to a clearly laid out output file in the same directory which is named as OUTPUT1.txt.
The source file will contain a list of
Information retrieval engine results in the form of -

Sample inputfile_name.txt contains :
1;A;RNRUNNRURRRUNUNNNNRR;10
1;B;RURRNNRNNNNRRURUNNRN;15
1;C;NNNNNRNNRNNNRNRURURR;10
2;A;RNRRRNNNRUUUUNRRNRRN;10
2;B;RNNNNRURUNNRUNRRNNNR;12
2;C;NRNNRRNNRRRNRNUNRURR;11
3;A;NURRNNRNNRRNUNRURNNR;10
3;B;NRNNUNRRNRRNUUNNRNNR;13
3;C;NRNNNNNRNURRRRRRNRRU;10


From command line:
To compile -javac IRWS1.java
To run - java IRWS1 inputfile_name.txt

Sample Output in console and contents of OUTPUT1.txt :
--------------------------------------------------------------
---------------------------Precision and Recall-----------------------------------
  Engine      Run         Precision           Recall
--------------------------------------------------------------
     A         1         0.4                  0.8
     B         1         0.4                  0.5333333333333333
     C         1         0.35                 0.7
     ........................................
     ........................................ 
===============================================================

--------------P@5---------------------
  Engine      Run         P@5    
-----------------------------------
     A         1         0.4            
     B         1         0.6            
     C         1         0.0            
    ........................
    .......................     
===================================

----------------------------------------------------------------------------------
  Engine      Run             InterPolated Precision(in the order of recall 100% to 0%)
----------------------------------------------------------------------------------
     A         1         [0.0, 0.0, 0.4, 0.4, 0.545, 0.545, 0.545, 0.545, 0.667, 1.0, 1.0]
     B         1         [0.0, 0.0, 0.0, 0.0, 0.0, 0.421, 0.467, 0.467, 0.75, 0.75, 1.0]
     C         1         [0.0, 0.0, 0.0, 0.35, 0.35, 0.35, 0.35, 0.35, 0.35, 0.35, 0.35]
     ...........................................
     ........................................ 

===================================================================================
----------------P@R=0.5---------------------------
  Engine      Run        P@R=.05
-------------------------------------------
     A         1         0.545          
     B         1         0.421          
     C         1         0.35           
      ...........................................
     ........................................ 

===============================================================

------------------Average Precision-------------------------
  Engine      Run        Average Precision
-------------------------------------------
     A         1         0.4353558137768664
     B         1         0.31693464430306534
     C         1         0.18462319070678204
    ...........................................
     ........................................ 
===========================================

-------------------------------------------
  Engine      Mean Average Precision
-------------------------------------------
     A         0.4403207979098072
     B         0.261453451948808
     C         0.3479376040873367
===========================================


--------------TOP 3 ENGINES---------------------
  SLNO      Engine   
-----------------------------------
     0         A         
     1         C         
     2         B         
===============================================================






TASK 2 

The script uses:  
1. Interleaving 
2. CombSUM 
3. LCM  
to produce a set of three top 100 documents, one for each fusion technique. 

The script runs from command line and or shell prompt and accepts two argument which are text files.
 The output is printed in the console and also written to a clearly laid out output file in the same directory which is named as OUTPUT2.txt.
The first argument can be a set of IR engine results in the format-

Engine#;Doc#;rank_score engine#;Doc#;rank_score engine;Doc#;rank_score 

Engine number will not change in a column, nor will weight. Document number can be any number between 1 and 1500 .
Rank score can be any number but will always decrease or remain constant as the rows increment. Two documents can have the same rank score. 

There is no set number of results per engine,the scripts cut each result set off at a fair point. 
 
For example, If engine A has only 20, B anc C has more than that, then after the documents in A are exhausted it will move on to B and C. 

The second argument will be a list of document Engine IDs and weights in the format 
engineID;weight[tab]engineID;weight 


Sample first argument file contains -
A;35;995	B;226;0.984	C;50;100
A;54;988	B;6;0.979	C;78;98
A;65;981	B;59;0.953	C;35;97
A;162;970	B;119;0.948	C;184;96
A;152;967	B;26;0.937	C;53;95
A;44;964	B;13;0.915	C;232;94
A;206;949	B;10;0.905	C;245;93
......................................
.....................................


Sample second argument file contains -
A;1.2	B;2.0	C;1.0



From command line :

To compile  - javac IRWS2.java

To run     - java IRWS2 inputfile_name.txt weightfile_name.txt


Sample Output in console and contents of OUTPUT2.txt :
----------Interleaving------------
  SLNO         Document
   1         35
   2         226
   3         50
   4         54
   5         6
   6         78
   7         65
   8         59
   9         184
   10        162
.................
................
   99        154
   100       202
---------------COMBSUM--------------------
-----------------------------------
  Rank      Document     Rating
-----------------------------------
   1         35           2.7260680196460014
   2         26           1.8883954368673588
   3         152          1.8285699276408094
   4         54           1.79740288409721
   5         115          1.7480103436043366
   6         51           1.6496802301044688
   7         24           1.640743318181523
   8         10           1.5538648331028393
   9         28           1.542952460383653
   10        6            1.5403577054953201
.............................................
.............................................
   99        110          0.5949044585987261
   100       137          0.5904023581812398
==================================
---------------LCM--------------------
-----------------------------------
  Rank      Document     Rating
-----------------------------------
   1         35           3.682439069595033
   2         26           3.0277462877474566
   3         54           2.8019395261562035
   4         115          2.728879208729615
   5         10           2.6002137426387995
   6         6            2.535260865536095
   7         119          2.4570513644597676
   8         59           2.4418496895561117
   9         152          2.309917311371177
   10        28           2.267723102585488
   11        187          2.1936799184505604
   12        24           2.1210928899820383
   13        226          2.0
.......................................
......................................
   99        144          0.7747196738022426
   100       138          0.764525993883792
==================================


Task 3

The program performs a fusion technique in IR called the ProbFuse. 
When executed the program generates a probfuse model from the training data which will be given in the format.
Once the probfuse model is complete it is applied to the file provided in the third argument which is a live file containing a list of Document IDs 
and the Engine ID they were produced by. 

The script runs from command line and or shell prompt and accepts three argument two of which are text files and one integer.
The first and third arguments are training file and live file respectively
The second argument will be a single number in the range 3 to 20 . This will be the number of sectors that is used in the model. 
The output is printed in the console and also written to a clearly laid out output file in the same directory which is named as OUTPUT3.txt.

The program takes the live results and by applying the model developed by the training phase, 
generates a ranked list of a top 20 documents from the results provided, and the top 20 documents with their rating is the output. 


From Command line:

To compile - javac IRWS3.java
To run - java IRWS3 training_file_name.txt sector_no live_file_name.txt


example run - java IRWS3 trainingfile.txt 3 livefile.txt


Sample contents of training_file_name.txt

1;A;RRNNNRRRRNRNRRR;10
1;B;RNRRRRNNRRNRNNN;15
2;A;NRNNRNRRRRNNNRR;10
2;B;NNRRRRNNNRRRNNN;12

Sample no.of sectors : 5

Sample contents of live_file_name.txt

A;[26,28,38,77,1,34,24,46,96,51,11,3,37,83,21]
B;[49,96,1,28,37,51,101,11,29,77,6,46,24,41,38]


Sample Output in the console and also contents of OUTPUT3.txt :
---------------Probfuse--------------------
-----------------------------------
  Rank      Document     Rating
-----------------------------------
   1         28           1.1
   2         1            1.1
   3         96           1.1
   4         37           0.9
   5         49           0.7
   6         77           0.65
   7         51           0.65
   8         38           0.5
   9         24           0.5
   10        46           0.5
   11        11           0.45000000000000007
   12        26           0.4
   13        34           0.4
   14        101          0.25
   15        29           0.25
   16        3            0.20000000000000004
   17        83           0.20000000000000004
   18        21           0.20000000000000004
   19        6            0.10000000000000002
   20        41           0.10000000000000002
==================================

