public enum ScenarioType{
    ADD_DEPOT(1),
    ADD_STOP(2),
    ADD_ROUTE(3),
    EXTEND_ROUTE(4),
    ADD_BUS(5),
    ADD_EVENT(6)
    ;

    int type;
    ScenarioType(int aType){
        type = aType;
    }
}
