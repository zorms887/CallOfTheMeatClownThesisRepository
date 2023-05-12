/**
 * Passes in an image and divides it's resolution by the resolution_coefficient. Can be run directly from terminal for testing purposes.
 * <br>
 * Compilation:  javac DownscaleImage.java <br>
 * Execution:    java DownscaleImage fileName String, resolution_coefficient int   <br>
 * <br>
 * Commandline arguments:
 *             args[0] fileName String, the name of the file that's being filtered.
 *             args[1] resolution_coefficient int, the amount of downscaling that will happen to the image.
 * <br>
 * @author Nicholas Floyd, 2021
 */


import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;


public class DownscaleImage extends ImageEditParent{
    private BufferedImage original_image;
    /**
     * The constructor of the DownscaleImage object. Runs the lowerResolution function with the desired parameters.
     * @param original BufferedImage, the original image that this object edits.
     * @param resolution_coefficient int, the coefficient for the size of the image.
     */
    public DownscaleImage(BufferedImage original, int resolution_coefficient, boolean do_not_visualize){
        original_image = original;

        if (resolution_coefficient != 1) {
            final_image = lowerResolution(original_image, resolution_coefficient);
        }else{
            final_image = original_image;
        }

        if (!do_not_visualize) {
            displayImage(final_image, "Lowered resolution");

            //this only runs if this is run from the console
            saveFile("downscaled_image.png",final_image);
        }
    }
    /**
     * Downscales the original image, dividing it's size by the resolution_coefficient by creating an image out of the mean values of each pixel in the origional image.
     * @param original BufferedImage, the original image that this edits.
     * @param resolution_coefficient int, the coefficient for the size of the image.
     * @return duplicate BufferedImage, the resized image.
     */
    private BufferedImage lowerResolution( BufferedImage original, int resolution_coefficient){
        int residual_height = original.getHeight()%resolution_coefficient;
        int residual_width = original.getWidth()%resolution_coefficient;
        // The output image begins as a blank image that is the same size and type as the original
        BufferedImage duplicate = new BufferedImage((original.getWidth()/resolution_coefficient) + residual_width, (original.getHeight()/resolution_coefficient) + residual_height, original.getType());


        // Iterate over the original, copying each pixel's RGB color over to the new image (the copy)
        int ww = resolution_coefficient;
        int wh = resolution_coefficient;
        int rgb;
        int[][] redArr = new int[wh][ww];
        int[][] greenArr = new int[wh][ww];
        int[][] blueArr = new int[wh][ww];

        Color colorIn, colorOut;
        for (int row=0; row<original.getHeight(); row += resolution_coefficient) {
            for (int col=0; col < original.getWidth(); col += resolution_coefficient) {
                for (int sRow = 0; sRow < wh; sRow++) {
                    for (int sCol = 0; sCol < ww; sCol++) {
                        try{
                            rgb = original.getRGB(col - ww / 2 + sCol, row - wh / 2 + sRow);
                            colorIn = new Color(rgb);
                            redArr[sRow][sCol] = colorIn.getRed();
                            greenArr[sRow][sCol] = colorIn.getGreen();
                            blueArr[sRow][sCol] = colorIn.getBlue();
                        }
                        catch(Exception e){
                            redArr[sRow][sCol] = 256;
                            greenArr[sRow][sCol] = 256;
                            blueArr[sRow][sCol] = 256;
                        }
                    }
                }
                int meanRed = meanResult(redArr,ww,wh);
                int meanGreen = meanResult(greenArr,ww,wh);
                int meanBlue = meanResult(blueArr,ww,wh);
                colorOut = new Color(meanRed, meanGreen, meanBlue);
                //System.out.println( String.format("original[%d][%d] = %d, %d, %d", row, col, red, green, blue) );
                duplicate.setRGB(col/resolution_coefficient, row/resolution_coefficient, colorOut.getRGB());
            }
        }
        // Return a reference to the shiny new copy of the input image
        return duplicate;
    }

    /**
     * Produces the mean of an int[][] which is passed in.
     * @param mean_list int[][], the list of ints you want to get the mean of.
     * @param ww int, the width of the mean_list.
     * @param wh int, the height of the mean_list.
     * @return full_number int, the mean of the mean_list.
     */
    private int meanResult (int[][] mean_list, int ww, int wh){
        int full_number = 0;
        for(int i = 0; i < ww; i += 1){
            for(int b = 0; b < wh; b += 1) {
                if (mean_list[i][b] != 256) {
                    full_number += mean_list[i][b];
                }
            }
        }
        full_number = full_number / (ww * wh);
        return full_number;
    }
    /**
     * Reads in the BufferedImage file.
     * @param fileName String, the file name as a string.
     * @return original BufferedImage, the image that the file contains.
     */
    private static BufferedImage read_in_file(String fileName){
        BufferedImage original = null;
        try {
            original = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
        }
        return original;
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
    /**
     * Passes in the commandline arguments and runs DownscaleImage, this is only for testing.
     * @param args String[], The arguments passed in from the commandline.
     *             Args[0], The image file name.
     *             Args[1], The coefficient by which this image's resolution will be divided.
     */
    public static void main(String[] args) {
        int resolution_coefficient = 2;
        if (args.length > 1){
            resolution_coefficient = Integer.parseInt(args[1]);
        }
        DownscaleImage m = new DownscaleImage(read_in_file(args[0]), resolution_coefficient, false);


    }
}
