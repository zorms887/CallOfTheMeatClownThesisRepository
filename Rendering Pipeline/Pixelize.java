/**
 * Passes in an image that will go through downscaling, palette reduction, and finally upscaling again.
 * You can pick what type of dithering will be applied with the third argument, and you select the color palette with the palette.csv file contained in the same folder as this program.
 * <br>
 * Compilation:  javac Pixelize.java <br>
 * Execution:    java Pixelize fileName String, resolution_coefficient int, filterType String    <br>
 * <br>
 * commandline arguments:
 *             args[0] fileName String, the name of the file that's being filtered.
 *             args[1] resolution_coefficient int, the amount of downscaling and upscaling that will happen to the image.
 *             args[2] filterType String, the type of filtering that will be applied to the image.
 *             "none" = no dithering.
 *             "dither" = crosshatch dithering.
 *             "random" = randomized dithering.
 * <br>
 * @author Nicholas Floyd, 2021
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Pixelize {
    private BufferedImage original = null;
    private BufferedImage finalImage = null;

    /**
     * Main filtering process. Passes the image through each image filter one by one until it is fully pixelized.
     * @param fileName String, The file name of the image in a string.
     * @param resolution_coefficient int, The coefficient by which the image's resolution will be reduced by.
     * @param filterType String, The type of dithering that is desired in the final image.
     * @param do_not_upscale boolean, whether or not the image should be upscaled back to it's original resolution after being pixelized
     * @param chromakey boolean, whether or not the image should be chromakeyed after the resolution is reduced.
     */
    public Pixelize(String fileName, int resolution_coefficient, String filterType, boolean do_not_upscale, boolean chromakey, boolean do_not_save, boolean do_not_visualize){
        //reading in file
        if (read_in_file(fileName)){
            //downscale code
            DownscaleImage downscale = new DownscaleImage(original, resolution_coefficient, do_not_visualize);
            BufferedImage downscaled_image = downscale.output_image();

            //chromakey code
            //this is so if the chromakey code is false it'll still pass through the previous step
            BufferedImage gs_image = downscaled_image;

            //actually if chromakey is true.
            if (chromakey){
                GreenScreen greenScreen = new GreenScreen(downscaled_image, do_not_visualize);
                gs_image = greenScreen.output_image();
            }

            //reduce palette code
            ReducePalette reduce;

            if (filterType.equals("dither")){
                reduce = new ReducePaletteDith(gs_image, do_not_visualize);
            } else if(filterType.equals("random")){
                reduce = new ReducePaletteRand(gs_image, do_not_visualize);
            } else{
                reduce = new ReducePalette(gs_image, do_not_visualize);
            }

            BufferedImage reduced_image = reduce.output_image();

            //upscale code
            UpscaleImage upscale = new UpscaleImage(reduced_image, resolution_coefficient, do_not_visualize);

            BufferedImage upscaled_image;

            if (!do_not_upscale) {
                upscaled_image = upscale.output_image();
            }
            else
            {
                upscaled_image = reduced_image;
            }

            if (!do_not_save) {
                saveFile(fileName, upscaled_image);
            }

            finalImage = upscaled_image;
        }


    }

    /**
     * reads in the file and makes sure it can read it.
     * @param fileName the name of the file you want to read in
     * @return can_read boolean, if this is successful in reading in the file to original this will be true.
     */
    private boolean read_in_file(String fileName){
        boolean can_read = true;
        try {
            original = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
            can_read = false;
        }
        return can_read;
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

    public BufferedImage output_image(){ return finalImage;}

    /**
     * Passes in the commandline arguments, produces error messages if not passed in, and runs the Pixelize program using those commandline arguments.
     * @param args String[], The arguments passed in from the commandline.
     *             args[0] fileName String, the name of the file that's being filtered.
     *             args[1] resolution_coefficient int, the amount of downscaling and upscaling that will happen to the image.
     *             args[2] filterType String, the type of filtering that will be applied to the image.
     *             "none" = no dithering.
     *             "dither" = crosshatch dithering.
     *             "random" = randomized dithering.
     */
    public static void main(String[] args) {

        // If the user misses a commandline argument, show them a helpful usage statement
        String usageStatement = "USAGE: java ImageFilter filePath"
                + "\nFor example:"
                + "\n\tjava ImageFilter image.png"
                + "The image's file extension must be PNG, JPEG, or JPG.";

        // Parse commandline arguments
        String fileName = "none";
        String filterType = "";
        int resolution_coefficient = 2;
        boolean do_not_upscale = false;
        boolean do_not_visualize = false;
        boolean chromakey = false;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            System.out.println(usageStatement);
            return;
        }
        if (args.length > 1){
            resolution_coefficient = Integer.parseInt(args[1]);
        }
        if (args.length > 2){
            filterType = args[2];
        }
        if (args.length > 3){
            do_not_upscale = Boolean.parseBoolean(args[3]);
        }
        if (args.length > 4){
            chromakey = Boolean.parseBoolean(args[4]);
        }
        //initializing the code.
        Pixelize finished_image = new Pixelize(fileName, resolution_coefficient, filterType, do_not_upscale, chromakey, false, false);

    }

}
