import java.util.HashMap;

public class main {
    //private HashMap<Integer, Stop> stops;

    public static void main(String[] args) {

        if(args[0] == null)
        {
            System.out.print("usage : need to provide input file") ;
            return ;
        }
        FileOperation fileOperation = new FileOperation();
        try {
            fileOperation.loadInputFile(args[0]);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        SystemSimulator systemSimulator = fileOperation.getSystemSimulator() ;
        try{
            systemSimulator.setTransportSystem(fileOperation.getTransportSystem() );
            systemSimulator.startSimulation();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}