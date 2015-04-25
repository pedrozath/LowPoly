class State{
    String state = "IDLE";
    void change(String state){
        this.state = state;
        println("state is now "+state);
    }

    Boolean is(String state){
        return this.state == state;
    }
}