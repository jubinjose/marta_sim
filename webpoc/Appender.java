public class Appender {
    
    public String mydata = "";

    private static Appender instance;
    public static Appender getInstance(){
        if (instance == null) instance = new Appender();
        return instance;
    }

}