class Line {
    int p1_id, p2_id;
    boolean hover;

    Line(int p1_id, int p2_id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
    }

    void hover(){
        hover = true;
    }

    void no_hover(){
        hover = false;
    }

    void render(){
        Point p1 = points.find(p1_id);
        Point p2 = points.find(p2_id);
        pushStyle();
        stroke(255);
        strokeWeight(1);
        if(hover) strokeWeight(2);
        line(p1.x, p1.y, p2.x, p2.y);
        popStyle();
    }

    void drag(int x, int y){
        Point p = this.points()[1];
        p.move(x,y);
    }

    Boolean is_near(int x, int y){
        return this.distance_between(x, y) < 20;
    }

    Point[] points(){
        Point[] my_points = { points.find(p1_id), points.find(p2_id) };
        return my_points;
    }

    float distance_between(int x, int y){
        Point p = this.closest_point(x,y);
        return sqrt(pow(x-p.x,2)+pow(y-p.y,2));
    }

    Point closest_point(int x, int y){
        Point p1 = this.points()[0];
        Point p2 = this.points()[1];
        float s = (float)(p2.y-p1.y)/(float)(p2.x-p1.x);
        float ps = 1/-s;
        float cx = (p1.x*s-p1.y-x*ps+y)/(s-ps);
        float cy = (float)s*(cx-p1.x) + p1.y;
        return new Point(round(cx), round(cy));
    }
}