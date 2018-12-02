import java.io.*;
import java.util.*;

// This class exists solely to bulk test the simulation engine against a folder with many input files
// It generates output files corresponding to each input file for 20 simulation steps
// This allows a folder diff against a known solutions folder for exact match
// Note that a rider file still needs to be provided as argument though the test is only to validate thge move bus logic
public class SimulationTester {
	public static void main(String[] args) {

        if (args.length<2){
            System.out.printf("%n%s%n%n%s%nOR%n%s%nOR%n%s%n", "Insufficient arguments. Invoke as shown below", 
                                "java SimulationTester <path_to_setup_file> <path_to_rider_file>",
                                "java SimulationTester <path_to_folder_with_setup_files> <path_to_rider_file>",
                                "java SimulationTester <path_to_folder_with_setup_files> <path_to_rider_file> <path_to_output_folder>");
            return;
        }

        String setupFileStr = args[0].trim();
        String riderFileStr = args[1].trim();

        File setupFile = new File(setupFileStr);

        if (!setupFile.exists()) {
            System.out.printf("Cannot find '%s' ", setupFileStr);
            return;
        }
        
        File riderFile = new File(riderFileStr);

        if (!riderFile.exists()) {
            System.out.printf("Cannot find '%s' ", riderFileStr);
            return;
        }
        
        SimulationEngine engine = SimulationEngine.getInstance();

        if (setupFile.isFile()){

            try{
                engine.initFromFile(setupFileStr, riderFileStr);
            }
            catch(IOException ioe){
                System.out.printf("Unable to read from one of these files - %n%s%n%s", setupFileStr, riderFileStr);
                return;
            }
            catch(Exception e){
                System.out.printf(e.getMessage());
                return;
            }

            List<String> outputList = engine.runSimulation(20);
            for (String eventMessage: outputList){
                System.out.print(String.format("%s%n", eventMessage));
            }
        }
        else{ 
            // For quick test against many test files -> give folder containing test files as first argument. Second argument is rider file. 
            // Pass output folder as optional third argument. if not specified, we default to folder named 'output' in input folder

            File outputDir = args.length >= 3 ? new File(args[2].trim()) : new File(setupFile ,"output");

            boolean outputDirExists = outputDir.exists();

            try {
            	if (!outputDirExists){
                    outputDirExists = outputDir.mkdirs();
                }
            }
            catch(Exception ex) {
            	System.out.printf("Could not create output folder '%s'", outputDir);
            	return;
            }
            
            File[] dirListing = setupFile.listFiles();

            for (File file : dirListing) {

                if (!file.isDirectory()){ // Ignore any inner directories

                    String filePathStr = file.getPath();
                    try{
                        engine.initFromFile(filePathStr, riderFileStr);
                    }
                    catch(IOException ioe){
                        System.out.printf("Unable to read from one of these files - %n%s%n%s", filePathStr, riderFileStr);
                        return;
                    }
                    catch(Exception e){
                        System.out.printf("%nError in initFromFile for following input files%n%n%s%n%s%n%n%s",
                        		filePathStr, riderFileStr, e.getMessage());
                        continue;
                    }

                    List<String> outputList = engine.runSimulation(20);
                    File outFile = new File(outputDir, file.getName().toString().split("_")[0] + "_solution.txt");

                    try{
                        FileWriter fw = new FileWriter(outFile);
                        for (String eventMessage: outputList){
                            
                        	// Hack to make diff easier with provided solutions
                            // which means hardcode passengers and fuel and set both to 0
                            eventMessage = eventMessage.split("//")[0] + "//p:0/f:0";

                            fw.write(String.format("%s%n", eventMessage));
                        }
                        fw.close();
                    }
                    catch(Exception e){
                        System.out.printf("Unable to write to file '%s'", outFile.getAbsolutePath());
                    }
                }
            }
        }
    }
}
