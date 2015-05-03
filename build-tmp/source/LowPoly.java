import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

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
class CameraControl{
    float x = 0;
    float y = 0;
    float zoom = 1;

    // float upX = 0;
    // float upY = 1;
    // float upZ = 0;

    public void apply(){
        // camera(x, y, z, x, y, 0, upX, upY, upZ);
        translate(x,y);
        scale(zoom);
    }

    public void zoom(float n){
        // center(this.mouse());
        zoom -= n/100;
        // println(z);
    }

    public void center(float x, float y){
        this.move((x/2)-(width/2), (y/2)-(height/2));
    }

    public void center(PVector p){
        this.center(p.x, p.y);
    }

    public void move(float x, float y){
        this.x -= x;
        this.y -= y;
    }

    public PVector mouse(){
        return new PVector((mouseX-x)/zoom,(mouseY-y)/zoom);
    }
}
class History{
    ArrayList<Object[]> backups = new ArrayList();
    int current_state = 0;

    public void save(){
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

    public void load(int state){
        if(state > 0){
            points = (PointSet) backups.get(state)[0];
            lines = (LineSet) backups.get(state)[1];
            triangles = (TriangleSet) backups.get(state)[2];
            current_state = state;
        }
    }

    public void undo(){
        load(current_state-1);
        println(current_state);
    }
}
class Keys{
    IntList pressed = new IntList();
    HashMap<String,Integer> keys_by_name = new HashMap<String,Integer>();

    Keys(){
        keys_by_name.put("spacebar", 32);
        keys_by_name.put("left control", 17);
        keys_by_name.put("option", 18);
        keys_by_name.put("command", 157);
        keys_by_name.put("shift", 16);
        keys_by_name.put("delete", 127);
        keys_by_name.put("z", 90);
    }

    public void add(int key){
        if(isnt_pressed(key)) this.pressed.append(key);
    }

    public void remove(int key){
        if(is_pressed(key)) this.pressed.remove(index(key));
    }

    public String to_string(){
        String output = "";
        for(int k=0;k<pressed.size();k++){
            output += pressed.get(k) + ": " + key_name(pressed.get(k)) + "; ";
        }
        return output;
    }

    public String key_name(int key_code){
        String output = "Key name not found";
        for(Map.Entry entry : keys_by_name.entrySet()){
            if(key_code == (Integer)entry.getValue()) output = (String)entry.getKey();
        }
        return output;
    }

    public int index(int key_code){
        Integer key_index = -1;
        for(int i=0;i<pressed.size();i++){
            int key = pressed.get(i);
            if(key_code==key){
                key_index = i;
            }
        }
        return key_index;
    }

    public Boolean are_pressed(String... key_names){
        Boolean output = is_pressed(key_names[0]);
        for(int i=1;i<key_names.length;i++){
            output = is_pressed(key_names[i]) && is_pressed(key_names[i-1]);
        }
        return output;
    }

    public Boolean arent_pressed(String... key_names){
        return !this.are_pressed(key_names);
    }

    public Boolean is_pressed(String key_name){
        return this.is_pressed(this.keys_by_name.get(key_name));
    }

    public Boolean is_pressed(int key_code){
        return this.index(key_code) > -1;    
    }

    public Boolean isnt_pressed(int key_code){
        return this.index(key_code) == -1;    
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
        strokeWeight((float)0.2f);
        if(hover) strokeWeight(2);
        line(p1.x, p1.y, p2.x, p2.y);
        popStyle();
    }

    public void drag(float x, float y){
        Point p = this.points()[1];
        p.move(x,y);
    }

