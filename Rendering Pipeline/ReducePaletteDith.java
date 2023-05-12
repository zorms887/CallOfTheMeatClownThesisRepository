/**
 * Passes in an image and reduces it's palette to the palette contained in the file palette.csv.
 * This also dithers in a crosshatch pattern any pixels that are too close to two colors in palette.csv.
 * This can be run from the terminal for testing purposes.
 * <br>
 * Compilation:  javac ReducePaletteDith.java <br>
 * Execution:    java ReducePaletteDith fileName String  <br>
 * <br>
 * Commandline arguments:
 *             args[0] fileName String, the name of the file that's being filtered.
 * <br>
 * @author Nicholas Floyd, 2021
 */


import java.awt.*;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;

public class ReducePaletteDith extends ReducePalette{
    /**
     * This is the constructor for ReducePaletteDith. It runs the constructor from it's parent class.
     * @param original BufferedImage, the original image that this object edits.
     */
    public ReducePaletteDith(BufferedImage original, boolean do_not_visualize){
        super(original, do_not_visualize);
    }


    /**
     * Gets a score based on how similar each pixel of the image is to the color palette and produces an image based on the highest scoring colors of that
     * @return duplicate BufferedImage, The image after the palette has been reduced.
     */
    protected BufferedImage scoreImage(){
        //Color[][] color_grid = new Color[original_image.getHeight()][original_image.getWidth()];
        BufferedImage duplicate = new BufferedImage(original_image.getWidth(), original_image.getHeight(), original_image.getType());
        int rgb, current_red, current_green, current_blue;
        for (int row=0; row<original_image.getHeight(); row++) {
            for (int col = 0; col < original_image.getWidth(); col++) {
                //gets each color in the current pixel
                rgb = original_image.getRGB(col, row);
                Color colorIn = new Color(rgb, true);
                current_red = colorIn.getRed();
                current_green = colorIn.getGreen();
                current_blue = colorIn.getBlue();

                double[] scores = new double[color_palette.length];

                //actually does the scoring.
                for(int i = 0; i < color_palette.length; i++ ){
                    double score;
                    double red_score;
                    double green_score;
                    double blue_score;

                    red_score = 255 - abs(color_palette[i].getRed() - current_red);
                    green_score = 255 - abs(color_palette[i].getGreen() - current_green);
                    blue_score = 255 - abs(color_palette[i].getBlue() - current_blue);

                    score = red_score + green_score + blue_score;

                    scores[i] = score;

                }

                int highest_color = findHighest(scores, row, col);

                if (colorIn.getAlpha() > 127) {
                    duplicate.setRGB(col, row, color_palette[highest_color].getRGB());
                }
                else
                {
                    duplicate.setRGB(col, row, transARGB);
                }
            }
        }
        return duplicate;
    }
    /**
     * Returns the int index of the highest scoring color in the color palette.
     * If the highest and second highest scoring colors are within 50 score of each other the second highest scoring color appears on every even pixel.
     * @param scores double[], The list of scores based on how similar each color is to it.
     * @return current_highest int, the int index of the highest scoring color in the color palette based on how similar it is to the base color.
     */
    protected int findHighest(double[] scores, int row, int col){
        int current_highest = 0;
        int second_highest = 0;
        for (int i = 1; i< scores.length; i++){
            if (scores[i] > scores[current_highest]){
                second_highest = current_highest;
                current_highest = i;
            }
        }

        if (abs(scores[current_highest] - scores[second_highest]) < 50)
        {
            if ((row + col) % 2 == 0){
                current_highest = second_highest;
            }

        }
        return current_highest;
    }
    /**
     * Passes in the commandline arguments and runs ReducePaletteDith, this is only for testing.
     * @param args String[], The arguments passed in from the commandline.
     *             Args[0], The image file name.
     */
    public static void main(String[] args) {
        ReducePalette m = new ReducePaletteDith(read_in_file(args[0]), false);
    }
}
