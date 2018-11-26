import java.io.*;
import static spark.Spark.*;
import java.util.*;
import java.util.stream.*;

public class SimulationService {

    public static void main(String[] args) {

        if (args.length<2){
            System.out.printf("%s%n%s", "Both Input files not provided!", 
                                "Usage: java -jar working_system.jar <path_to_scenario_file> <path_to_rider_file>");
            return;
        }

        String setupFileStr = args[0].trim();
        String riderFileStr = args[1].trim();

        File setupFile = new File(setupFileStr);

        if (!setupFile.exists() || !setupFile.isFile()) {
            System.out.printf("Cannot find file '%s' ", setupFileStr);
            return;
        }

        File riderFile = new File(riderFileStr);

        if (!riderFile.exists() || !riderFile.isFile()) {
            System.out.printf("Cannot find file '%s' ", riderFileStr);
            return;
        }

        SimulationEngine engine = SimulationEngine.getInstance();

        try{
            engine.initFromFile(setupFileStr, riderFileStr);
        }
        catch(IOException ioe){
            System.out.print("Unable to read from input files");
            return;
        }
        catch(Exception e){
            System.out.printf(e.getMessage());
            return;
        }

        runServer();
    }

    public static void runServer(){
        staticFiles.location("/static");

        SimulationEngine engine = SimulationEngine.getInstance();

        post("/ksave", (request, response) ->{

            String strData  = request.body() ;
            Map<String, String> map = getQueryMap(request.body());
            Set<String> keys = map.keySet();
            String strValue  ="" ;
            for (String key : keys)
            {
                strValue = map.get(key) ;
                double value = Double.parseDouble(strValue );
                setKValue(engine,key,value);
            }
            return "200" ;
        } );

        get("/", (request, response) -> {
            return createJsonSystemState();
        });

        get("/movebus", (request, response) -> {
            return createJsonMoveBus();
        });

        get("/rewind/:count", (request, response) -> {
            return createJsonRewind(Integer.parseInt(request.params(":count")));
        });

        get("/getbus/:busid", (request, response) -> {
            int busId = Integer.parseInt(request.params(":busid"));
            Bus bus = engine.getBus(busId); ;
            return createJsonBusModal(bus);
        });

        get("/reset", (request, response) -> {
            engine.init();
            return createJsonSystemState();
        });

        post("/changebus", (request, response) -> {

            int busId = Integer.parseInt(request.queryParams("busid"));
            int speed = Integer.parseInt(request.queryParams("speed"));
            int capacity = Integer.parseInt(request.queryParams("capacity"));
            String routeId = request.queryParams("route");
            String stopIndex = request.queryParams("stopindex");

            Bus bus = null;

            if (routeId!=null){
                bus = engine.changeBus(busId, speed, capacity, Integer.parseInt(routeId), Integer.parseInt(stopIndex));
            }
            else{
                bus = engine.changeBus(busId, speed, capacity);
            }

            return createJsonBus(bus);
        });

    }


    // JSON Returning methods below

    // We can always return entire system state for every operation and have the UI redraw everything
    // But it is much better to just send what is really updated for actions like Move Bus etc where onyl a bus and stop need to be redrawn in UI


    private static String createJsonBus(Bus bus){
        return String.format(
            "{\"id\":%d, \"current_stop_id\":%d, \"arrival_time\":%d, \"status\": \"%s\", \"rider_count\":%d,\"route\":%d, \"capacity\":%d, \"speed\":%d}",
            bus.getBusId(), bus.getCurrentStop().getStopId(), bus.getArrivaltime(), bus.toString(), bus.getRiderCount(), 
            bus.getRoute().getRouteId(),bus.getCapacity(),bus.getSpeed());
    }

    private static String createJsonBusModal(Bus bus){ 
        return String.format(
            "{\"id\":%d, \"current_stop_id\":%d, \"arrival_time\":%d, \"status\": \"%s\", \"rider_count\":%d,\"route\":%d, \"capacity\":%d, \"speed\":%d, \"routeid\":%d, \"nextstopindex\":%d}",
            bus.getBusId(), bus.getCurrentStop().getStopId(), bus.getArrivaltime(), bus.toString(), bus.getRiderCount(), 
            bus.getRoute().getRouteId(),bus.getCapacity(),bus.getSpeed(),  bus.getRoute().getRouteId(), bus.getNextStopIndex());
    }

