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
    render();
}

public void render(){
    background(0);
    points.render();
    lines.render();
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
    for(int i=0;i<near_lines_ids.size();i++){
        // lines.find(near_lines_ids.get(i)).start_triangle();
        break;
    }
}

