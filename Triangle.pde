class Triangle {
    int p1_id, p2_id, p3_id;
    int l1_id, l2_id, l3_id;

    Triangle(int p1_id, int p2_id, int p3_id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
        this.p3_id = p3_id;
    }

    void drag_triangle(int x, int y){
        this.points()[2].x = x;
        this.points()[2].y = y;
    }

    Point[] points() {
        Point[] my_points = {
            points.find(p1_id),
            points.find(p2_id),
            points.find(p3_id),
        };
        return my_points;
    }

    void render(){
        pushStyle();
        noStroke();
        fill(120);
        beginShape();
        vertex(points()[0].x, points()[0].y);
        vertex(points()[1].x, points()[1].y);
        vertex(points()[2].x, points()[2].y);
        endShape(CLOSE);
        popStyle();
    }
}