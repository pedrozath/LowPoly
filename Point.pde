class Point {
    int x, y, id;
    Boolean hover = false;
    
    Point(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    Point(Point p, int id){
        this.x = p.x;
        this.y = p.y;
        this.id = id;
    }

    Boolean near(int x, int y){
        return sqrt(pow(this.x-x,2)+pow(this.y-y,2)) < 20;
    }

    void hover(){
        hover = true;
    }

    void render(){
        int r;
        if(hover){
            r = 10;
            hover = false;
        } else {
            r = 5;
        }
        ellipse(x,y,r,r);
    }

    void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    void move(Point p){
        this.move(p.x,p.y);
    }

    void trace(){
        println(this.to_string());
    }

    String to_string(){
        return "["+x+","+y+"]";
    }
}