class Point {
    float x, y, id;
    IntList triangles_ids = new IntList();
    Boolean hover = false;
    
    Point(float x, float y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    Point(float x, float y){
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

    public Boolean near(float x, float y, int distance){
        return sqrt(pow(this.x-x,2)+pow(this.y-y,2)) < distance;
    }

    public void hover(){
        hover = true;
    }

    public void add_triangle(int triangle_id){
        this.triangles_ids.append(triangle_id);
    }

    public void render(){
        int r;
        if(hover){
            r = 5;
            hover = false;
        } else {
            r = 2;
        }
        pushStyle();
        fill(0);
        noStroke();
        ellipse(x,y,r,r);
        popStyle();
    }

    public void move(float x, float y){
        this.x = x;
        this.y = y;
        for(int i=0;i<triangles_ids.size();i++){
            if(triangles.find(triangles_ids.get(i)) != null) triangles.find(triangles_ids.get(i)).update_color();
        }
    }

    public void move(Point p){
        this.move(p.x,p.y);
    }

    public void trace(){
        println(this.to_s());
    }

    public String to_s(){
        return "["+x+","+y+"]";
    }
}