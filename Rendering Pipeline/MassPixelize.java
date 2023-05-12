import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class MassPixelize {

    int image_number = 0;
    public MassPixelize(String directory, int resolution_coefficient, String filterType, boolean do_not_upscale, boolean chromakey, boolean do_not_save){

        String[] fileNames = getFileNames(directory, true);
        String[] outputFileNames = getFileNames(directory, false);
        BufferedImage[] filteredImages = generateImages(fileNames, resolution_coefficient, filterType, do_not_upscale, chromakey);



        massSaveFile(outputFileNames, filteredImages);

    }

    private BufferedImage[] generateImages(String[] fileNames, int resolution_coefficient, String filterType, boolean do_not_upscale, boolean chromakey){
        BufferedImage[] image_list = new BufferedImage[fileNames.length];

        //applies the filter based on file names
        for (int i = 0; i < fileNames.length; i++){
            Pixelize filter = new Pixelize(fileNames[i],  resolution_coefficient, filterType, do_not_upscale, chromakey, true, true);
            BufferedImage filteredImage = filter.output_image();
            image_list[i] = filteredImage;
        }

        return image_list;

    }

    //gets the names of all the files as strings, not sure if I need to append them with anything yet to make sure the Pixelize program finds what it needs to.
    private String[] getFileNames(String directory, boolean is_input){
        //initializing the file names
        File folder = new File(directory);
        File[] fileList = folder.listFiles();
        String[] fileNames = new String[fileList.length];
        //changes the directory if it's being used for output
        if (!is_input){
            directory = "output_images";
        }
        //saves each file as it's directory in a list of strings.
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {
                fileNames[i] = directory + "/" + fileList[i].getName();
                System.out.println(fileNames[i]);
            }
        }

        return fileNames;
    }

    private void massSaveFile(String[] inFileName, BufferedImage[] filtered_image){
        for (int i = 0; i < inFileName.length; i++) {
            int period = inFileName[i].indexOf(".");
            String fileExtension = inFileName[i].substring(period + 1);
            //String copyFileName = inFileName[i].substring(0, period) + "_" + image_number + "." + fileExtension;
            String copyFileName = inFileName[i].substring(0, period) + "." + fileExtension;
            try {
                File copiedFile = new File(copyFileName);
                ImageIO.write(filtered_image[i], fileExtension, copiedFile);
            } catch (IOException e) {
                System.err.println(String.format("%s%n", e));
            }
        }
    }

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
        if (args.length > 1) {
            resolution_coefficient = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            filterType = args[2];
        }
        if (args.length > 3) {
            do_not_upscale = Boolean.parseBoolean(args[3]);
        }
        if (args.length > 4) {
            chromakey = Boolean.parseBoolean(args[4]);
        }
        //initializing the code
        MassPixelize finished_image = new MassPixelize(fileName, resolution_coefficient, filterType, do_not_upscale, chromakey, true);
    }
}
