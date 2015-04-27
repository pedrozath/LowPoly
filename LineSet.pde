class LineSet {
    ArrayList<Line> lines = new ArrayList<Line>();

    int editing_line_id;

    public void start_line(int x, int y){
        int p1_id = points.add(x,y);
        int p2_id = points.add(x,y);
        lines.add(new Line(p1_id, p2_id, lines.size()));
        editing_line_id = lines.size()-1;
    }

    void update_point_id(int old_point_id, int new_point_id){
        for(int i=0;i<lines.size();i++){
            Line l = lines.get(i);
            if(l.p1_id == old_point_id) lines.get(i).p1_id = new_point_id;
            if(l.p2_id == old_point_id) lines.get(i).p2_id = new_point_id;
        }
    }

    int add(int p1_id, int p2_id){
        lines.add(new Line(p1_id, p2_id, lines.size()));
        return lines.size()-1;
    }

    void drag_line(int x, int y){
        lines.get(editing_line_id).drag(x, y);
    }

    void end_line(){

    }

    void render(){
        for(int i=0;i<lines.size();i++){
            lines.get(i).render();
        }
    }

    Line find(int id){
        return lines.get(id);
    }

    void no_hover(){
        for(int i=0;i<lines.size();i++){
            lines.get(i).no_hover();
        }
    }

    IntList near(int x, int y){
        IntList found_lines_ids = new IntList();
        found_lines_ids = new IntList();
        for(int i=0;i<lines.size();i++){
            Line the_line = lines.get(i);
            if(the_line.is_near(x,y) && the_line.triangles_count() < 2) found_lines_ids.append(i);
        }

        return found_lines_ids;
    }

    int nearest(int x, int y){
        IntList near_lines_ids = this.near(x,y);
        int nearest_line_id = -1;
        for(int i=0;i<near_lines_ids.size();i++){
            int the_id = near_lines_ids.get(i);
            Line the_line = this.find(the_id);
            if(i>0){
                Line previous_line = this.find(near_lines_ids.get(i-1));
                float this_distance = the_line.distance_between(mouseX, mouseY);
                float distance_before = previous_line.distance_between(mouseX, mouseY);
                if(this_distance < distance_before) nearest_line_id = the_id;
            } else {
                nearest_line_id = the_id;
            }
        }

        return nearest_line_id;
    }
}