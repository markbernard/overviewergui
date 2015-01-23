package overviewergui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * Main GUI class for Overviewer
 * 
 * @author Mark Bernard
 */
public class OverviewerGui extends JPanel implements WindowListener {
    /** ID for serialization */
    private static final long serialVersionUID = 3331291564814065006L;
    private static final String WINDOW_X = "window.x";
    private static final String WINDOW_Y = "window.y";
    private static final String WINDOW_WIDTH = "window.width";
    private static final String WINDOW_HEIGHT = "window.height";
    private static final String WINDOW_MAXIMIZED = "window.maximized";
    private static final String MINECRAFT_SAVE_DATA = "minecraft.save.data";
    private static final String MINECRAFT_MAP_OUTPUT = "minecraft.map.output";
    private static final String OVERVIEWER_EXECUTABLE = "overviewer.executable";
    
    private JFrame parent;
    private SettingsPanel settingsPanel;
    private JTabbedPane tabPane;
    private Map<String, OverviewerOutputPanel> activePanels;

    /**
     * Set up GUI.
     * 
     * @param parent 
     */
    public OverviewerGui(JFrame parent) {
        this.parent = parent;
        parent.addWindowListener(this);
        setLayout(new BorderLayout());
        tabPane = new JTabbedPane();
        add(tabPane, BorderLayout.CENTER);
        settingsPanel = new SettingsPanel(this);
        tabPane.addTab("Settings", settingsPanel);
        activePanels = new HashMap<>();
        
        loadPrefs();
    }
    
    /**
     * Add an Overviewer to the display.
     * 
     * @param minecraftFolder 
     * @param mapName
     * @param mapOutputFolder 
     * @param overviewerExecutable 
     */
    public void addOverviewer(String minecraftFolder, String mapName, String mapOutputFolder, String overviewerExecutable) {
        OverviewerOutputPanel panel = activePanels.get(mapName);
        if(panel == null) {
            panel = new OverviewerOutputPanel();
            activePanels.put(mapName, panel);
            tabPane.addTab(null, panel);
            int index = tabPane.indexOfComponent(panel);
            tabPane.setTabComponentAt(index, new TabTitleComponent(mapName, this));
        }
        tabPane.setSelectedComponent(panel);
        tabPane.repaint();
        if(!panel.isRunning()) {
            panel.start(minecraftFolder, mapName, mapOutputFolder, overviewerExecutable);
        }
    }
    
    /**
     * Stop the Overviewer run for the provided map and remove it from the tab.
     * 
     * @param mapName
     */
    public void stopOverviewerRun(String mapName) {
        OverviewerOutputPanel panel = activePanels.remove(mapName);
        if(panel != null) {
            panel.setRunning(false);
            tabPane.remove(panel);
        }
    }

    /**
     * Save preferences and exit.
     */
    public void exit() {
        for(OverviewerOutputPanel panel:activePanels.values()) {
            panel.setRunning(false);
        }
        savePrefs();
        System.exit(0);
    }
    @Override
    public void windowClosing(WindowEvent e) {
        exit();
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}

    private void loadPrefs() {
        Preferences prefs = Preferences.userNodeForPackage(OverviewerGui.class);
        
        int x = prefs.getInt(WINDOW_X, 0);
        int y = prefs.getInt(WINDOW_Y, 0);
        int width = prefs.getInt(WINDOW_WIDTH, 800);
        int height = prefs.getInt(WINDOW_HEIGHT, 600);
        parent.setBounds(x, y, width, height);
        boolean maximized = prefs.getBoolean(WINDOW_MAXIMIZED, false);
        if(maximized) {
            parent.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        String minecraftLocation[] = {"", 
                System.getProperty("user.home").replace("\\", "/") + "/.minecraft/saves",
                System.getProperty("user.home").replace("\\", "/") + "/AppData/Roaming/.minecraft/saves"};
        int choice = 0;
        if(new File(minecraftLocation[1]).isDirectory()) {
            choice = 1;
        }
        else if(new File(minecraftLocation[2]).isDirectory()) {
            choice = 2;
        }
        settingsPanel.setMinecraftSaveFolder(prefs.get(MINECRAFT_SAVE_DATA, minecraftLocation[choice]));
        settingsPanel.setOverviewerExecutable(prefs.get(OVERVIEWER_EXECUTABLE, ""));
        settingsPanel.setMapOutputFolder(prefs.get(MINECRAFT_MAP_OUTPUT, new File(System.getProperty("user.home") + "/MinecraftMaps").getAbsolutePath()));
    }
    
    private void savePrefs() {
        Preferences prefs = Preferences.userNodeForPackage(OverviewerGui.class);
        
        boolean maximized = (parent.getExtendedState() == JFrame.MAXIMIZED_BOTH);
        prefs.getBoolean(WINDOW_MAXIMIZED, maximized);
        if(!maximized) {
            prefs.putInt(WINDOW_X, parent.getX());
            prefs.putInt(WINDOW_Y, parent.getY());
            prefs.putInt(WINDOW_WIDTH, parent.getWidth());
            prefs.putInt(WINDOW_HEIGHT, parent.getHeight());
        }
        prefs.put(MINECRAFT_SAVE_DATA, settingsPanel.getMinecraftSaveFolder());
        prefs.put(MINECRAFT_MAP_OUTPUT, settingsPanel.getMapOutputFolder());
        prefs.put(OVERVIEWER_EXECUTABLE, settingsPanel.getOverviewerExecutable());
    }
    /**
     * Overviewer GUI entry point.
     * 
     * @param args Command line arguments.
     */
    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) { e.getMessage(); }
        JFrame f = new JFrame("Overviewer GUI");
        f.add(new OverviewerGui(f));
        f.setVisible(true);
    }
}
