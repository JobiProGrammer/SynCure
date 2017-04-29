package syncure.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileBrowserPanel extends JPanel {

    JPanel treePanel;
    JPanel filePanel;
    JSplitPane splitPane;

    // TODO: Heavily WIP, not working at all
    public FileBrowserPanel() {
        super(new BorderLayout());
        treePanel = new JPanel();
        treePanel.add(getTree());
        filePanel = new JPanel();
        filePanel.add(new JLabel("File"));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treePanel, filePanel);
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);
        //splitPane.setDividerLocation(300);

        this.add(splitPane);
    }

    private JTree getTree() {
        JTree tree = new JTree();

        return tree;
    }

}
