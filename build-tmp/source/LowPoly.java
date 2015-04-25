import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class LowPoly extends PApplet {

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
    ArrayList<Integer> near_lines_ids = lines.find_by_coords(mouseX, mouseY);
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

class Line {
    int p1_id, p2_id;
    boolean hover;

    Line(int p1_id, int p2_id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
    }

    public void hover(){
        hover = true;
    }

    public void no_hover(){
        hover = false;
    }

    public void render(){
        Point p1 = points.find(p1_id);
        Point p2 = points.find(p2_id);
        pushStyle();
        stroke(255);
        strokeWeight(1);
        if(hover) strokeWeight(2);
        line(p1.x, p1.y, p2.x, p2.y);
        popStyle();
    }

    public void drag(int x, int y){
        Point p = this.points()[1];
        p.move(x,y);
    }

    public Boolean near(int x, int y){
        return this.distance_between(x, y) < 20;
    }

    public Point[] points(){
        Point[] my_points = { points.find(p1_id), points.find(p2_id) };
        return my_points;
    }

    public float distance_between(int x, int y){
        Point p = this.closest_point(x,y);
        return sqrt(pow(x-p.x,2)+pow(y-p.y,2));
    }

    public Point closest_point(int x, int y){
        Point p1 = this.points()[0];
        Point p2 = this.points()[1];
        float s = (float)(p2.y-p1.y)/(float)(p2.x-p1.x);
        float ps = 1/-s;
        float cx = (p1.x*s-p1.y-x*ps+y)/(s-ps);
        float cy = (float)s*(cx-p1.x) + p1.y;
        return new Point(round(cx), round(cy));
    }
}
class LineSet {
    ArrayList<Line> lines = new ArrayList<Line>();
    int editing_line_id;

    public void start_line(int x, int y){
        int p1_id = points.add(x,y);
        int p2_id = points.add(x,y);
        lines.add(new Line(p1_id, p2_id));
        editing_line_id = lines.size()-1;
        println(editing_line_id);
    }

    public void drag_line(int x, int y){
        lines.get(editing_line_id).drag(x, y);
    }

    public void end_line(){

    }

    public void render(){
        for(int i=0;i<lines.size();i++){
            lines.get(i).render();
        }
    }

    public Line find(int id){
        return lines.get(id);
    }

    public void no_hover(){
        for(int i=0;i<lines.size();i++){
            lines.get(i).no_hover();
        }
    }

    public ArrayList<Integer> find_by_coords(int x, int y){
        ArrayList<Integer> found_lines_ids = new ArrayList<Integer>();
        for(int i=0;i<lines.size();i++){
            found_lines_ids = new ArrayList<Integer>();
            if(lines.get(i).near(x,y)) found_lines_ids.add(i);
            // break;
        }
        return found_lines_ids;
    }
}
class Point {
    int x, y;
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void render(){
        ellipse(x,y,5,5);
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void trace(){
        println("["+x+","+y+"]");
    }
}
class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();

    public int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    public void render() {
        for(int i=0;i<points.size();i++){
            points.get(i).render();
        }
    }

    public Point find(int id){
        return points.get(id);
    }
}

class State{
    String state = "IDLE";
    public void change(String state){
        this.state = state;
        println("state is now "+state);
    }

    public Boolean is(String state){
        return this.state == state;
    }
}
class TriangleSet {
    int p1_id, p2_id, p3_id;

    public void drag_triangle(int x, int y){
        this.points()[2].x = x;
        this.points()[2].y = y;
    }

    public void start_triangle(){
        
    }

    public Point[] points() {
        Point[] my_points = {
            points.find(p1_id),
            points.find(p2_id),
            points.find(p3_id),
        };
        return my_points;
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
}
