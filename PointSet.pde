class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();

    int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    void render() {
        for(int i=0;i<points.size();i++){
            points.get(i).render();
        }
    }

    Point find(int id){
        return points.get(id);
    }
}

