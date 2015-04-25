class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();

    int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    int add(Point p){
        points.add(p);
        return points.size()-1;
    }

    void render() {
        for(int i=0;i<points.size();i++){
            Point my_point = points.get(i);
            if(my_point != null) points.get(i).render();
        }
    }

    Point find(int id){
        return points.get(id);
    }

    void destroy(int id){
        points.set(id, null);
    }
}