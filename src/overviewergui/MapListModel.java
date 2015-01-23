package overviewergui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * Model to hold the list of available maps.
 * 
 * @author Mark Bernard
 */
public class MapListModel extends AbstractListModel<String> {
    private static final long serialVersionUID = -6696446612827244475L;
    
    private String folder;
    private List<String> maps;
    
    /**
     * Generate initial list.
     * 
     * @param folder Folder holding minecraft maps
     */
    public MapListModel(String folder) {
        this.folder = folder;
        maps = new ArrayList<String>();
        updateList();
    }
    
    @Override
    public int getSize() {
        return maps.size();
    }

    @Override
    public String getElementAt(int index) {
        if(0 <= index && index < maps.size()) {
            return maps.get(index);
        }
        return null;
    }

    /**
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(String folder) {
        if(!this.folder.equals(folder)) {
            this.folder = folder;
            updateList();
        }
    }

    private void updateList() {
        maps.clear();
        if(!"".equals(folder)) {
            File base = new File(folder);
            if(base.isDirectory()) {
                File list[] = base.listFiles();
                for(File file:list) {
                    if(file.isDirectory()) {
                        maps.add(file.getName());
                    }
                }
            }
            fireContentsChanged(this, 0, getSize());
        }
    }
}
