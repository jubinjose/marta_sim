import java.util.*;

public class Bus {

	int id;
	float speed;
	int capacityPassenger;
	int capacityFuel;
	Route route;
	int locationIndex;
	int intialFuel;
	ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	Stop currStop;
	Boolean started = false;
	
	Bus(int id, Route route, int location, int initPassCnt, int passCap, int initFuel, int fuelCap, float speed){
		this.id = id;
		this.speed = speed;
		this.capacityPassenger = passCap;
		this.capacityFuel = fuelCap;
		this.route = route;
		this.locationIndex = location;
		this.intialFuel = initFuel;
		this.currStop = null;
	}
	
	public float getSpeed() {
		return speed;
	}

	public int getId() {
		return id;
	}
	
	public int getNextStopIndex(int locationIndex) {
		int n_plus;
		//System.out.println(String.valueOf(route.getLength()));
		//System.out.println(String.valueOf(locationIndex));
		if (locationIndex < route.getLength() - 1) {
			n_plus = locationIndex + 1;
				} else {
					n_plus = 0;
				}
		return n_plus;
		
	}
	

	public int getLocationIndex() {
		return locationIndex;
	}

	public void setLocationIndex(int locationIndex) {
		if (locationIndex == route.getLength()) {
			this.locationIndex = 0;
		} else {
			this.locationIndex = locationIndex;
		}
	}

	int moveBus() {
		double distance = 0.0;
		//System.out.println("Location index: " + String.valueOf(locationIndex));
		currStop = route.atStop(locationIndex);
		if (currStop != null) {
		
			distance = route.checkDistance(locationIndex, getNextStopIndex(locationIndex));
		} else {
			System.out.println("currStop is null");
		}
		int travel_time = (int) (1 + (((int)distance)* 60 / speed));
		return travel_time;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Boolean getStarted() {
		return started;
	}
	
	public void setStarted(Boolean state) {
		started = state;
	}
	
	
}
