import java.util.*;

public class Stop {

	int id;
	String name;
	int riders;
	double latitude;
	double longitude;
	ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	
	Stop(int id, String name, int riders, double lat, double lng){
		this.id = id;
		this.name = name;
		this.riders = riders;
		this.latitude= lat;
		this.longitude = lng;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	void checkArrivals() {
		
	}
	
	void getOff(Passenger busPassenger) {
		
	}
	
	ArrayList<Passenger> getOn() {
		ArrayList<Passenger> passList = new ArrayList<Passenger>(); //default passenger list
		return passList;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	void passengerDepart(Passenger departingPass) {
		
		
	}
}
