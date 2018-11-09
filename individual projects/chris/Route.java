import java.util.*;


public class Route {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	int id;
	String name;
	int number;
	ArrayList<Integer> stops = new ArrayList<Integer>();
	
	Route(int id, int number, String name){
		this.id = id;
		this.number = number;
		this.name = name;
		
	}
	
	void extendRoute(int stopID){
		stops.add(stopID);
	}
	
	double checkDistance(int index1, int index2) {
		//System.out.println(String.valueOf(index1));
		//System.out.println(String.valueOf(index2));
		Stop stop1 = Main.getStop(stops.get(index1));
		Stop stop2 = Main.getStop(stops.get(index2));
		double y1 = stop1.getLatitude();
		double y2 = stop2.getLatitude();
		double x1 = stop1.getLongitude();
		double x2 = stop2.getLongitude();
		double distance = 70.0 * Math.sqrt(Math.pow((y1 -  y2),2)+Math.pow((x1 - x2),2));
		//System.out.println("Distance: " + String.valueOf(distance));
		return distance;
	}
	
	Stop atStop(int index) {
		
		Stop stop = Main.getStop(stops.get(index));
		//stop.checkArrivals();
		return stop;
	}
	
	int getLength() {
		return stops.size();
		
	}
	
	public ArrayList<Integer> getStops(){
		return stops;
	}
}
