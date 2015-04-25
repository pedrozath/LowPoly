class State{
    String state = "IDLE";
    void change(String state){
        this.state = state;
        println("i am now "+state);
    }

    Boolean is(String state){
        return this.state == state;
    }

    Boolean wasnt(String state){
        return this.state != state;
    }
}