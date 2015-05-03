class History{
    ArrayList<Object[]> backups = new ArrayList();
    int current_state = 0;

    void save(){
        for(int i=current_state; i<backups.size(); i++){
            backups.remove(i);
        }
        backups.add(new Object[]{ 
            new PointSet(points), 
            new LineSet(lines), 
            new TriangleSet(triangles)
        });
        current_state++;
    }

    void load(int state){
        if(state > 0){
            points = (PointSet) backups.get(state)[0];
            lines = (LineSet) backups.get(state)[1];
            triangles = (TriangleSet) backups.get(state)[2];
            current_state = state;
        }
    }

    void undo(){
        load(current_state-1);
        println(current_state);
    }
}