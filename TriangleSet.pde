class TriangleSet {
    ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    int editing_triangle_id = -1;
    int starter_line_id = -1;

    public Triangle find(int id){
        return triangles.get(id);
    }

    public void drag_triangle(int x, int y){
        if(editing_triangle_id > -1) triangles.get(editing_triangle_id).drag(x,y);
    }

    public void update_point_id(int old_point_id, int new_point_id){
        for(int i=0;i<triangles.size();i++){
            Triangle t = triangles.get(i);
            if(t.p1_id == old_point_id) triangles.get(i).p1_id = new_point_id;
            if(t.p2_id == old_point_id) triangles.get(i).p2_id = new_point_id;
            if(t.p3_id == old_point_id) triangles.get(i).p3_id = new_point_id;
        }
    }

    public void start_from_line(int line_id, int x, int y){
        Line l = lines.find(line_id);
        int p3_id = points.add(l.closest_point(x,y));
        // points.start_moving(p3_id);
        // points.move(x, y);
        // points.stop_moving();
        triangles.add(new Triangle(l.p1_id, l.p2_id, p3_id, triangles.size()));
        editing_triangle_id = triangles.size()-1;
        starter_line_id = line_id;
    }

    public void cancel_triangle(){
        if(editing_triangle_id > -1){
            points.destroy(triangles.get(editing_triangle_id).p3_id);
            triangles.set(editing_triangle_id, null);
            editing_triangle_id = -1;
        }
    }

    public void end_triangle(){
        if(editing_triangle_id > -1){
            Triangle t = triangles.get(editing_triangle_id);
            int l1_id = lines.add(t.p2_id, t.p3_id);
            int l2_id = lines.add(t.p3_id, t.p1_id);
            lines.find(starter_line_id).add_triangle(editing_triangle_id);
            lines.find(l1_id).add_triangle(editing_triangle_id);
            lines.find(l2_id).add_triangle(editing_triangle_id);
            editing_triangle_id = -1;
            starter_line_id = -1;
        }
    }

    IntList at(int x, int y){
        IntList found_triangles = new IntList();
        for(int i=0;i<triangles.size();i++){
            Triangle t = triangles.get(i);
            if(t != null){
                if(t.test_collision(x,y)){
                    found_triangles.append(i);
                }
            }
        }
        return found_triangles;
    }

    public void render(){
        for(int i=0;i<triangles.size();i++){
            Triangle my_triangle = triangles.get(i);
            if(my_triangle != null) triangles.get(i).render();
        }
    }
}
