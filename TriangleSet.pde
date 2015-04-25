class TriangleSet {
    ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    int editing_triangle_id = -1;

    void drag_triangle(int x, int y){
        triangles.get(editing_triangle_id).drag_triangle(x,y);
    }

    void start_from_line(int line_id, int x, int y){
        Line l = lines.find(line_id);
        int p3_id = points.add(l.closest_point(x,y));
        triangles.add(new Triangle(l.p1_id, l.p2_id, p3_id));
        editing_triangle_id = triangles.size()-1;
    }

    void cancel_triangle(){
        if(editing_triangle_id > -1){
            points.destroy(triangles.get(editing_triangle_id).p3_id);
            triangles.set(editing_triangle_id, null);
            editing_triangle_id = -1;
        }
    }

    void end_triangle(){
        if(editing_triangle_id > -1){
            Triangle t = triangles.get(editing_triangle_id);
            lines.add(t.p2_id, t.p3_id);
            lines.add(t.p3_id, t.p1_id);
            editing_triangle_id = -1;
        }
    }

    void render(){
        for(int i=0;i<triangles.size();i++){
            Triangle my_triangle = triangles.get(i);
            if(my_triangle != null) triangles.get(i).render();
        }
    }
}