    private static String createJsonStop(Stop stop){
        return String.format("{\"id\":%d,\"name\":\"%s\",\"waiting\":%d}", stop.getStopId(), stop.getName(), stop.getWaitingCount());
    }

    private static String createJsonRoute(BusRoute route){
        return String.format("{\"id\":%d,\"stops\":[%s]}", route.getRouteId(), 
                route.getStopList().stream()
                .map(s -> s.getStopId() + "")
                .collect(Collectors.joining(",")));
    }

    public static String createJsonSystemState(){
        
        SimulationEngine engine = SimulationEngine.getInstance();

        String json = String.format("{\"kspeed\":%s,\"kcapacity\":%s,\"kwaiting\":%s,\"kbuses\":%s,\"kcombined\":%s,\"efficiency\":%s,\"num_rewinds_possible\":%d,\"routes\":[",
                    engine.getSysSpeed() + "", engine.getSysCapacity() + "", engine.getSysWaiting() + "", engine.getSysBuses() + "", 
                    engine.getSysCombined() + "", engine.calcSystemEfficiency(), engine.getNumberOfRewindsPossible());
                            
        
        String csv = "";

        for (BusRoute route : engine.getRoutes()) {
            csv = csv + String.format("%s,", createJsonRoute(route));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        json = json + csv + "],\"stops\":[";

        csv = "";
        for (Stop stop : engine.getStops()) {
            csv = csv + String.format("%s,", createJsonStop(stop));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        json = json + csv + "],\"buses\":[";

        csv = "";
        for (Bus bus : engine.getBuses()){
            csv = csv + String.format("%s,", createJsonBus(bus));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        return json + csv + "]}";
    }

    public static String createJsonMoveBus(){

        SimulationEngine engine = SimulationEngine.getInstance();
        String result = engine.moveBus();

        if (result==null) return "{\"move\":false}";

        // Extract bus id from a string like "b:67->s:16@0//p:0"
        int busId = Integer.parseInt(result.split(":")[1].split("-")[0]);
        Bus bus = engine.getBus(busId);

        return String.format("{\"move\":true,\"bus\":%s,\"efficiency\":%s, \"num_rewinds_possible\":%d, \"waiting_at_stop\":%d}", 
                createJsonBus(bus), engine.calcSystemEfficiency(), engine.getNumberOfRewindsPossible(), bus.getCurrentStop().getWaitingCount() );
    }

    public static String createJsonRewind(int numEventsToRewind){

        SimulationEngine engine = SimulationEngine.getInstance();

        ArrayList<SystemState> resultsList = engine.rewind(numEventsToRewind);
        String result = String.format("{\"efficiency\":%s, \"num_rewinds_possible\":%d, \"rewinds\":[",
                                    engine.calcSystemEfficiency(), engine.getNumberOfRewindsPossible());

        // If a bus moved twice, we only care about the last move. 
        // So let us traverse in reverse and thus not add same bus if it was rewound later
        ArrayList<Integer> processedBuses = new ArrayList<>();

        for (int i = resultsList.size() -1; i >= 0; i--) {
            
            SystemState state = resultsList.get(i);
            Bus bus = engine.getBus(state.getBus().getBusId());
            Stop stop = engine.getStop(state.getStop().getStopId());

            if (!processedBuses.contains(bus.getBusId())){
                processedBuses.add(bus.getBusId());

                result = result + String.format("{\"bus\":%s,\"stop\":%s}",
                                createJsonBus(bus), createJsonStop(stop));
            }
        }   
        return result + "]}";
    }
    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static void setKValue(SimulationEngine engine,String key, double value) {
        switch (key) {
            case "kspeed":
                engine.setSysSpeed(value);
                break;
            case "kcapacity":
                engine.setCapacity(value);
                break;
            case "kwaiting":
                engine.setSysWaiting(value);
                break;
            case "kbuses":
                engine.setSysBuses(value);
                break;
            case "kcombined":
                engine.setSysCombined(value);
                break;
            default:
                break;
        }
    }

}
