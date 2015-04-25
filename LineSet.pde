class LineSet {
    ArrayList<Line> lines = new ArrayList<Line>();
    int editing_line_id;

    public void start_line(int x, int y){
        int p1_id = points.add(x,y);
        int p2_id = points.add(x,y);
        lines.add(new Line(p1_id, p2_id));
        editing_line_id = lines.size()-1;
    }

    void add(int p1_id, int p2_id){
        lines.add(new Line(p1_id, p2_id));
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

    ArrayList<Integer> near(int x, int y){
        ArrayList<Integer> found_lines_ids = new ArrayList<Integer>();
        found_lines_ids = new ArrayList<Integer>();
        for(int i=0;i<lines.size();i++){
            if(lines.get(i).is_near(x,y)) found_lines_ids.add(i);
        }

        return found_lines_ids;
    }
}