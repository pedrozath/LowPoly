class Point {
    int x, y;
    Boolean hover = false;
    
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    void render(){
        int r;
        if(hover) r = 10; else r = 5;
        ellipse(x,y,r,r);
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