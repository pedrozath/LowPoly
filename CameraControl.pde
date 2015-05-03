class CameraControl{
    float x = 0;
    float y = 0;
    float zoom = 1;

    // float upX = 0;
    // float upY = 1;
    // float upZ = 0;

    void apply(){
        // camera(x, y, z, x, y, 0, upX, upY, upZ);
        translate(x,y);
        scale(zoom);
    }

    void zoom(float n){
        // center(this.mouse());
        zoom -= n/100;
        // println(z);
    }

    void center(float x, float y){
        this.move((x/2)-(width/2), (y/2)-(height/2));
    }

    void center(PVector p){
        this.center(p.x, p.y);
    }

    void move(float x, float y){
        this.x -= x;
        this.y -= y;
    }

    PVector mouse(){
        return new PVector((mouseX-x)/zoom,(mouseY-y)/zoom);
    }
}