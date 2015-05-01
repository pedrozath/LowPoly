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

    void render(){
        background(0);
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