/**
 * The abstract parent class for every image editing class in this program. This only has an output for the image and a display function.
 * <br>
 * @author Nicholas Floyd, 2021
 */

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class ImageEditParent implements ImageEdit{
    protected BufferedImage final_image;

    /**
     * This is the constructor for this abstract class. There isn't anything here.
     */
    public ImageEditParent(){ }
    /**
     * Outputs the image as a BufferedImage object.
     * @return final_image BufferedImage, the final image of the image editor.
     */
    public BufferedImage output_image(){
        return final_image;
    }
    /**
     * Displays the image with whatever title specified.
     * @param img BufferedImage, the image you'd like to display.
     * @param title String, the title of the image you're displaying.
     * @return window JFrame, A displayed frame of the image.
     */
    public JFrame displayImage(BufferedImage img, String title ){
        // Create the graphics window
        JFrame window = new JFrame();
        window.setTitle( title );
        window.setSize( img.getWidth()+20, img.getHeight()+40 );

        // Center the image in the graphics window
        ImageIcon icon = new ImageIcon( img );
        JLabel label = new JLabel( icon );
        window.add( label );

        // Make the graphics window visible until the user closes it (which also ends the program)
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        // Return a reference to the display window, so that we can manipulate it in the future, if we like.
        return window;
    }
}

