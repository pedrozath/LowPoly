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

    public void drag(int x, int y){
        this.points()[2].x = x;
        this.points()[2].y = y;
        this.update_color();
    }

    PVector[] area(){
        Point[] p = this.points();
        int min_x = p[0].x;
        int min_y = p[0].y;
        int max_x = p[0].x;
        int max_y = p[0].y;

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

    void update_color(){
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

    public Boolean test_collision(int x,int y){
        Float[] bc = this.to_baricentric(x,y);
        Boolean l1 = bc[0] >= 0 && bc[0] <= 1;
        Boolean l2 = bc[1] >= 0 && bc[1] <= 1;
        Boolean l3 = bc[2] >= 0 && bc[2] <= 1;
        return l1 && l2 && l3;
    }

    public Float[] to_baricentric(int x, int y){
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
