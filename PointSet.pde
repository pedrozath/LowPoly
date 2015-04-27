class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();
    int moving_point_id;

    int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    int add(Point p){
        points.add(p);
        return points.size()-1;
    }

    int size(){
        return points.size();
    }

    void start_moving(int point_id){
        moving_point_id = point_id;
    }

    void move(int x,int y){
        if(moving_point_id > -1) {
            if(this.any_near_except(x,y,moving_point_id)){
                points.get(moving_point_id).move(x,y);
            } else {
                points.get(moving_point_id).move(points.get(this.nearest_except(x,y,moving_point_id)));
            }
        }
    }

    Boolean any_near_except(int x, int y, int p){
        return this.near_except(x,y,p).size() > 0;
    }

    IntList near_except(int x, int y, int p){
        IntList found_ids = this.near(x,y,p);
        found_ids.remove(p_id);
        return found_ids;
    }

    int nearest_except(int x, int y, int p_id){
        return nearest_except(x,y,p_id);
    }

    void stop_moving(){
        Point m = points.get(moving_point_id);
        
        if(this.near(m.x,m.y).size()>0){
            // this.merge(moving_point_id, this.nearest(m));
        }

        moving_point_id = -1;
    }

    void merge(int old_point_id, int new_point_id){
        lines.update_point_id(old_point_id, new_point_id);
        triangles.update_point_id(old_point_id, new_point_id);
        this.destroy(old_point_id);
    }

    void render() {
        for(int i=0;i<points.size();i++){
            Point my_point = points.get(i);
            if(my_point != null) { 
                if(my_point.near(mouseX, mouseY)) my_point.hover();
                my_point.render();
            }
        }
    }

    Point find(int id){
        return points.get(id);
    }

    void destroy(int id){
        points.set(id, null);
    }

    IntList near(int x, int y){
        IntList found_ids = new IntList();
        for(int i=0;i<points.size();i++){
            Point p = points.get(i);
            if(p == null) continue;
            if(sqrt(pow(p.x-x,2)+pow(p.y-y,2)) < 20) found_ids.append(i);
        }

        IntList ordered_found_ids = found_ids;

        for(int i=0;i<found_ids;i++){
            ordered_found_ids.append(2,3);
        }

        return ordered_found_ids;
    }

    int nearest(int x, int y){
        return this.near(x,y).get(0);
    }

    int nearest(Point p){
        return nearest(p.x,p.y);
    }
}