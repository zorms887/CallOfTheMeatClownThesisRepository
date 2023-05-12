/**
 * Passes in an image and reduces it's palette to the palette contained in the file palette.csv.
 * This also dithers randomly any pixels that are too close to two colors in palette.csv.
 * This can be run from the terminal for testing purposes.
 * <br>
 * Compilation:  javac ReducePaletteRand.java <br>
 * Execution:    java ReducePaletteRand fileName String  <br>
 * <br>
 * Commandline arguments:
 *             args[0] fileName String, the name of the file that's being filtered.
 * <br>
 * @author Nicholas Floyd, 2021
 */


import java.awt.image.BufferedImage;
import static java.lang.Math.abs;
import java.util.Random;

public class ReducePaletteRand extends ReducePalette{
    /**
     * This is the constructor for ReducePaletteRand. It runs the constructor from it's parent class.
     * @param original BufferedImage, the original image that this object edits.
     */
    public ReducePaletteRand(BufferedImage original, boolean do_not_visualize){
        super(original, do_not_visualize);
    }


    /**
     * Returns the int index of the highest scoring color in the color palette.
     * If the highest and second highest scoring colors are within 50 score of each other the second highest scoring color appears based on a percentage of how close the color is to the highest and second highest scoring colors.
     * @param scores double[], The list of scores based on how similar each color is to it.
     * @return current_highest int, the int index of the highest scoring color in the color palette based on how similar it is to the base color.
     */
    protected int findHighest(double[] scores){
        int current_highest = 0;
        int second_highest = 0;
        Random rand = new Random();
        for (int i = 1; i< scores.length; i++){
            if (scores[i] > scores[current_highest]){
                second_highest = current_highest;
                current_highest = i;
            }
        }

        if (abs(scores[current_highest] - scores[second_highest]) < 100)
        {
            //I needed a +1 here because if it returns less than 1 than nextInt will not work. I added 1 to the other side of the equation so it works despite this.
            if (rand.nextInt((int)(scores[current_highest] - scores[second_highest] + 1)) > 51){
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
        ReducePalette m = new ReducePaletteRand(read_in_file(args[0]), false);
    }
}
