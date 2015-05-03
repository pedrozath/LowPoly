class Line {
    int p1_id, p2_id, id;
    boolean hover;
    IntList triangles_ids = new IntList();

    Line(int p1_id, int p2_id, int id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
        this.id = id;
    }

    public void hover(){
        hover = true;
    }

    public void no_hover(){
        hover = false;
    }

    public void add_triangle(int t_id){
        triangles_ids.append(t_id);
    }

    public int triangles_count(){
        return triangles_ids.size();
    }

    public void render(){
        Point p1 = points.find(p1_id);
        Point p2 = points.find(p2_id);
        pushStyle();
        stroke(255);
        strokeWeight((float)0.2);
        if(hover) strokeWeight(2);
        line(p1.x, p1.y, p2.x, p2.y);
        popStyle();
    }

    public void drag(float x, float y){
        Point p = this.points()[1];
        p.move(x,y);
    }

    public Boolean is_near(float x, float y, float distance){
        Boolean inside_horizontal_range = x > points()[0].x && x < points()[1].x;
        Boolean inside_vertical_range = y > points()[0].y && y < points()[1].y;
        return this.distance_between(x, y) < distance;
    }

    public Point[] points(){
        Point[] my_points = { points.find(p1_id), points.find(p2_id) };
        return my_points;
    }

    public Point[] ordered_points(){
        if(points()[0].x > points()[1].x){
            Point[] output = { points()[1], points()[0] };
            return output;
        } else {
            return points();
        }
    }

    public float distance_between(float x, float y){
        Point p = this.closest_point(x,y);
        return sqrt(pow(x-p.x,2)+pow(y-p.y,2));
    }

    public Point closest_point(float x, float y){
        Point p1 = this.ordered_points()[0];
        Point p2 = this.ordered_points()[1];
        if(x < p1.x){
            return p1;
        } else if(x > p2.x){
            return p2;
        } else {
            float s = (float)(p2.y-p1.y)/(float)(p2.x-p1.x);
            float ps = 1/-s;
            float cx = (p1.x*s-p1.y-x*ps+y)/(s-ps);
            float cy = (float)s*(cx-p1.x) + p1.y;
            return new Point(round(cx), round(cy));
        }
    }
}