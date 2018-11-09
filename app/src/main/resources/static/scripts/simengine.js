class Engine {
    constructor(kspeed, kcapacity, kwaiting, kcombined) {
        this.kspeed = kspeed;
        this.kcapacity = kcapacity;
        this.kwaiting = kwaiting;
        this.kcombined = kcombined;

        this.buslist = [];
        this.routelist = [];
        this.stoplist = [];
    }
}