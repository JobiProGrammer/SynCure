package syncure.gui;

import syncure.core.Config;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileBrowserPanel extends JPanel {

    private JPanel treePanel;
    private JPanel filePanel;
    private JSplitPane splitPane;
    private Config config;
    private JTree tree;

    // TODO: Heavily WIP, not working at all
    public FileBrowserPanel(Config config) {
        super(new BorderLayout());
        this.config = config;

        tree = createTree();
        treePanel = new JPanel(new BorderLayout());
        treePanel.add(tree, BorderLayout.CENTER);
        filePanel = new JPanel(new BorderLayout());
        filePanel.add(new JLabel("File"));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treePanel, filePanel);
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(300);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> createTreeModel());
        treePanel.add(refreshButton, BorderLayout.PAGE_START);

        this.add(splitPane);
    }

    private void createTreeModel() {
        DefaultMutableTreeNode root = createDirStructure(config.getLocalDirectory().toFile());
        TreeModel treeModel = new DefaultTreeModel(root);
		tree.setModel(treeModel);
        //return new JTree(root);
    }
    private JTree createTree() {
        DefaultMutableTreeNode root = createDirStructure(config.getLocalDirectory().toFile());
        return new JTree(root);
    }

    private DefaultMutableTreeNode createDirStructure(File parent) {
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(parent.getName());
        if (parent.isDirectory()) {
            for (File file : parent.listFiles()) {
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(file.getName());
                if (file.isDirectory()) {
                    tempNode.add(createDirStructure(file));
                }
                parentNode.add(tempNode);
            }
        }
        return parentNode;
    }

}
