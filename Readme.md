# Transport simulation example with es6 UI and Java microserver backend

## Application Requirements

Java version 1.8+

Browser with support for HTML5 & ES6 JavaScript

## Application External Dependencies

jQuery (JavaScript library) - https://jquery.com/

Spark (Java Micro Webserver) - http://sparkjava.com/

Simple Logging Facade for Java - https://www.slf4j.org/

## Description of folders within repo

app – Maven project for the Java backend

app/src – All source code (java and UI) for the project

test – All provided test files

test/probabilities – All provided passenger distribution files

test/20 Step Solutions – Solutions of twenty step simulation with each test file

## To Run the application

This is a two-step process – Launch the Java app and then access UI on browser

To start the Java application, open a terminal and change directory

    cd ./app/

Issue following command for Mac/Unix

Note that for Windows systems, use semicolon instead of colon and back slash instead of forward slash

To launch with no input files specified

    java -cp "./target/classes:./target/classes/lib/*" SimulationService

To launch with a setup file and passenger probability file specified

    java -cp "./target/classes:./target/classes/lib/*" SimulationService <setup file>  <passenger probability file>

Server will start running. Do not close the window

Open a browser and access below URL to access the application UI
http://localhost:4567/view.html

To load (or re-run) application with another set of input files

    Option 1: Use the ‘initialize from files’ button on the UI to load the new setup and passenger probability files

    Option 2: shut down the Java console app and re-run with new set of input files as argument. Then launch browser and navigate to http://localhost:4567/view.html

## To Build application from source (compile)

Always make source code changes (java or html/js/css) only in app/src folder.

Compiled classes & everything required to run app will be created in app/target

Open a terminal and change directory

    cd ./app
    
Build using Maven -> Execute following command

    mvn clean compile

Build using Java → Execute the following four commands
 
    rm -f -r ./app/target

    mkdir -p target/classes

    javac -cp "./src/main/java/resources/lib/*" -d "./target/classes"  ./src/main/java/*.java

    cp -R ./src/main/resources/* ./target/classes

## Application notes

There is extensive error checking and validation built in with good feedback to the user – For example, try update bus or system changes to incorrect or alphabetic values. Or try initializing system without specifying a file. Similarly, info like number of rewinds available or features like disabling rewind button when all rewinds are exhausted are implemented.

There is extensive logging on the server console to show information related to passenger exchange etc. that is not all visible on the UI.

Application combines domain driven design with single responsibility principle. Hence the reason why Bus, Stop, Route etc. are core business entities and most of the application logic is taken away from them by SimulationEngine (Inversion of Control).

SimulationEngine is a singleton so that every web request coming in can get a hold of the same instance

The UI is truly stateless. It is just a window into the actual application engine. This de coupling of UI from application engine using a well-known protocol (http + json) allows multiple UIs to connect to the SimulationService and view or manipulate the system.

The UI has clean ES6 JavaScript class-based code. No angular/react/vue or simialr as it will be overkill.

On a rewind, we restore all the state related to the bus including any capacity, passenger and route changes that may have happened.

## Other interesting info

http://localhost:4567/ -> This will give a JSON representation of system state. 
The application start page view.html as well as many operations makes http call to this URL to get the system state and redraw UI

http://localhost:4567/movebus -> Performs move bus event and returns data pertaining to bus that moved, stop that got affected. The “move bus “ button on the UI calls this.
We could return the entire system state data too as a result of this and have the UI repaint everything. But why do that when we know only a bus and stop changes as a result of move bus event. Hence a different minimal return data for this as opposed to the URL just above

http://localhost:4567/reset -> Resets system to the very start. This is not required for the assignment. But is a convenient way to debug/test the application

http://localhost:4567/changebus -> This is a HTTP POST method and accepts bus Id, speed, capacity, route and stop index on new route as request parameters. 
It queues the proposed changes for the bus to happen at the next event for that bus

http://localhost:4567/rewind/<N> -> Rewinds system by N steps. We hardcode N to 1 since the Ui is designed to allow only a single rewind at a time.  Returns data relevant to the stop and bus impacted by the rewind

http://localhost:4567/changebus -> System efficiency update end point that accepts all five k values

## Bulk testing against provided test files and their solutions

Only move bus sequence logic can be validated since passenger count etc. is random

Run SimulationTester passing folder with test files as first argument and a passenger probability file as second argument.

The results will be created in a folder named “output” in the folder passed as first argument.

This output folder can be folder diff-ed with the provided solutions folder ( ~/git/cs6310/test/20 Step Solutions ) to validate output correctness

    cd ./app

    java -cp ./target/classes:./target/classes/lib/* SimulationTester ../test  ../test/probabilities/test_evening_distibution.csv

Now do a folder diff between  ../test/output and ../test/20 Step Solutions 
