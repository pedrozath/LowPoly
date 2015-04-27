class Template{
    PImage img = new PImage();
    int[] pixels;

    Template(){
        img = loadImage("../../sample.jpg");
        img.loadPixels();
        pixels = img.pixels;
    }

    void render(){
        image(img, 0, 0);
    }

    int[] pixels(){
        return this.pixels;
    }

    int height(){
        return img.height;
    }

    int width(){
        return img.width;
    }

    int pixel_at(int x,int y){
        return this.pixels[y*img.width+x];
    }
}
