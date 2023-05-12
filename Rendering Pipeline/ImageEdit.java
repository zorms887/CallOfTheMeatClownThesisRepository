/**
 * The interface for image editing objects. Contains output_image and display image as those are the only required fields of an image editing object.
 * <br>
 * @author Nicholas Floyd, 2021
 */

import javax.swing.*;
import java.awt.image.BufferedImage;

interface ImageEdit{
    /**
     * Returns the final_image for an image edit object.
     * @return final_imageBufferedImage, the filtered image for the
     */
    BufferedImage output_image();

    /**
     * Displays the image for a image edit object.
     * @param img BufferedImage, the image you'd like to display.
     * @param title String, the title of the image you're displaying.
     * @return window JFrame, A displayed frame of the image.
     */
    JFrame displayImage(BufferedImage img, String title );
}