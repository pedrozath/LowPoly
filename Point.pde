class Point {
    int x, y;
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    void render(){
        ellipse(x,y,5,5);
    }

    void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    void trace(){
        println("["+x+","+y+"]");
    }
}
