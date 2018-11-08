import java.util.Scanner;
import java.io.File;

public class FileOperation {
    private String fileName ;
    public TransportSystem transportSystem ;
    public SystemSimulator systemSimulator ;
    public FileOperation()
    {
        transportSystem = new TransportSystem() ;
        systemSimulator = new SystemSimulator() ;
    }

    public void loadInputFile(String strInputFile){

        try {
            Scanner inputData = new Scanner(new File(strInputFile));
            do {
                String strFileLine = inputData.nextLine();
                String[] tokens = strFileLine.split(",");
                int stopID;
                int busID;
                int depotID ;
                switch (tokens[0]) {
                    case "add_depot":
                        depotID = transportSystem.addDepot(Integer.parseInt(tokens[1]), tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
                        break;
                    case "add_stop":
                        stopID = transportSystem.addStop(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]));
                        break;
                    case "add_route":
                        int routeID = transportSystem.addRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
                        //transportSystem.extendRoute(routeID, );
                        break;
                    case "extend_route":
                        transportSystem.extendRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                        break;
                    case "add_bus":
                        busID = transportSystem.addBus(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]), Integer.parseInt(tokens[8]));
                        break;
                    case "add_event":
                        systemSimulator.addEvent(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]) );
                        break;
                }
            }while (inputData.hasNextLine());
            inputData.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTransportSystem(TransportSystem transportSystem) {
        this.transportSystem = transportSystem;
    }

    public void setSystemSimulator(SystemSimulator systemSimulator) {
        this.systemSimulator = systemSimulator;
    }
    public String readLine(){
        return "" ;
    }
    public String readNext(){
        return "" ;
    }
    public SystemSimulator getSystemSimulator()
    {
        return systemSimulator ;
    }
    public TransportSystem getTransportSystem()
    {
        return transportSystem ;
    }
}
