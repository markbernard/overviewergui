package overviewergui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Provides a panel for the Overviewer output. Also executes the Overviewer command.
 * 
 * @author Mark Bernard
 */
public class OverviewerOutputPanel extends JPanel implements Runnable {
    private static final long serialVersionUID = -1231657415239800281L;
    private static final Object LOCK_OBJECT = new Object();

    private String map;
    private String mapFolder;
    private String mapOutputFolder;
    private String overviewerExecutable;
    private StringBuilder overviewerOutput;
    private JTextArea overviewerOutputView;
    private JScrollPane scrollPane;
    private JLabel runningLabel;
    private boolean running;

    /**
     * Set up GUI elements
     */
    public OverviewerOutputPanel() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);
        JButton stopButton = new JButton("Stop Overviewer");
        stopButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (LOCK_OBJECT) {
                    running = false;
                }
            }
        });
        topPanel.add(stopButton);
        runningLabel = new JLabel();
        runningLabel.setBackground(Color.RED);
        topPanel.add(runningLabel);
        
        overviewerOutputView = new JTextArea();
        overviewerOutputView.setEditable(false);
        overviewerOutputView.setLineWrap(true);
        scrollPane = new JScrollPane(overviewerOutputView);
        add(scrollPane, BorderLayout.CENTER);
        
    }
    
    /**
     * Set data items and start the run.
     * 
     * @param mapFolder
     * @param map
     * @param mapOutputFolder
     * @param overviewerExecutable
     */
    public void start(String mapFolder, String map, String mapOutputFolder, String overviewerExecutable) {
        this.map = map;
        this.mapFolder = mapFolder;
        this.mapOutputFolder = mapOutputFolder;
        this.overviewerExecutable = overviewerExecutable;
        runningLabel.setText("Overviewer is running");

        running = true;
        
        Thread t = new Thread(this, "Overviewer - " + map);
        t.start();
    }
    
    @Override
    public void run() {
        InputStreamReader in = null;
        try {
            overviewerOutput = new StringBuilder();
            String commandString = buildCommand();
            Process process = Runtime.getRuntime().exec(commandString);
            in = new InputStreamReader(new BufferedInputStream(process.getInputStream()), "UTF-8");
            char buffer[] = new char[8192];
            int read = -1;
            while((read = in.read(buffer)) > -1 && process.isAlive()) {
                overviewerOutput.append(buffer, 0, read);
                overviewerOutputView.setText(overviewerOutput.toString());
                JScrollBar bar = scrollPane.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
                scrollPane.repaint();
                synchronized (LOCK_OBJECT) {
                    if(!running) {
                        process.destroyForcibly();
                        process.waitFor();
                    }
                }
            }
            
            int exitValue = process.exitValue();
            runningLabel.setText("Overviewer " + (exitValue==0?"is complete":"has completed with errors"));
            runningLabel.setBackground(Color.GREEN);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
            scrollPane.repaint();
            if (in != null) { try { in.close(); } catch (Exception e) { e.getMessage(); } }
            running = false;
        }
    }
    
    /**
     * @return the running
     */
    public boolean isRunning() {
        synchronized (LOCK_OBJECT) {
            return running;
        }
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        synchronized (LOCK_OBJECT) {
            this.running = running;
        }
    }

    private String buildCommand() {
        StringBuilder command = new StringBuilder();
        
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            command.append("cmd /c ");
        }
        command.append(overviewerExecutable + " ");
        command.append(mapFolder + System.getProperty("file.separator") + map + " ");
        command.append(mapOutputFolder + System.getProperty("file.separator") + map);
        
        File output = new File(mapOutputFolder + "/" + map);
        if(!output.exists()) {
            if(!output.mkdirs()) {
                throw new RuntimeException("Unable to create output directory: " + output.getAbsolutePath());
            }
            
        }
        
        return command.toString();
    }
}
