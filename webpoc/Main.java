import static spark.Spark.*;

import java.io.Console;

public class Main {
    public static void main(String[] args) {
        if (args.length<1) {
            System.out.print("specify some string as argument");
            return;
        }

        Appender.getInstance().mydata = args[0];

        get("/", (request, response) -> {
            return Appender.getInstance().mydata;
        });

        get("/concat/:name", (req, res) -> appendAndRespond(req.params(":name")));

    }

    public static String appendAndRespond(String s){
        Appender.getInstance().mydata += s;
        return Appender.getInstance().mydata;
    }
}