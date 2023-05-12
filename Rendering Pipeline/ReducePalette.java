/**
 * Passes in an image and reduces it's palette to the palette contained in the file palette.csv.
 * This can be run from the terminal for testing purposes.
 * <br>
 * Compilation:  javac ReducePalette.java <br>
 * Execution:    java ReducePalette fileName String  <br>
 * <br>
 * Commandline arguments:
 *             args[0] fileName String, the name of the file that's being filtered.
 * <br>
 * @author Nicholas Floyd, 2021
 */


import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import static java.lang.Math.abs;


public class ReducePalette extends ImageEditParent {
    protected Color[] color_palette;
    protected BufferedImage original_image;
    protected Color transparent = new Color(0, 0, 0, 0);
    protected int transARGB = transparent.getRGB();


    /**
     * The constructor of the ReducePalette object. Runs the paletteToStrings function with the desired parameters, and then the stringsToColor function, and finally the ScoreImage function, with the color palette which was read with the last two functions.
     * @param original BufferedImage, the original image that this object edits.
     */
    public ReducePalette(BufferedImage original, boolean do_not_visualize){
        original_image = original;
        //original_image = convertType(original_image);
        String[][] string_palette = paletteToStrings();
        color_palette = stringsToColor(string_palette);


        final_image = scoreImage();

        if (!do_not_visualize){
            displayImage(final_image, "ReducedImage");
        }
    }


    protected BufferedImage convertType(BufferedImage original){
        BufferedImage convertedImg = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        convertedImg.getGraphics().drawImage(original, 0, 0, null);
        return convertedImg;
    }
    /**
     * Turns the string values from the CSV file into an array of colors.
     * @param string_palette String[][], the strings passed in which will turn into colors.
     * @return reconstructed_palette Color[], the colors
     */
    protected Color[] stringsToColor(String[][] string_palette){
        Color[] reconstructed_palette = new Color[string_palette.length];
        int current_red, current_green, current_blue;
        for(int i = 0; i < string_palette.length; i++){
            current_red = Integer.parseInt(string_palette[i][0]);
            current_green = Integer.parseInt(string_palette[i][1]);
            current_blue = Integer.parseInt(string_palette[i][2]);

            reconstructed_palette[i] = new Color(current_red, current_green, current_blue);

        }
        return(reconstructed_palette);

    }

    /**
     * Turns the palette.csv file into a String[][] representing each of the colors.
     * @return The String[][] that the palette is turned into.
     */
    protected String[][] paletteToStrings(){
        // gets the number of lines for string_palette_values.
        int line_num_init = 0;
        String line_init;
        try (BufferedReader bb = new BufferedReader(new FileReader("palette.csv"))) {
            while ((line_init = bb.readLine()) != null) {
                line_num_init += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //figure out how to convert this to arraylist.
        String[][] string_palette_values = new String[line_num_init][3];
        //code taken and heavily modified from https://stackoverflow.com/questions/59317619/how-can-i-convert-csv-file-into-an-integer-array-in-java
        try (BufferedReader br = new BufferedReader(new FileReader("palette.csv"))) {
            String line;
            int line_num = 0;
            while ((line = br.readLine()) != null) {
                //gets rid of the \n at the end.
                String[] values = line.split("\n");
                //chops the values up by comma.
                String[] value_separated = values[0].split(",");
                //inserts each
                for(int i = 0; i < value_separated.length; i++) {
                    string_palette_values[line_num][i] = value_separated[i];
                    //System.out.println("this is done " + line_num + " times");
                }
                line_num += 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //end of borrow.
        return string_palette_values;
    }

    /**
     * Returns the int index of the highest scoring color in the color palette.
     * @param scores double[], The list of scores based on how similar each color is to it.
     * @return current_highest int, the int index of the highest scoring color in the color palette based on how similar it is to the base color.
     */
    protected int findHighest(double[] scores){
        int current_highest = 0;
        //int second_highest = 0;
        for (int i = 1; i< scores.length; i++){
            if (scores[i] > scores[current_highest]){
                current_highest = i;
            }
        }
        return current_highest;
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

                int highest_color = findHighest(scores);


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
     * Prints out the String palette to make sure it's working.
     * @param string_palette String[][], The current palette constructed of strings.
     */
    protected void testColors(String[][] string_palette){
        for(int i = 0; i < string_palette.length; i++) {
            for(int b = 0; b < string_palette[i].length; b++){
                System.out.println(string_palette[i][b]);
            }
        }
    }

    /**
     * reads in the BufferedImage file.
     * @param fileName String, the file name as a string.
     * @return original BufferedImage, the image that the file contains.
     */
    protected static BufferedImage read_in_file(String fileName){
        BufferedImage original = null;
        try {
            original = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
        }
        return original;
    }

    /**
     * Passes in the commandline arguments and runs ReducePalette, this is only for testing.
     * @param args String[], The arguments passed in from the commandline.
     */
    public static void main(String[] args) {
            ReducePalette m = new ReducePalette(read_in_file(args[0]), false);

    }
}
