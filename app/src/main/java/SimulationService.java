import java.io.*;
import static spark.Spark.*;

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

        RunServer();
    }

    public static void RunServer(){
        staticFiles.location("/static");

        get("/", (request, response) -> {
            return SimulationEngine.getInstance().getState();
        });

        get("/movebus", (request, response) -> {
            return SimulationEngine.getInstance().moveBus();
        });

        get("/replay", (request, response) -> {
            return SimulationEngine.getInstance().replay();
        });

        get("/reset", (request, response) -> {
            SimulationEngine.getInstance().init();
            return SimulationEngine.getInstance().getState();
        });

    }
}
