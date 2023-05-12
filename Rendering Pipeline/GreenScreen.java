import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import static java.lang.Math.abs;

public class GreenScreen extends ImageEditParent{

    protected Color removed_color = new Color(0,255, 0, 255);
    protected Color transparent = new Color(0, 0, 0, 0);
    protected int transARGB = transparent.getRGB();
    protected BufferedImage original_image;

    public GreenScreen(BufferedImage original, boolean do_not_visualize){
        original_image = original;

        original_image = convertType(original);

        final_image = removeColor();

        final_image = convertType(final_image);

        if (!do_not_visualize) {
            displayImage(final_image, "Chromakeyed image");

            //only happens if this is run directly from the console
            saveFile("green_screened_image.png", final_image);
        }
        //test code
        /*ReducePalette reducePalette = new ReducePalette(final_image);
        final_image = reducePalette.output_image();*/



    }

    protected BufferedImage convertType(BufferedImage original){
        BufferedImage convertedImg = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        convertedImg.getGraphics().drawImage(original, 0, 0, null);
        return convertedImg;
    }

    /*
    This is the chromakey function, removes the specified color which is universally green so far.


    TODO: support other colors
     */
    protected BufferedImage removeColor() {
        Color[][] color_grid = new Color[original_image.getHeight()][original_image.getWidth()];
        BufferedImage duplicate = new BufferedImage(original_image.getWidth(), original_image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        int rgb, current_red, current_green, current_blue;
        for (int row = 0; row < original_image.getHeight(); row++) {
            for (int col = 0; col < original_image.getWidth(); col++) {
                //gets each color in the current pixel
                rgb = original_image.getRGB(col, row);
                Color colorIn = new Color(rgb);
                current_red = colorIn.getRed();
                current_green = colorIn.getGreen();
                current_blue = colorIn.getBlue();

                //scores them based on how similar they are to the chromakeyed color.
                double score;
                double red_score;
                double green_score;
                double blue_score;

                red_score = 255 - abs(current_red);
                green_score = 255 - abs(removed_color.getGreen() - current_green);
                blue_score = 255 - abs(current_blue);

                score = red_score + green_score + blue_score;

                score = score/3;

                //System.out.println(red_score);
                //System.out.println(blue_score);
                //System.out.println(green_score);
                //System.out.println("");
                //sets transparency if it is close enough to color, but has 2 settings. Make partially transparent, and make fully transparent.
                //will set full transparency if the color is below 150 in the simmilarity score, will set partial transparency based on how green it was if the color is below 200 but above 150
                if (score < 145) {
                    duplicate.setRGB(col, row, rgb);
                } /*else if (score < 200) {
                    int alpha_score = (int)green_score - ((int)red_score + (int)blue_score)*2;
                    if (alpha_score <= 0){alpha_score = 0;}
                    if (alpha_score >= 255){alpha_score = 255;}

                    //System.out.println("");
                    //System.out.println(alpha_score);
                    Color softChromaColor = new Color(current_red, abs(current_green - (int)green_score), current_blue, 255 - (alpha_score));
                    int softChromaRGB = softChromaColor.getRGB();
                    duplicate.setRGB(col, row, softChromaRGB);
                } */else {
                        duplicate.setRGB(col, row, transARGB);
                    }
                }

            }
        return duplicate;
    }

    /**
     * Saves the buffered image passed in as a file.
     * @param inFileName String, The name of the file passed in
     * @param filtered_image Buffered Image, The image passed in to be saved.
     */
    private void saveFile(String inFileName, BufferedImage filtered_image){
        int period = inFileName.indexOf( "." );
        String fileExtension = inFileName.substring( period+1 );
        String copyFileName = inFileName.substring( 0, period ) + "_pixelized." + fileExtension;
        try {
            File copiedFile = new File( copyFileName );
            ImageIO.write( filtered_image, fileExtension,  copiedFile );
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
        }
    }

    protected static BufferedImage read_in_file(String fileName){
        BufferedImage original = null;
        try {
            original = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
        }
        return original;
    }

    public static void main(String[] args) {
        GreenScreen m = new GreenScreen(read_in_file(args[0]), false);

    }
}
