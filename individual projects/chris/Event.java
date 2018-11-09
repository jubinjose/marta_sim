

public class Event implements Comparable<Event> {
	
	int time;
	String type;
	int id;
	
	Event(int time, String type, int id){
		this.time = time;
		this.type = type;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getTime() {
		return time;
	}
	
	@Override
	public int compareTo(Event eventCompareTo) {
		int compareTime = eventCompareTo.getTime();
		return this.time-compareTime;
	}
	
	public void updateTime(int newTime) {
		time = newTime;
		
	}
	
	int executeEvent() {
		return id;
	}

}
