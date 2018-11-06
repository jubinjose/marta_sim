import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        if (args.length<1){
            System.out.printf("%s%n%s", "Input file not provided!", 
                                "Usage: java -jar working_system.jar <path_to_input_file>");
            return;
        }

        String inputFileStr = args[0].trim();

        File inputFile = new File(inputFileStr);

        if (!inputFile.exists()) {
            System.out.printf("Cannot find '%s' ", inputFileStr);
            return;
        }

        SimulationEngine engine = new SimulationEngine();

        if (inputFile.isFile()){

            try{
                engine.initFromFile(inputFileStr);
            }
            catch(IOException ioe){
                System.out.printf("Unable to read from file '%s' ", inputFileStr);
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
            // For quick test against many test files -> give folder containing test files as argument. 
            // Pass output folder as optional second argument. if not specified, we default to folder named 'output' in input folder

            File outputDir = args.length >= 2 ? new File(args[1].trim()) : new File(inputFile ,"output");

            boolean outputDirExists = outputDir.exists();

            if (!outputDirExists){
                outputDirExists = outputDir.mkdirs();
            }

            if (outputDirExists){

                File[] dirListing = inputFile.listFiles();

                for (File file : dirListing) {

                    if (!file.isDirectory()){ // Ignore any subfolders

                        String absoluteFilePath = file.getAbsolutePath();
                        try{
                            engine.initFromFile(absoluteFilePath);
                        }
                        catch(IOException ioe){
                            System.out.printf("Unable to read from file '%s' ", absoluteFilePath);
                            continue;
                        }
                        catch(Exception e){
                            System.out.printf("Error processing file '%s'%n%s", absoluteFilePath, e.getMessage());
                            continue;
                        }

                        List<String> outputList = engine.runSimulation(20);
                        File outFile = new File(outputDir, file.getName().toString().split("_")[0] + "_solution.txt");

                        try{
                            FileWriter fw = new FileWriter(outFile);
                            for (String eventMessage: outputList){
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
            else{
                System.out.printf("Could not create output folder '%s'", outputDir);
            }
        }
    }

}
