package overviewergui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Custom tab title to enable a close box.
 * 
 * @author Mark Bernard
 *
 */
public class TabTitleComponent extends JPanel {
    private static final long serialVersionUID = -3479738282862739743L;

    /**
     * Create a tab closing component with the provided title.
     *  
     * @param title
     * @param parent 
     */
    public TabTitleComponent(final String title, final OverviewerGui parent) {
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setOpaque(false);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);
        ImageButton closeButton = new ImageButton();
//        closeButton.setBorder(BorderFactory.createEmptyBorder());
//        closeButton.setBorderPainted(false);
//        closeButton.setIcon(CLOSE_BUTTON);
//        closeButton.setPressedIcon(CLOSE_BUTTON_PRESSED);
//        closeButton.setMargin(new Insets(2, 2, 2, 2));
        add(closeButton, BorderLayout.EAST);
        closeButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.stopOverviewerRun(title);
            }
        });
    }
}
