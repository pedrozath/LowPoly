import java.util.*;

LineSet lines         = new LineSet();
PointSet points       = new PointSet();
TriangleSet triangles = new TriangleSet();
State state           = new State();
CameraControl viewport;
Template template;
Keys keys = new Keys();
History history = new History();

public void setup(){
    viewport = new CameraControl();
    colorMode(RGB,255,255,255,100);
    size(1024,768,OPENGL);
    template = new Template();
    smooth(4);
    frameRate(120);
}

public void draw(){
    background(0);
    viewport.apply();
    template.render();
    triangles.render();
    lines.render();
    points.render();
}


public void keyPressed(){
    keys.add(keyCode);
    if(keys.are_pressed("command", "z")) history.undo();
}

public void keyReleased(){
    println(keyCode);
    keys.remove(keyCode);
    println(keys.to_string());
}

public void mouseWheel(MouseEvent e){
    viewport.zoom(e.getCount());
}

public void mousePressed(){
    if(state.is("IDLE")){
        lines.start_line(viewport.mouse());
        state.change("CREATING_LINE");
    }
}

public void mouseDragged(){
    float mx = viewport.mouse().x;
    float my = viewport.mouse().y;
    if(keys.are_pressed("spacebar")) state.change("MOVING_VIEWPORT");
    if(state.is("MOVING_VIEWPORT")) viewport.move(pmouseX-mouseX, pmouseY-mouseY);
    if(state.is("ZOOMING_VIEWPORT")) viewport.zoom(pmouseY-mouseY);
    if(state.is("CREATING_LINE")) lines.drag_line(viewport.mouse());
    if(state.is("CREATING_TRIANGLE")) triangles.drag_triangle(mx, my);
    if(state.is("MOVING_A_POINT")) points.move(mx, my);
}

public void mouseReleased(){
    if(state.is("CREATING_LINE")) lines.end_line();
    if(state.is("CREATING_TRIANGLE")) triangles.end_triangle();
    if(state.is("MOVING_A_POINT")) points.stop_moving();
    if(state.isnt("IDLE")) state.change("IDLE");
    history.save();
}

public void mouseMoved(){
    float mx = viewport.mouse().x;
    float my = viewport.mouse().y;
    IntList near_lines_ids = lines.near(mx, my, 20/viewport.zoom);
    IntList near_points_ids = points.near(mx, my, 2/viewport.zoom);
    IntList near_triangles_ids = triangles.at(mx, my);

    triangles.cancel_triangle();

    if(near_points_ids.size() > 0){
        state.change("MOVING_A_POINT");
        points.start_moving(near_points_ids.get(0));
    } else if(near_lines_ids.size()>0) {         
        state.change("CREATING_TRIANGLE");
        triangles.start_from_line(lines.nearest(mx, my, 20/viewport.zoom), mx, my);
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
