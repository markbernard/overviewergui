package overviewergui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Custom image button since the regular button was drawing a border around the images and I couldn't 
 * get rid of it.
 * 
 * @author Mark Bernard
 */
public class ImageButton extends JComponent implements MouseListener {
    private static final long serialVersionUID = 5489428043969765304L;
    private BufferedImage closeButton;
    private BufferedImage closeButtonBressed;
    private Dimension size;
    private boolean buttonPressed;
    private boolean mouseOver;
    private List<ActionListener> listeners;
    
    /**
     * Load button images
     */
    public ImageButton() {
        listeners = new ArrayList<ActionListener>();
        try {
            closeButton = ImageIO.read(TabTitleComponent.class.getClassLoader().getResource("images/exit.png"));
            closeButtonBressed = ImageIO.read(TabTitleComponent.class.getClassLoader().getResource("images/exit_pressed.png"));
            size = new Dimension(closeButton.getWidth(), closeButton.getHeight());
            buttonPressed = false;
            mouseOver = false;
            addMouseListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        buttonPressed = true;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttonPressed = false;
        if(mouseOver) {
            notifyListeners();
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    /**
     * Allow objects to register for events.
     * 
     * @param listener
     */
    public void addActionListener(ActionListener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Allow objects to unregister for events.
     * 
     * @param listener
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for(ActionListener listener:listeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ImageButton"));
        }
    }
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        if(buttonPressed && mouseOver) {
            g.drawImage(closeButtonBressed, 0, 0, this);
        }
        else {
            g.drawImage(closeButton, 0, 0, this);
        }
    }
}
