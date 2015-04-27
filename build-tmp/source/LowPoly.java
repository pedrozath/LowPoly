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








class Line {
    int p1_id, p2_id, id;
    boolean hover;
    IntList triangles_ids = new IntList();

    Line(int p1_id, int p2_id, int id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
        this.id = id;
    }

    public void hover(){
        hover = true;
    }

    public void no_hover(){
        hover = false;
    }

    public void add_triangle(int t_id){
        triangles_ids.append(t_id);
    }

    public int triangles_count(){
        return triangles_ids.size();
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

    public Point[] ordered_points(){
        if(points()[0].x > points()[1].x){
            Point[] output = { points()[1], points()[0] };
            return output;
        } else {
            return points();
        }
    }

    public float distance_between(int x, int y){
        Point p = this.closest_point(x,y);
        return sqrt(pow(x-p.x,2)+pow(y-p.y,2));
    }

    public Point closest_point(int x, int y){
        Point p1 = this.ordered_points()[0];
        Point p2 = this.ordered_points()[1];
        if(x < p1.x){
            return p1;
        } else if(x > p2.x){
            return p2;
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
        lines.add(new Line(p1_id, p2_id, lines.size()));
        editing_line_id = lines.size()-1;
    }

    public void update_point_id(int old_point_id, int new_point_id){
        for(int i=0;i<lines.size();i++){
            Line l = lines.get(i);
            if(l.p1_id == old_point_id) lines.get(i).p1_id = new_point_id;
            if(l.p2_id == old_point_id) lines.get(i).p2_id = new_point_id;
        }
    }

    public int add(int p1_id, int p2_id){
        lines.add(new Line(p1_id, p2_id, lines.size()));
        return lines.size()-1;
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

    public IntList near(int x, int y){
        IntList found_lines_ids = new IntList();
        found_lines_ids = new IntList();
        for(int i=0;i<lines.size();i++){
            Line the_line = lines.get(i);
            if(the_line.is_near(x,y) && the_line.triangles_count() < 2) found_lines_ids.append(i);
        }

        return found_lines_ids;
    }

    public int nearest(int x, int y){
        IntList near_lines_ids = this.near(x,y);
        int nearest_line_id = -1;
        for(int i=0;i<near_lines_ids.size();i++){
            int the_id = near_lines_ids.get(i);
            Line the_line = this.find(the_id);
            if(i>0){
                Line previous_line = this.find(near_lines_ids.get(i-1));
                float this_distance = the_line.distance_between(mouseX, mouseY);
                float distance_before = previous_line.distance_between(mouseX, mouseY);
                if(this_distance < distance_before) nearest_line_id = the_id;
            } else {
                nearest_line_id = the_id;
            }
        }

        return nearest_line_id;
    }
}
class Point {
    int x, y, id;
    Boolean hover = false;
    
    Point(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    Point(Point p, int id){
        this.x = p.x;
        this.y = p.y;
        this.id = id;
    }

    public Boolean near(int x, int y){
        return sqrt(pow(this.x-x,2)+pow(this.y-y,2)) < 20;
    }

    public void hover(){
        hover = true;
    }

    public void render(){
        int r;
        if(hover){
            r = 10;
            hover = false;
        } else {
            r = 5;
        }
        ellipse(x,y,r,r);
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void move(Point p){
        this.move(p.x,p.y);
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
    int moving_point_id;

    public int add(int x,int y) {
        points.add(new Point(x,y));
        return points.size()-1;
    }

    public int add(Point p){
        points.add(p);
        return points.size()-1;
    }

    public int size(){
        return points.size();
    }

    public void start_moving(int point_id){
        moving_point_id = point_id;
    }

    public void move(int x,int y){
        if(moving_point_id > -1) {
            if(this.any_near_except(x,y,moving_point_id)){
                points.get(moving_point_id).move(x,y);
            } else {
                points.get(moving_point_id).move(points.get(this.nearest_except(x,y,moving_point_id)));
            }
        }
    }

    public Boolean any_near_except(int x, int y, int p){
        return this.near_except(x,y,p).size() > 0;
    }

    public IntList near_except(int x, int y, int p){
        IntList found_ids = this.near(x,y,p);
        found_ids.remove(p_id);
        return found_ids;
    }

    public int nearest_except(int x, int y, int p_id){
        return nearest_except(x,y,p_id);
    }

    public void stop_moving(){
        Point m = points.get(moving_point_id);
        
        if(this.near(m.x,m.y).size()>0){
            // this.merge(moving_point_id, this.nearest(m));
        }

        moving_point_id = -1;
    }

    public void merge(int old_point_id, int new_point_id){
        lines.update_point_id(old_point_id, new_point_id);
        triangles.update_point_id(old_point_id, new_point_id);
        this.destroy(old_point_id);
    }

    public void render() {
        for(int i=0;i<points.size();i++){
            Point my_point = points.get(i);
            if(my_point != null) { 
                if(my_point.near(mouseX, mouseY)) my_point.hover();
                my_point.render();
            }
        }
    }

    public Point find(int id){
        return points.get(id);
    }

    public void destroy(int id){
        points.set(id, null);
    }

    public IntList near(int x, int y){
        IntList found_ids = new IntList();
        for(int i=0;i<points.size();i++){
            Point p = points.get(i);
            if(p == null) continue;
            if(sqrt(pow(p.x-x,2)+pow(p.y-y,2)) < 20) found_ids.append(i);
        }

        IntList ordered_found_ids = found_ids;

        for(int i=0;i<found_ids;i++){
            ordered_found_ids.append(2,3);
        }

        return ordered_found_ids;
    }

    public int nearest(int x, int y){
        return this.near(x,y).get(0);
    }

    public int nearest(Point p){
        return nearest(p.x,p.y);
    }
}
class State{
    String state = "IDLE";
    public void change(String state){
        this.state = state;
        // println("i am now "+state);
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
    int starter_line_id = -1;

    public void drag_triangle(int x, int y){
        if(editing_triangle_id > -1) triangles.get(editing_triangle_id).drag_triangle(x,y);
    }

    public void update_point_id(int old_point_id, int new_point_id){
        for(int i=0;i<triangles.size();i++){
            Triangle t = triangles.get(i);
            if(t.p1_id == old_point_id) triangles.get(i).p1_id = new_point_id;
            if(t.p2_id == old_point_id) triangles.get(i).p2_id = new_point_id;
            if(t.p3_id == old_point_id) triangles.get(i).p3_id = new_point_id;
        }
    }

    public void start_from_line(int line_id, int x, int y){
        Line l = lines.find(line_id);
        int p3_id = points.add(l.closest_point(x,y));
        // points.start_moving(p3_id);
        // points.move(x, y);
        // points.stop_moving();
        triangles.add(new Triangle(l.p1_id, l.p2_id, p3_id));
        editing_triangle_id = triangles.size()-1;
        starter_line_id = line_id;
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
            int l1_id = lines.add(t.p2_id, t.p3_id);
            int l2_id = lines.add(t.p3_id, t.p1_id);
            lines.find(starter_line_id).add_triangle(editing_triangle_id);
            lines.find(l1_id).add_triangle(editing_triangle_id);
            lines.find(l2_id).add_triangle(editing_triangle_id);
            editing_triangle_id = -1;
            starter_line_id = -1;
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
