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

class Line {
    int p1_id, p2_id;
    boolean hover;

    Line(int p1_id, int p2_id){
        Point p1 = points.find(p1_id);
        Point p2 = points.find(p1_id);
        if(p1.x < p2.x){
            this.p1_id = p1_id;
            this.p2_id = p2_id; 
        } else {
            this.p1_id = p2_id;
            this.p2_id = p1_id;             
        }
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

    public Boolean is_near(int x, int y){
        Boolean inside_horizontal_range = x > points()[0].x && x < points()[1].x;
        Boolean inside_vertical_range = y > points()[0].y && y < points()[1].y;
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

        if(x < p1.x){
            return new Point(p1);
        } else if(x > p2.x){
            return new Point(p2);
        } else {
            float s = (float)(p2.y-p1.y)/(float)(p2.x-p1.x);
            float ps = 1/-s;
            float cx = (p1.x*s-p1.y-x*ps+y)/(s-ps);
            float cy = (float)s*(cx-p1.x) + p1.y;
            return new Point(round(cx), round(cy));
        }
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
    }

    public void add(int p1_id, int p2_id){
        lines.add(new Line(p1_id, p2_id));
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

    public ArrayList<Integer> near(int x, int y){
        ArrayList<Integer> found_lines_ids = new ArrayList<Integer>();
        found_lines_ids = new ArrayList<Integer>();
        for(int i=0;i<lines.size();i++){
            if(lines.get(i).is_near(x,y)) found_lines_ids.add(i);
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

    Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    public void render(){
        ellipse(x,y,5,5);
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void trace(){
        println(this.to_string());
    }

    public String to_string(){
        return "["+x+","+y+"]";
    }
}
class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();

    public int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    public int add(Point p){
        points.add(p);
        return points.size()-1;
    }

    public void render() {
        for(int i=0;i<points.size();i++){
            Point my_point = points.get(i);
            if(my_point != null) points.get(i).render();
        }
    }

    public Point find(int id){
        return points.get(id);
    }

    public void destroy(int id){
        points.set(id, null);
    }
}
class State{
    String state = "IDLE";
    public void change(String state){
        this.state = state;
        println("i am now "+state);
    }

    public Boolean is(String state){
        return this.state == state;
    }

    public Boolean wasnt(String state){
        return this.state != state;
    }
}
class Triangle {
    int p1_id, p2_id, p3_id;
    int l1_id, l2_id, l3_id;

    Triangle(int p1_id, int p2_id, int p3_id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
        this.p3_id = p3_id;
    }

    public void drag_triangle(int x, int y){
        this.points()[2].x = x;
        this.points()[2].y = y;
    }

    public Point[] points() {
        Point[] my_points = {
            points.find(p1_id),
            points.find(p2_id),
            points.find(p3_id),
        };
        return my_points;
    }

    public void render(){
        pushStyle();
        noStroke();
        fill(120);
        beginShape();
        vertex(points()[0].x, points()[0].y);
        vertex(points()[1].x, points()[1].y);
        vertex(points()[2].x, points()[2].y);
        endShape(CLOSE);
        popStyle();
    }
}
class TriangleSet {
    ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    int editing_triangle_id = -1;

    public void drag_triangle(int x, int y){
        triangles.get(editing_triangle_id).drag_triangle(x,y);
    }

    public void start_from_line(int line_id, int x, int y){
        Line l = lines.find(line_id);
        int p3_id = points.add(l.closest_point(x,y));
        triangles.add(new Triangle(l.p1_id, l.p2_id, p3_id));
        editing_triangle_id = triangles.size()-1;
    }

    public void cancel_triangle(){
        if(editing_triangle_id > -1){
            points.destroy(triangles.get(editing_triangle_id).p3_id);
            triangles.set(editing_triangle_id, null);
            editing_triangle_id = -1;
        }
    }

    public void end_triangle(){
        if(editing_triangle_id > -1){
            Triangle t = triangles.get(editing_triangle_id);
            lines.add(t.p2_id, t.p3_id);
            lines.add(t.p3_id, t.p1_id);
            editing_triangle_id = -1;
        }
    }

    public void render(){
        for(int i=0;i<triangles.size();i++){
            Triangle my_triangle = triangles.get(i);
            if(my_triangle != null) triangles.get(i).render();
        }
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
