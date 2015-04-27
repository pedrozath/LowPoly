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
    if(state.is("MOVING_A_POINT")) points.move(mouseX, mouseY);
}

public void mouseReleased(){
    if(state.is("CREATING_LINE")) lines.end_line();
    if(state.is("CREATING_TRIANGLE")) triangles.end_triangle();
    if(state.is("MOVING_A_POINT")) points.stop_moving();
    if(state.wasnt("IDLE")) state.change("IDLE");
}

public void mouseMoved(){
    IntList near_lines_ids = lines.near(mouseX, mouseY);
    IntList near_points_ids = points.near(mouseX, mouseY);
    
    triangles.cancel_triangle();

    if(points.near(mouseX, mouseY).size() > 0){
        state.change("MOVING_A_POINT");
        points.start_moving(points.near(mouseX, mouseY).get(0));
    } else if(near_lines_ids.size()>0) {         
        state.change("CREATING_TRIANGLE");
        triangles.start_from_line(lines.nearest(mouseX, mouseY), mouseX, mouseY);        
    } else {
        state.change("IDLE");
    }
}








