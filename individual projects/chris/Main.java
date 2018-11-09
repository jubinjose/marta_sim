import java.io.*;
import java.util.*;

public class Main {
		static ArrayList<Bus> buses = new ArrayList<Bus>();
		static ArrayList<Route> routes = new ArrayList<Route>();
		static ArrayList<Stop> stops = new ArrayList<Stop>();
		static ArrayList<Event> events = new ArrayList<Event>();
		
	public static void main(String[] args) {
		String inputFileName;

		
		//Process start
		if (args.length != 1) {
			System.out.println("Must have one argument, input text file.");
			return;
		} else {
			//System.out.println("Argument accepted");
		}
		
		inputFileName = args[0];
		//System.out.println(inputFileName);
		//Step 1: Read the data from the provided scenario configuration file
		//read in parameter to get file name
		//file exists in current directory as Jar file

		//open file to read, throw error if no file exists
		try {
		Scanner fileInput = new Scanner(new File(inputFileName));
		String[] commandArgs;
		do {
			String commands = fileInput.nextLine();
			commandArgs = commands.split(",");
			//parse commandArgs
			switch(commandArgs[0]) {
			case "add_depot":
				//Ignoring all depot logic
				break;
			case "add_stop":
				Stop newStop = new Stop(Integer.parseInt(commandArgs[1]),commandArgs[2],Integer.parseInt(commandArgs[3]),Double.parseDouble(commandArgs[4]),Double.parseDouble(commandArgs[5]));
				stops.add(newStop);
				break;
			case "add_route":
				Route newRoute = new Route(Integer.parseInt(commandArgs[1]),Integer.parseInt(commandArgs[2]),commandArgs[3]);
				routes.add(newRoute);
				break;
			case "extend_route":
				Route extendedRoute = findRoute(Integer.parseInt(commandArgs[1]));
				extendedRoute.extendRoute(Integer.parseInt(commandArgs[2]));
				break;
			case "add_bus":
				Route busRoute = findRoute(Integer.parseInt(commandArgs[2]));
				Bus newBus = new Bus(Integer.parseInt(commandArgs[1]),busRoute,Integer.parseInt(commandArgs[3]),Integer.parseInt(commandArgs[4]),Integer.parseInt(commandArgs[5]),Integer.parseInt(commandArgs[6]),Integer.parseInt(commandArgs[7]),Float.parseFloat(commandArgs[8]));
				buses.add(newBus);
				break;
			case "add_event":
				Event newEvent = new Event(Integer.parseInt(commandArgs[1]),commandArgs[2],Integer.parseInt(commandArgs[3]));
				events.add(newEvent);
				break;
			default:
				System.out.println("Unknown command in input file:" + commandArgs[0]);
				break;
			}
			
		} while (fileInput.hasNextLine());
		fileInput.close();
		} catch (Exception except) {
			System.out.println();
			except.printStackTrace();
			System.out.println();
		}
		sortEvents(events);
		//loops for 20 iterations		
		for (int i = 0; i < 20; i++) {
			
	
		//Step 2: Determine which bus should be selected for processing (based on lowest arrival time)
		// This will be done by looking at "Event Stack"
			Event nextEvent = getTopEvent(events);
			
			Bus nextBus = findBus(nextEvent.executeEvent());
			//System.out.println("Bus ID: " + String.valueOf(nextBus.getId()));
			if (nextBus.getStarted() != false) {
			nextBus.setLocationIndex(nextBus.getLocationIndex() + 1);	
			}
		//Debug//
			
			int timeDiff = nextBus.moveBus();
		//Step 3: Determine which stop the bus will travel to next (based on the current location and route)
			nextBus.setStarted(true);
			Route busRoute = nextBus.getRoute();
			Stop nextStop = getStop(busRoute.getStops().get(nextBus.getNextStopIndex(nextBus.getLocationIndex())));
		//Step 4: Calculate the distance and travel time between the current and next stops
			//System.out.println("Time Diff: " + String.valueOf(timeDiff));
			//System.out.println("Stop ID: " + String.valueOf(nextStop.getId()));
			int newTime = nextEvent.getTime() + timeDiff;
			//int oldTime = nextEvent.getTime();
			nextEvent.updateTime(newTime);
		//Step 5: Display the output line of text to the display
			//Debug specific routes:
			//if (nextBus.getId() == 69) {
			String output = "b:" + String.valueOf(nextBus.getId()) + "->s:" + String.valueOf(nextStop.getId()) + "@" + String.valueOf(newTime)+"//p:0/f:0";
			System.out.println(output);//}
		//Step 6: Update system state and generate new events as needed
			sortEvents(events);
		//end loop
		}
		
	}
	
	
	public static Stop getStop(int id) {
		for (int i = 0; i < stops.size();i++) {
		if 	(stops.get(i).getId() == id) {
			return stops.get(i);
		}
		}
		return null;
	}
	
	public static Route findRoute(int id) {
		for (int i = 0; i < routes.size();i++) {
			if 	(routes.get(i).getId() == id) {
				return routes.get(i);
			}
			}
		return null;
	}
	
	public static Bus findBus(int id){
		for (int i = 0; i < buses.size();i++) {
			if 	(buses.get(i).getId() == id) {
				return buses.get(i);
			}
			}
		return null;
	}
	 
	
	public static void sortEvents(ArrayList<Event> events) {
		Collections.sort(events);
	}
	
	public static Event getTopEvent(ArrayList<Event> events) {
		return events.get(0);
	}

}
