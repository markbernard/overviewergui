package overviewergui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * Settings for running overviewer with.
 * 
 * @author Mark Bernard
 */
public class SettingsPanel extends JPanel implements ActionListener {

    /** ID for serialization */
    private static final long serialVersionUID = -3690877463504526150L;

    private JTextField overviewerExecutable = new JTextField();
    private JTextField minecraftSaveFolder = new JTextField();
    private JTextField mapOutputFolder = new JTextField();
    private MapListModel listModel = new MapListModel("");
    private JList<String> mapList = new JList<String>(listModel);
    private OverviewerGui overviewerGui;

    /**
     * Create the settings panel.
     * 
     * @param overviewerGui Main GUI class 
     */
    public SettingsPanel(final OverviewerGui overviewerGui) {
        this.overviewerGui = overviewerGui;
        setLayout(new BorderLayout());
        JPanel foldersPanel = new JPanel(new BorderLayout());
        add(foldersPanel, BorderLayout.NORTH);
        foldersPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5), 
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        JPanel mainSettingsPanel = new JPanel(new BorderLayout(5, 5));
        foldersPanel.add(mainSettingsPanel, BorderLayout.NORTH);
        JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainSettingsPanel.add(labelPanel, BorderLayout.WEST);
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainSettingsPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainSettingsPanel.add(buttonPanel, BorderLayout.EAST);
        
        labelPanel.add(new JLabel("Overviewer Executable"));
        labelPanel.add(new JLabel("Minecraft Save Folder"));
        labelPanel.add(new JLabel("Map Output Folder"));
        
        inputPanel.add(overviewerExecutable);
        inputPanel.add(minecraftSaveFolder);
        inputPanel.add(mapOutputFolder);
        
        buttonPanel.add(new JButton(new FileBrowse(overviewerExecutable, this, false, null)));
        buttonPanel.add(new JButton(new FileBrowse(minecraftSaveFolder, this, true, null)));
        buttonPanel.add(new JButton(new FileBrowse(mapOutputFolder, this, true, new Runnable() {
            
            @Override
            public void run() {
                listModel.setFolder(minecraftSaveFolder.getText());
            }
        })));
        minecraftSaveFolder.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {
                listModel.setFolder(minecraftSaveFolder.getText());
            }
            
            @Override
            public void focusGained(FocusEvent e) {}
        });

        JPanel mapListPanel = new JPanel(new BorderLayout());
        mapListPanel.add(new JScrollPane(mapList));
        mapListPanel.setBorder(BorderFactory.createTitledBorder("Minecraft Maps"));
        add(mapListPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        add(panel, BorderLayout.SOUTH);
        JButton runButton = new JButton("Run Overviewer");
        panel.add(runButton);
        runButton.addActionListener(this);
        JButton exitButton = new JButton("Exit");
        panel.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                overviewerGui.exit();
            }
        });
    }

    /**
     * @return the overviewerExecutable
     */
    public String getOverviewerExecutable() {
        return overviewerExecutable.getText();
    }

    /**
     * @param overviewerExecutable the overviewerExecutable to set
     */
    public void setOverviewerExecutable(String overviewerExecutable) {
        this.overviewerExecutable.setText(overviewerExecutable);
    }

    /**
     * @return the minecraftSaveFolder
     */
    public String getMinecraftSaveFolder() {
        return minecraftSaveFolder.getText();
    }

    /**
     * @param minecraftSaveFolder the minecraftSaveFolder to set
     */
    public void setMinecraftSaveFolder(String minecraftSaveFolder) {
        this.minecraftSaveFolder.setText(minecraftSaveFolder);
        listModel.setFolder(minecraftSaveFolder);
    }

    /**
     * @return the mapOutputFolder
     */
    public String getMapOutputFolder() {
        return mapOutputFolder.getText();
    }

    /**
     * @param mapOutputFolder the mapOutputFolder to set
     */
    public void setMapOutputFolder(String mapOutputFolder) {
        this.mapOutputFolder.setText(mapOutputFolder);
    }

    /**
     * Get a list of the maps selected.
     * 
     * @return A list of selected maps or an empty list if none are selected.
     */
    public List<String> getSelectedMaps() {
        return mapList.getSelectedValuesList();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> maps = mapList.getSelectedValuesList();
        for(String map:maps) {
            this.overviewerGui.addOverviewer(getMinecraftSaveFolder(), map, getMapOutputFolder(), getOverviewerExecutable());
        }
    }
}

class FileBrowse extends AbstractAction {
    private static final long serialVersionUID = -8761058272307425991L;
    private JTextField targetField;
    private JComponent parentComponent;
    private Runnable callback;
    private boolean folderOnly;

    public FileBrowse(JTextField targetField, JComponent parentComponent, boolean folderOnly, Runnable callback) {
        putValue(NAME, "Browse...");
        this.targetField = targetField;
                this.parentComponent = parentComponent;
        this.folderOnly = folderOnly;
        this.callback = callback;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String startingFolder = targetField.getText();
        if("".equals(startingFolder)) {
            startingFolder = System.getProperty("user.home");
        }
        JFileChooser fileChooser = new JFileChooser(startingFolder);
        if(folderOnly) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        if(fileChooser.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
            targetField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
        if(callback != null) {
            callback.run();
        }
    }
    
}