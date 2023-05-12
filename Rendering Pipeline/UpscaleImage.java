import java.awt.image.*;
/**
 * Makes an image passed into it scale up by the resolution multiplier.
 * <br>
 * This file has no testing functionality. Running it from the terminal will do nothing.
 * <br>
 * @author Nicholas Floyd, 2021
 */

public class UpscaleImage extends ImageEditParent{


    /**
     * The constructor of the UpscaleImage object. Runs the resize function with the desired parameters.
     * @param original BufferedImage, the original image that this object edits.
     * @param resolution_multiplier int, the multiplier for the size of the image.
     */
    public UpscaleImage(BufferedImage original, int resolution_multiplier, boolean do_not_visualize){
        if (resolution_multiplier != 1) {
            final_image = resize(original, resolution_multiplier);
        }
        else
        {
            final_image = original;
        }

        if (!do_not_visualize) {
            displayImage(final_image, "Upscaled Image.");
        }
    }

    /**
     * Resizes the original image, multiplying it's size by the resolution_multiplier.
     * @param original BufferedImage, the original image that this edits.
     * @param resolution_multiplier int, the multiplier for the size of the image.
     * @return duplicate BufferedImage, the resized image.
     */
    private BufferedImage resize(BufferedImage original, int resolution_multiplier){
        BufferedImage duplicate = new BufferedImage(original.getWidth()*resolution_multiplier, original.getHeight()*resolution_multiplier, original.getType());
        int rgb;
        for (int row=0; row<original.getHeight(); row += 1) {
            for (int col = 0; col < original.getWidth(); col += 1) {
                rgb = original.getRGB(col, row);
                for (int sRow = 0; sRow < resolution_multiplier; sRow++) {
                    for (int sCol = 0; sCol < resolution_multiplier; sCol++) {
                            duplicate.setRGB((col * resolution_multiplier) + sCol, (row *resolution_multiplier)+sRow, rgb);
                        }
                    }
                }
            }
        return duplicate;
    }
}
