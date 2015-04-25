class Point {
    int x, y;
    
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    void render(){
        ellipse(x,y,5,5);
    }

    void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    void trace(){
        println(this.to_string());
    }

    String to_string(){
        return "["+x+","+y+"]";
    }
}