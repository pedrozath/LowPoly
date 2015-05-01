class CameraControl{
    float x = width/2;
    float y = height/2;
    float z = width;

    float upX = 0;
    float upY = 1;
    float upZ = 0;

    void apply(){
        camera(x, y, z, x, y, 0, upX, upY, upZ);
    }

    void zoom(float n){
        this.z += n*1;
        println(z);
    }

    void move(float x, float y){
        this.x += x/1000;
        this.y += y/1000;
    }
}