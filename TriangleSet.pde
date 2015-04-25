class TriangleSet {
    int p1_id, p2_id, p3_id;

    void drag_triangle(int x, int y){
        this.points()[2].x = x;
        this.points()[2].y = y;
    }

    void start_triangle(){
        
    }

    Point[] points() {
        Point[] my_points = {
            points.find(p1_id),
            points.find(p2_id),
            points.find(p3_id),
        };
        return my_points;
    }
}

