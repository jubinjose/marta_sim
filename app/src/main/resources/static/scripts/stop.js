class Stop {
    constructor(id, name, waiting_count) {
        this.id = id;
        this.name = name;
        this.waiting_count = waiting_count;
        this.buslist = [];
    }
}