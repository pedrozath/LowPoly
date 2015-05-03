class State{
    String state = "IDLE";
    public void change(String state){
        this.state = state;
        // println("i am now "+state);
    }

    public Boolean is(String state){
        return this.state == state;
    }

    public Boolean isnt(String state){
        return this.state != state;
    }
}