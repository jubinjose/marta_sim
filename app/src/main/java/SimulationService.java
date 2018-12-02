import java.awt.List;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.*;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

import static spark.Spark.*;


// This class exists just so that it adds a webr equest handling wrapper around SimulationEngine (which is unaware of any web behavior)
public class SimulationService {

    private static final Logger logger = LoggerFactory.getLogger("");

    public static void main(String[] args) {

        if (args.length==1 || args.length>2){
            logger.info(String.format("%s%n%s", "Please invoke with two arguments - setup file and rider file", 
                                "Usage: java -cp .:lib\\* SimulationService <path_to_setup_file> <path_to_rider_file>"));
            return;
        }

        SimulationEngine engine = SimulationEngine.getInstance();

        // If both input files are provided, then load system with that data
        // If no input files provided, then paint a blank UI. user can always upload files from there
        if (args.length==2){ 
            String setupFileStr = args[0].trim();
            String riderFileStr = args[1].trim();

            File setupFile = new File(setupFileStr);

            if (!setupFile.exists() || !setupFile.isFile()) {
                logger.info(String.format("Cannot find file '%s' ", setupFileStr));
                return;
            }

            File riderFile = new File(riderFileStr);

            if (!riderFile.exists() || !riderFile.isFile()) {
                logger.info(String.format("Cannot find file '%s' ", riderFileStr));
                return;
            }

            try{
                engine.initFromFile(setupFileStr, riderFileStr);
            }
            catch(IOException ioe){
                logger.info("Unable to read from input files");
                return;
            }
            catch(Exception e){
                logger.info(String.format(e.getMessage()));
                return;
            }
        }

        runServer();
        Spark.awaitInitialization();

        System.out.println(String.format("%n%nStarted Simulation - Access through browser at http://%s:%d/view.html%n%n","localhost",Spark.port()));
    }

    public static void runServer(){
        staticFiles.location("/static");

        SimulationEngine engine = SimulationEngine.getInstance();

        // A web request handler that returns entire system state
        get("/", (request, response) -> {
            return createJsonSystemState();
        });

        // Process a move bus web request and returns system efficiency along with info about bus & stop impacted by the move
        get("/movebus", (request, response) -> {
            return createJsonMoveBus();
        });

        // Process a rewind web request and returns system efficiency along with info about bus & stop impacted by the rewind
        get("/rewind/:count", (request, response) -> {
            return createJsonRewind(Integer.parseInt(request.params(":count")));
        });

        // Performs a full system reset web request to intial state after file load. Returns the full system state
        get("/reset", (request, response) -> {
            engine.init();
            return createJsonSystemState();
        });

        // Process a changeBus web request 
        post("/changebus", (request, response) -> {

            int busId = Integer.parseInt(request.queryParams("busid"));
            int speed = Integer.parseInt(request.queryParams("speed"));
            int capacity = Integer.parseInt(request.queryParams("capacity"));
            int routechanged = Integer.parseInt(request.queryParams("routechanged"));
        
            if (routechanged==1){
                int routeId = Integer.parseInt(request.queryParams("route"));
                int stopIndex = Integer.parseInt(request.queryParams("stopindex"));
                engine.changeBus(busId, speed, capacity, routeId, stopIndex);
            }
            else{
                engine.changeBus(busId, speed, capacity);
            }

            return "{\"success\":\"true\"}";
        });

        // Process a system constant update web request and returns the new system efficiency
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
            double efficiency = engine.calcSystemEfficiency() ;
            return String.format("%.1f", efficiency ) ;
        } );

        // Process the upload of files from the UI
        post("/upload", (request, response) -> {

            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            ArrayList<String> setupData = new ArrayList<String>();
            ArrayList<String> riderData = new ArrayList<String>();

            try (InputStream input = request.raw().getPart("setupfile").getInputStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                
                String line = null;
                
                while ((line = br.readLine()) != null) {
                    setupData.add(line);
                }
            }

            try (InputStream input = request.raw().getPart("riderfile").getInputStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                
                String line = null;
                
                while ((line = br.readLine()) != null) {
                    riderData.add(line);
                }
            }

            try{
                engine.initFromData(setupData, riderData);
                return String.format("{\"success\":\"true\",\"systemstate\":%s}",createJsonSystemState());
            }
            catch(Exception ex){
                return "Failed to initialize";
            }
        });

        // A utility handler to shut down the simulation service (Java web server)
        post("/stop", (request, response) -> {
            stopServer();
            return "{\"success\":\"true\"";
        });
    }


    // JSON Returning methods below

    // We can always return entire system state for every operation and have the UI redraw everything
    /* But it is much better to just send what is really updated for actions like Move Bus etc.
       where only a bus and stop need to be redrawn in UI */


    private static String createJsonBus(Bus bus){
        return String.format(
            "{\"id\":%d, \"current_stop_id\":%d, \"arrival_time\":%d, \"status\": \"%s\", \"rider_count\":%d,\"route\":%d, \"capacity\":%d, \"speed\":%d}",
            bus.getBusId(), bus.getCurrentStop().getStopId(), bus.getArrivaltime(), bus.toString(), bus.getRiderCount(), 
            bus.getRoute().getRouteId(),bus.getCapacity(),bus.getSpeed());
    }

    private static String createJsonStop(Stop stop){
        return String.format("{\"id\":%d,\"name\":\"%s\",\"waiting\":%d}", stop.getStopId(), stop.getName(), stop.getWaitingCount());
    }

    private static String createJsonRoute(BusRoute route){
        return String.format("{\"id\":%d,\"name\":\"%s\",\"stops\":[%s]}", route.getRouteId(), route.getName(),
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

    public static void stopServer() {
        try {
            Spark.stop();
            while (true) {
                try {
                    Spark.port();
                    Thread.sleep(500);
                } catch (final IllegalStateException ignored) {
                    break;
                }
            }
        } catch (final Exception ex) {
        }
    }
}