    public Boolean is_near(float x, float y, float distance){
        Boolean inside_horizontal_range = x > points()[0].x && x < points()[1].x;
        Boolean inside_vertical_range = y > points()[0].y && y < points()[1].y;
        return this.distance_between(x, y) < distance;
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

    public float distance_between(float x, float y){
        Point p = this.closest_point(x,y);
        return sqrt(pow(x-p.x,2)+pow(y-p.y,2));
    }

    public Point closest_point(float x, float y){
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
    int editing_line_id = -1;

    LineSet(){

    }

    LineSet(Object lineset){
        this.lines = new ArrayList<Line>(((LineSet)lineset).lines);
    }

    public void start_line(float x, float y){
        int p1_id = points.add(x,y);
        int p2_id = points.add(x,y);
        lines.add(new Line(p1_id, p2_id, lines.size()));
        editing_line_id = lines.size()-1;
    }

    public void start_line(PVector p){
        start_line(p.x, p.y);
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

    public void drag_line(float x, float y){
        lines.get(editing_line_id).drag(x, y);
    }

    public void drag_line(PVector p){
        drag_line(p.x, p.y);
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

    public IntList near(float x, float y, float distance){
        IntList found_lines_ids = new IntList();
        found_lines_ids = new IntList();
        for(int i=0;i<lines.size();i++){
            Line the_line = lines.get(i);
            if(the_line.is_near(x,y, distance) && the_line.triangles_count() < 2) found_lines_ids.append(i);
        }

        return found_lines_ids;
    }

    public int nearest(float x, float y, float distance){
        IntList near_lines_ids = this.near(x,y,distance);
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
    float x, y, id;
    IntList triangles_ids = new IntList();
    Boolean hover = false;
    
    Point(float x, float y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    Point(float x, float y){
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

    public Boolean near(float x, float y, int distance){
        return sqrt(pow(this.x-x,2)+pow(this.y-y,2)) < distance;
    }

    public void hover(){
        hover = true;
    }

    public void add_triangle(int triangle_id){
        this.triangles_ids.append(triangle_id);
    }

    public void render(){
        int r;
        if(hover){
            r = 5;
            hover = false;
        } else {
            r = 2;
        }
        pushStyle();
        fill(0);
        noStroke();
        ellipse(x,y,r,r);
        popStyle();
    }

    public void move(float x, float y){
        this.x = x;
        this.y = y;
        for(int i=0;i<triangles_ids.size();i++){
            if(triangles.find(triangles_ids.get(i)) != null) triangles.find(triangles_ids.get(i)).update_color();
        }
    }

    public void move(Point p){
        this.move(p.x,p.y);
    }

    public void trace(){
        println(this.to_s());
    }

    public String to_s(){
        return "["+x+","+y+"]";
    }
}
class PointSet {
    ArrayList<Point> points = new ArrayList<Point>();
    int moving_point_id;

    PointSet(){

    }

    PointSet(Object pointset){
        this.points = new ArrayList<Point>(((PointSet)pointset).points);
    }

    public int add(float x, float y) {
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

    public void move(float x, float y){
        if(moving_point_id > -1) {
            Point moving_point = points.get(moving_point_id);
        //     if(this.any_near_except(x,y,moving_point_id)){
                if(moving_point != null) moving_point.move(x,y);
        //     } else {
                // points.get(moving_point_id).move(points.get(this.nearest_except(x,y,moving_point_id)));
        //     }
        }
    }

    // public Boolean any_near_except(float x, float y, int p){
    //     return this.near_except(x,y,p).size() > 0;
    // }

    // public IntList near_except(float x, float y, int p_id){
    //     IntList found_ids = this.near(x,y);
    //     // found_ids.remove(p_id);
    //     return found_ids;
    // }

    // public int nearest_except(float x, float y, int p_id){
    //     return nearest_except(x,y,p_id);
    // }

    public void stop_moving(){
        Point m = points.get(moving_point_id);
        
        // if(this.near(m.x,m.y).size()>0){
            // this.merge(moving_point_id, this.nearest(m));
        // }

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
                if(my_point.near(mouseX, mouseY, 2)) my_point.hover();
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

    public IntList near(float x, float y, float distance){
        IntList found_ids = new IntList();
        for(int i=0;i<points.size();i++){
            Point p = points.get(i);
            if(p == null) continue;
            if(sqrt(pow(p.x-x,2)+pow(p.y-y,2)) < distance) found_ids.append(i);
        }

        return found_ids;
    }

    public int nearest(float x, float y, float distance){
        return this.near(x,y,distance).get(0);
    }

    public int nearest(Point p, int distance){
        return nearest(p.x,p.y,distance);
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

    public Boolean isnt(String state){
        return this.state != state;
    }
}
class Template{
    PImage img = new PImage();
    int[] pixels;

    Template(){
        // selectInput("Selecione uma imagem", "load_image", this);
        // img = loadImage(image_file.getAbsolutePath());
        img = loadImage("/sample.jpg");
        img.loadPixels();
        pixels = img.pixels;
    }

    // void load_image(File image_file){
    // }

    public void render(){
        background(0);
        image(img, 0, 0);
    }

    public int[] pixels(){
        return this.pixels;
    }

    public int height(){
        return img.height;
    }

    public int width(){
        return img.width;
    }

    public int pixel_at(int x,int y){
        return this.pixels[y*img.width+x];
    }
}
class Triangle {
    int p1_id, p2_id, p3_id;
    int l1_id, l2_id, l3_id;
    int id;
    int fill_color;

    Triangle(int p1_id, int p2_id, int p3_id, int id){
        this.p1_id = p1_id;
        this.p2_id = p2_id;
        this.p3_id = p3_id;
        this.id = id;
        for(int i=0;i<this.points().length;i++){
            this.points()[i].add_triangle(id);
        }
    }

    public void drag(float x, float y){
        this.points()[2].x = x;
        this.points()[2].y = y;
        this.update_color();
    }

    public PVector[] area(){
        Point[] p = this.points();
        float min_x = p[0].x;
        float min_y = p[0].y;
        float max_x = p[0].x;
        float max_y = p[0].y;

        for(int i=1;i<p.length;i++){
            if(p[i].x < min_x) min_x = p[i].x; 
            if(p[i].x > max_x) max_x = p[i].x;
            if(p[i].y < min_y) min_y = p[i].y; 
            if(p[i].y > max_y) max_y = p[i].y;
        }

        return new PVector[] { new PVector(min_x, min_y), new PVector(max_x, max_y)};
    }

    public Point[] points() {
        Point[] my_points = {
            points.find(p1_id),
            points.find(p2_id),
            points.find(p3_id),
        };

        return my_points;
    }

    public void update_color(){
        float total = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        PVector[] a = this.area();
        for(int y=0;y<template.height();y++){
            for(int x=0;x<template.width();x++){
                if(x > a[0].x && x < a[1].x && y > a[0].y && y < a[1].y){
                    if(this.test_collision(x,y)){
                        total++;
                        int p = template.pixel_at(x,y);
                        r += red(p);
                        g += green(p);
                        b += blue(p);
                    }
                }
            }
        }
        this.fill_color = color(r/total, g/total, b/total);
    }

    public Boolean test_collision(float x, float y){
        Float[] bc = this.to_baricentric(x,y);
        Boolean l1 = bc[0] >= 0 && bc[0] <= 1;
        Boolean l2 = bc[1] >= 0 && bc[1] <= 1;
        Boolean l3 = bc[2] >= 0 && bc[2] <= 1;
        return l1 && l2 && l3;
    }

    public Float[] to_baricentric(float x, float y){
        Point[] p = this.points();
        float l_1 = (float)(p[1].x*(-y)+p[1].x*p[2].y+p[2].x*y+x*p[1].y-p[2].x*p[1].y-x*p[2].y)/(-p[1].x*p[0].y+p[2].x*p[0].y+p[0].x*p[1].y-p[2].x*p[1].y-p[0].x*p[2].y+p[1].x*p[2].y);
        float l_2 = (float)(-l_1*p[0].x+l_1*p[2].x+x-p[2].x)/(p[1].x-p[2].x);
        float l_3 = 1-l_2-l_1;

        return new Float[] { l_1, l_2, l_3 };
    }

    public void render(){
        pushStyle();
        noStroke();
        fill(this.fill_color);
        beginShape();
        vertex(points()[0].x, points()[0].y);
        vertex(points()[1].x, points()[1].y);
        vertex(points()[2].x, points()[2].y);
        endShape(CLOSE);
        fill(255,0,0,20);
        // rectMode(CORNERS);
        // rect(this.area()[0].x, this.area()[0].y, this.area()[1].x, this.area()[1].y);
        popStyle();
    }
}
class TriangleSet {
    ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    int editing_triangle_id = -1;
    int starter_line_id = -1;

    TriangleSet(){

    }

    TriangleSet(Object triangleset){
        this.triangles = new ArrayList<Triangle>(((TriangleSet)triangleset).triangles);
    }

    public Triangle find(int id){
        return triangles.get(id);
    }

    public void drag_triangle(float x, float y){
        if(editing_triangle_id > -1) triangles.get(editing_triangle_id).drag(x,y);
    }

    public void update_point_id(int old_point_id, int new_point_id){
        for(int i=0;i<triangles.size();i++){
            Triangle t = triangles.get(i);
            if(t.p1_id == old_point_id) triangles.get(i).p1_id = new_point_id;
            if(t.p2_id == old_point_id) triangles.get(i).p2_id = new_point_id;
            if(t.p3_id == old_point_id) triangles.get(i).p3_id = new_point_id;
        }
    }

    public void start_from_line(int line_id, float x, float y){
        Line l = lines.find(line_id);
        int p3_id = points.add(l.closest_point(x,y));
        // points.start_moving(p3_id);
        // points.move(x, y);
        // points.stop_moving();
        triangles.add(new Triangle(l.p1_id, l.p2_id, p3_id, triangles.size()));
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

    public IntList at(float x, float y){
        IntList found_triangles = new IntList();
        for(int i=0;i<triangles.size();i++){
            Triangle t = triangles.get(i);
            if(t != null){
                if(t.test_collision(x,y)){
                    found_triangles.append(i);
                }
            }
        }
        return found_triangles;
    }

    public void render(){
        for(int i=0;i<triangles.size();i++){
            Triangle my_triangle = triangles.get(i);
            if(my_triangle != null) triangles.get(i).render();
        }
    }
}
}
