class Engine {
    constructor() {
        this.kspeed = 0;
        this.kcapacity = 0;
        this.kwaiting = 0;
        this.kcombined = 0;
        this.num_rewinds_possible = 0;

        this.buslist = [];
        this.routelist = [];
        this.stoplist = [];

        this.efficiency = 0;
    }

    get_bus(busId){
        return this.buslist.find(b => b.id == busId);
    }

    get_stop(stopId){
        return this.stoplist.find(s => s.id == stopId);
    }

    get_route(routeId){
        return this.routelist.find(r => r.id == routeId);
    }
}