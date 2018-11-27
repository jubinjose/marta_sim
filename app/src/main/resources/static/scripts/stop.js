class Stop {
    constructor(id, name, waiting_count) {
        this.id = id;
        this.name = name;
        this.waiting_count = waiting_count;
        this.buslist = [];
    }

    get_display_info(){
        return this.name + " [" + this.id + "]<br>" + "waiting : " + this.waiting_count;
    }
    
}