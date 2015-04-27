LineSet lines         = new LineSet();
PointSet points       = new PointSet();
TriangleSet triangles = new TriangleSet();
State state           = new State();
Template template;

public void setup(){
    background(0);
    colorMode(RGB,255,255,255,100);
    size(1024,768);
    template = new Template();
    smooth();
    frameRate(120);
}

public void draw(){
    template.render();
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
    if(state.isnt("IDLE")) state.change("IDLE");
}

public void mouseMoved(){
    IntList near_lines_ids = lines.near(mouseX, mouseY, 5);
    IntList near_points_ids = points.near(mouseX, mouseY, 2);
    IntList near_triangles_ids = triangles.at(mouseX, mouseY);

    triangles.cancel_triangle();

    if(near_points_ids.size() > 0){
        state.change("MOVING_A_POINT");
        points.start_moving(near_points_ids.get(0));
    } else if(near_lines_ids.size()>0) {         
        state.change("CREATING_TRIANGLE");
        triangles.start_from_line(lines.nearest(mouseX, mouseY, 5), mouseX, mouseY);
    } else if(near_triangles_ids.size()>0){
        state.change("MOVING_A_TRIANGLE");
    } else {
        state.change("IDLE");
    }
}



static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "LowPoly" };
    if (passedArgs != null) {
        PApplet.main(concat(appletArgs, passedArgs));
    } else {
        PApplet.main(appletArgs);
    }
}
