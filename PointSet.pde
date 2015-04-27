class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();
    int moving_point_id;

    public int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    public int add(Point p){
        points.add(p);
        return points.size()-1;
    }

    public int size(){
        return points.size();
    }

    public void start_moving(int point_id){
        moving_point_id = point_id;
    }

    public void move(int x,int y){
        if(moving_point_id > -1) {
            Point moving_point = points.get(moving_point_id);
        //     if(this.any_near_except(x,y,moving_point_id)){
                if(moving_point != null) moving_point.move(x,y);
        //     } else {
                // points.get(moving_point_id).move(points.get(this.nearest_except(x,y,moving_point_id)));
        //     }
        }
    }

    // public Boolean any_near_except(int x, int y, int p){
    //     return this.near_except(x,y,p).size() > 0;
    // }

    // public IntList near_except(int x, int y, int p_id){
    //     IntList found_ids = this.near(x,y);
    //     // found_ids.remove(p_id);
    //     return found_ids;
    // }

    // public int nearest_except(int x, int y, int p_id){
    //     return nearest_except(x,y,p_id);
    // }

    public void stop_moving(){
        Point m = points.get(moving_point_id);
        
        // if(this.near(m.x,m.y).size()>0){
            // this.merge(moving_point_id, this.nearest(m));
        // }

        moving_point_id = -1;
    }

    public void merge(int old_point_id, int new_point_id){
        lines.update_point_id(old_point_id, new_point_id);
        triangles.update_point_id(old_point_id, new_point_id);
        this.destroy(old_point_id);
    }

    public void render() {
        for(int i=0;i<points.size();i++){
            Point my_point = points.get(i);
            if(my_point != null) { 
                if(my_point.near(mouseX, mouseY, 2)) my_point.hover();
                my_point.render();
            }
        }
    }

    public Point find(int id){
        return points.get(id);
    }

    public void destroy(int id){
        points.set(id, null);
    }

    public IntList near(int x, int y, int distance){
        IntList found_ids = new IntList();
        for(int i=0;i<points.size();i++){
            Point p = points.get(i);
            if(p == null) continue;
            if(sqrt(pow(p.x-x,2)+pow(p.y-y,2)) < distance) found_ids.append(i);
        }

        return found_ids;
    }

    public int nearest(int x, int y, int distance){
        return this.near(x,y,distance).get(0);
    }

    public int nearest(Point p, int distance){
        return nearest(p.x,p.y,distance);
    }
}
