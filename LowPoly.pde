LineSet lines         = new LineSet();
PointSet points       = new PointSet();
TriangleSet triangles = new TriangleSet();
State state           = new State();

public void setup(){
    background(0);
    size(600,500);
    smooth();
    frameRate(120);
}

public void draw(){
    background(0);
    triangles.render();
    lines.render();
    points.render();
}

public void mousePressed(){
    if(state.is("IDLE")){
        lines.start_line(mouseX, mouseY);
        state.change("CREATING_LINE");
    }
}

public void mouseDragged(){
    if(state.is("CREATING_LINE")) lines.drag_line(mouseX, mouseY);
    if(state.is("CREATING_TRIANGLE")) triangles.drag_triangle(mouseX, mouseY);

}

public void mouseReleased(){
    if(state.is("CREATING_LINE")){ 
        lines.end_line();
    }
    if(state.is("CREATING_TRIANGLE")){
        triangles.end_triangle();
    }
    
    if(state.wasnt("IDLE")){
        state.change("IDLE");
    }
}

public void mouseMoved(){
    ArrayList<Integer> near_lines_ids = lines.near(mouseX, mouseY);
    if (near_lines_ids.size()>0) { 
        state.change("CREATING_TRIANGLE");
    } else {
        state.change("IDLE");
    }

    triangles.cancel_triangle();

    if(near_lines_ids.size() > 0){
        int nearest_line_id = near_lines_ids.get(0);
        for(int i=0;i<near_lines_ids.size();i++){
            int the_id = near_lines_ids.get(i);
            Line the_line = lines.find(the_id);
            if(i>0){
                Line previous_line = lines.find(near_lines_ids.get(i-1));
                float this_distance = the_line.distance_between(mouseX, mouseY);
                float distance_before = previous_line.distance_between(mouseX, mouseY);
                if(this_distance < distance_before) nearest_line_id = the_id;
            }
        }

        triangles.start_from_line(nearest_line_id, mouseX, mouseY);        
    }
}

