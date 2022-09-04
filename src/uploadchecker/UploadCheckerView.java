/*
 * UploadCheckerView.java
 */

package uploadchecker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
import pt.unl.fct.di.tsantos.util.Pair;
import pt.unl.fct.di.tsantos.util.app.AppUtils;
import pt.unl.fct.di.tsantos.util.tmdb.SearchResult;
import pt.unl.fct.di.tsantos.util.tmdb.TMDbSearch;

/**
 * The application's main frame.
 */
public class UploadCheckerView extends FrameView {

    public UploadCheckerView(SingleFrameApplication app) {
        super(app);
        
        initComponents();

        if (UploadCheckerApp.USER_OS != null &&
                UploadCheckerApp.USER_OS.toLowerCase().equals("windows 7")) {
            Icon homeFolderIcon =
                    getResourceMap().getIcon("Application.homeFolderIcon");
            Icon upFolderIcon =
                    getResourceMap().getIcon("Application.upFolderIcon");
            Icon newFolderIcon =
                    getResourceMap().getIcon("Application.newFolderIcon");
            Icon listViewIcon =
                    getResourceMap().getIcon("Application.listViewIcon");
            Icon detailsViewIcon =
                    getResourceMap().getIcon("Application.detailsViewIcon");
            AppUtils.useWindows7FileChooserIcons(homeFolderIcon, upFolderIcon,
                    newFolderIcon, listViewIcon, detailsViewIcon);
        }

        getFrame().setResizable(false);

        settingsDir = new File(UploadCheckerApp.USER_HOME,
                                UploadCheckerApp.SETTINGS_DIR);
        if (!settingsDir.exists()) settingsDir.mkdir();

        try {
            settings = Persistent.resurrect(
                                    new File(settingsDir.getAbsolutePath()
                                + UploadCheckerApp.FILE_SEPARATOR
                                + "settings.db"),Properties.class);

            /*settings = Persistent.resurrect(new File(settingsDir.getAbsolutePath()
                                + UploadCheckerApp.FILE_SEPARATOR
                                + "settings.db"), Settings.class);*/
        } catch (Exception ex) {
            /*settings = new Settings();*/
            settings = new Properties();
        }

        String url = settings.getProperty("announceURL");
        //String url = settings.getAnnounceURL();
        announceTextField.setText(url == null ? "" : url);

        getFrame().setTitle("Upload Checker");

        movieSearchDialog.setModal(true);
        movieSearchDialog.setResizable(false);
        movieSearchDialog.setTitle("Movie Search");
        movieSearchDialog.pack();
        
        Dimension dim = searchResScrollPane.getSize();
        maxImgWidth = (int)
            (movieSearchDialog.getSize().getWidth() - (dim.getWidth() + 20));
        maxImgHeight = (int) dim.getHeight() - 20;
        imgLabel.setText("");
        setImgIcon(MISSING_ICON);
        searchTextField.setText("");
        movieFrameState = IDLE;
        
        imgsCache = new Cache<String, URL>();

        repCache = new Cache<Pair<File, Media.Type>, Report>();

        try {
            /*searchCache = Persistent.loadCache(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR +
                                    "search.cache", SearchCache.class,
                                    SIZE_5MB);*/
            searchCache = new Cache<String, List<SearchResult>>();
        } catch(Exception e) {
            
        }

        try {
            File cache = new File(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR +
                                    "langs.cache");
            if (cache.length() > SIZE_5MB) {
                languageCache = new Cache<String, List<String>>();
            } else {
                languageCache = Cache.load(new FileInputStream(cache));
            }
        } catch(Exception e) {
            languageCache = new Cache<String, List<String>>();
        }

        DefaultListModel dlm = (DefaultListModel) filterList.getModel();
        dlm.clear();
        List<String> filters = (List<String>) settings.get("nameFilters");
        if (filters == null) {
            filters = new LinkedList<String>();
            filters.add("BluRay.1080p.DTS.x264.dxva-EuReKA".toLowerCase());
            settings.put("nameFilters", filters);
        }
        for (String s : filters) dlm.addElement(s);

        settingsDialog.pack();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = UploadCheckerApp.getApplication().getMainFrame();
            aboutBox = new UploadCheckerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        UploadCheckerApp.getApplication().show(aboutBox);
    }

    @Action
    private void showSearchDialog() {
        movieSearchDialog.setModal(true);
        movieSearchDialog.setLocationRelativeTo(getFrame());
        movieSearchDialog.setVisible(true);
    }

    @Action
    private void showSettingsDialog() {
        settingsDialog.setModal(true);
        settingsDialog.setLocationRelativeTo(getFrame());
        settingsDialog.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();
        comboBox = new javax.swing.JComboBox();
        filesScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        outputScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        createButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        addMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        mediaInfoCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        originalAudioCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        settingsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        fileChooser = new javax.swing.JFileChooser();
        torrFrame = new javax.swing.JFrame();
        progressBar = new javax.swing.JProgressBar();
        taskLabel = new javax.swing.JLabel();
        mediaInfoFrame = new javax.swing.JFrame();
        mediaInfoScrollPane = new javax.swing.JScrollPane();
        mediaInfoTextArea = new javax.swing.JTextArea();
        movieSearchDialog = new javax.swing.JDialog();
        searchResScrollPane = new javax.swing.JScrollPane();
        searchResList = new javax.swing.JList();
        imgLabel = new javax.swing.JLabel();
        selectButton = new javax.swing.JButton();
        notFoundButton = new javax.swing.JButton();
        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        settingsDialog = new javax.swing.JDialog();
        announceLabel = new javax.swing.JLabel();
        announceTextField = new javax.swing.JTextField();
        filterScrollPane = new javax.swing.JScrollPane();
        filterList = new javax.swing.JList();
        addFilterButton = new javax.swing.JButton();
        removeFilterButton = new javax.swing.JButton();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(uploadchecker.UploadCheckerApp.class).getContext().getResourceMap(UploadCheckerView.class);
        removeButton.setText(resourceMap.getString("removeButton.text")); // NOI18N
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1080p Normal movie", "1080p Animation", "720p Normal movie", "720p Animation" }));
        comboBox.setName("comboBox"); // NOI18N
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });

        filesScrollPane.setName("filesScrollPane"); // NOI18N

        fileList.setModel(new DefaultListModel());
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setName("fileList"); // NOI18N
        fileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fileListValueChanged(evt);
            }
        });
        filesScrollPane.setViewportView(fileList);

        outputScrollPane.setName("outputScrollPane"); // NOI18N

        textArea.setColumns(20);
        textArea.setEditable(false);
        textArea.setRows(5);
        textArea.setName("textArea"); // NOI18N
        outputScrollPane.setViewportView(textArea);

        createButton.setText(resourceMap.getString("createButton.text")); // NOI18N
        createButton.setName("createButton"); // NOI18N
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(createButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addComponent(removeButton)
                .addContainerGap())
            .addComponent(filesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addComponent(outputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(filesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeButton)
                    .addComponent(createButton))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        addMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        addMenuItem.setText(resourceMap.getString("addMenuItem.text")); // NOI18N
        addMenuItem.setName("addMenuItem"); // NOI18N
        addMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(addMenuItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(uploadchecker.UploadCheckerApp.class).getContext().getActionMap(UploadCheckerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        optionsMenu.setText(resourceMap.getString("optionsMenu.text")); // NOI18N
        optionsMenu.setName("optionsMenu"); // NOI18N

        mediaInfoCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mediaInfoCheckBoxMenuItem.setText(resourceMap.getString("mediaInfoCheckBoxMenuItem.text")); // NOI18N
        mediaInfoCheckBoxMenuItem.setName("mediaInfoCheckBoxMenuItem"); // NOI18N
        mediaInfoCheckBoxMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mediaInfoCheckBoxMenuItemItemStateChanged(evt);
            }
        });
        optionsMenu.add(mediaInfoCheckBoxMenuItem);

        originalAudioCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        originalAudioCheckBoxMenuItem.setSelected(true);
        originalAudioCheckBoxMenuItem.setText(resourceMap.getString("originalAudioCheckBoxMenuItem.text")); // NOI18N
        originalAudioCheckBoxMenuItem.setName("originalAudioCheckBoxMenuItem"); // NOI18N
        optionsMenu.add(originalAudioCheckBoxMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        optionsMenu.add(jSeparator1);

        settingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        settingsMenuItem.setText(resourceMap.getString("settingsMenuItem.text")); // NOI18N
        settingsMenuItem.setName("settingsMenuItem"); // NOI18N
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(settingsMenuItem);

        menuBar.add(optionsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        fileChooser.setName("fileChooser"); // NOI18N

        torrFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        torrFrame.setName("torrFrame"); // NOI18N
        torrFrame.setResizable(false);

        progressBar.setName("progressBar"); // NOI18N

        taskLabel.setText(resourceMap.getString("taskLabel.text")); // NOI18N
        taskLabel.setName("taskLabel"); // NOI18N

        javax.swing.GroupLayout torrFrameLayout = new javax.swing.GroupLayout(torrFrame.getContentPane());
        torrFrame.getContentPane().setLayout(torrFrameLayout);
        torrFrameLayout.setHorizontalGroup(
            torrFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, torrFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(torrFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(taskLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        torrFrameLayout.setVerticalGroup(
            torrFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(torrFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(taskLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        mediaInfoFrame.setTitle(resourceMap.getString("mediaInfoFrame.title")); // NOI18N
        mediaInfoFrame.setName("mediaInfoFrame"); // NOI18N
        mediaInfoFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                mediaInfoFrameWindowClosing(evt);
            }
        });

        mediaInfoScrollPane.setName("mediaInfoScrollPane"); // NOI18N

        mediaInfoTextArea.setColumns(20);
        mediaInfoTextArea.setEditable(false);
        mediaInfoTextArea.setRows(5);
        mediaInfoTextArea.setName("mediaInfoTextArea"); // NOI18N
        mediaInfoScrollPane.setViewportView(mediaInfoTextArea);

        javax.swing.GroupLayout mediaInfoFrameLayout = new javax.swing.GroupLayout(mediaInfoFrame.getContentPane());
        mediaInfoFrame.getContentPane().setLayout(mediaInfoFrameLayout);
        mediaInfoFrameLayout.setHorizontalGroup(
            mediaInfoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mediaInfoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        mediaInfoFrameLayout.setVerticalGroup(
            mediaInfoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mediaInfoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        movieSearchDialog.setName("movieSearchDialog"); // NOI18N
        movieSearchDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                movieSearchDialogWindowClosing(evt);
            }
        });

        searchResScrollPane.setName("searchResScrollPane"); // NOI18N

        searchResList.setModel(new DefaultListModel());
        searchResList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        searchResList.setName("searchResList"); // NOI18N
        searchResList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                searchResListValueChanged(evt);
            }
        });
        searchResScrollPane.setViewportView(searchResList);

        imgLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgLabel.setText(resourceMap.getString("imgLabel.text")); // NOI18N
        imgLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        imgLabel.setName("imgLabel"); // NOI18N

        selectButton.setText(resourceMap.getString("selectButton.text")); // NOI18N
        selectButton.setName("selectButton"); // NOI18N
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        notFoundButton.setText(resourceMap.getString("notFoundButton.text")); // NOI18N
        notFoundButton.setName("notFoundButton"); // NOI18N
        notFoundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notFoundButtonActionPerformed(evt);
            }
        });

        searchTextField.setName("searchTextField"); // NOI18N

        searchButton.setText(resourceMap.getString("searchButton.text")); // NOI18N
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout movieSearchDialogLayout = new javax.swing.GroupLayout(movieSearchDialog.getContentPane());
        movieSearchDialog.getContentPane().setLayout(movieSearchDialogLayout);
        movieSearchDialogLayout.setHorizontalGroup(
            movieSearchDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movieSearchDialogLayout.createSequentialGroup()
                .addGroup(movieSearchDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(movieSearchDialogLayout.createSequentialGroup()
                        .addComponent(searchResScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(imgLabel))
                    .addGroup(movieSearchDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notFoundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        movieSearchDialogLayout.setVerticalGroup(
            movieSearchDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movieSearchDialogLayout.createSequentialGroup()
                .addGroup(movieSearchDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchResScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(movieSearchDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(imgLabel)))
                .addGap(12, 12, 12)
                .addGroup(movieSearchDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(selectButton)
                    .addComponent(notFoundButton)
                    .addComponent(searchButton)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        settingsDialog.setTitle(resourceMap.getString("settingsDialog.title")); // NOI18N
        settingsDialog.setName("settingsDialog"); // NOI18N
        settingsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                settingsDialogWindowClosing(evt);
            }
        });

        announceLabel.setText(resourceMap.getString("announceLabel.text")); // NOI18N
        announceLabel.setName("announceLabel"); // NOI18N

        announceTextField.setText(resourceMap.getString("announceTextField.text")); // NOI18N
        announceTextField.setToolTipText(resourceMap.getString("announceTextField.toolTipText")); // NOI18N
        announceTextField.setName("announceTextField"); // NOI18N

        filterScrollPane.setName("filterScrollPane"); // NOI18N

        filterList.setModel(new DefaultListModel());
        filterList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        filterList.setName("filterList"); // NOI18N
        filterScrollPane.setViewportView(filterList);

        addFilterButton.setText(resourceMap.getString("addFilterButton.text")); // NOI18N
        addFilterButton.setName("addFilterButton"); // NOI18N
        addFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFilterButtonActionPerformed(evt);
            }
        });

        removeFilterButton.setText(resourceMap.getString("removeFilterButton.text")); // NOI18N
        removeFilterButton.setName("removeFilterButton"); // NOI18N
        removeFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFilterButtonActionPerformed(evt);
            }
        });

        filterTextField.setText(resourceMap.getString("filterTextField.text")); // NOI18N
        filterTextField.setName("filterTextField"); // NOI18N

        filterLabel.setText(resourceMap.getString("filterLabel.text")); // NOI18N
        filterLabel.setName("filterLabel"); // NOI18N

        javax.swing.GroupLayout settingsDialogLayout = new javax.swing.GroupLayout(settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addComponent(announceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(announceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(filterLabel)
                    .addComponent(filterScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addComponent(addFilterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeFilterButton)))
                .addContainerGap())
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, settingsDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(announceTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, settingsDialogLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(announceLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addFilterButton)
                    .addComponent(removeFilterButton)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void addMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMenuItemActionPerformed
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = 
                AppUtils.getExtensionFilter("Matroska (.mkv)", "mkv");
        fileChooser.setFileFilter(extensionFilter);
        fileChooser.setSelectedFile(new File(""));
        int returnVal = fileChooser.showOpenDialog(this.getFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            DefaultListModel model = (DefaultListModel) fileList.getModel();
            model.addElement(file.getAbsolutePath());
        }
        fileChooser.removeChoosableFileFilter(extensionFilter);
    }//GEN-LAST:event_addMenuItemActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int index = fileList.getSelectedIndex();
        if (index < 0) return ;
        else {
            DefaultListModel model = (DefaultListModel) fileList.getModel();
            model.remove(index);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void fileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fileListValueChanged
        if (evt != null && evt.getValueIsAdjusting()) return;
        textArea.setText("");
        int index = fileList.getSelectedIndex();
        if (index < 0) return ;
        else {
            try {
                DefaultListModel model = (DefaultListModel) fileList.getModel();
                String s = (String) model.get(index);

                File f = new File(s);
                Pair<File, Media.Type> pair =
                        new Pair<File, Media.Type>(f,getCurrentType());
                Report rep = repCache.get(pair);
                if (rep == null) {
                if (!originalAudioCheckBoxMenuItem.isSelected()) {
                    Media med = new Media(s);
                    Report res = med.check(getCurrentType());
                    textArea.setText(res.toFormatedString());
                    mediaInfoTextArea.setText(res.getMediaInfoOutput());
                    repCache.put(pair, res);
                }
                else {
                    String name = new File(s).getName().toLowerCase();
                    name = name.replace(".mkv", "");
                    DefaultListModel dlm =
                            (DefaultListModel) filterList.getModel();
                    Enumeration<?> enume = dlm.elements();
                    int matchSize = - Integer.MAX_VALUE;
                    int matchPosition = - Integer.MAX_VALUE;
                    int matchIndex = - Integer.MAX_VALUE;
                    int count = 0;
                    while(enume.hasMoreElements()) {
                        String element = (String) enume.nextElement();
                        int pos = name.indexOf(element.toLowerCase());
                        if (pos >= 0) {
                            if (element.length() > matchSize) {
                                matchSize = element.length();
                                matchPosition = pos;
                                matchIndex = count;
                            }
                        }
                        count++;
                    }
                    if (matchIndex >= 0)
                        name = name.substring(0, matchPosition);
                    String replaced = name;
                    StringTokenizer st = new StringTokenizer(replaced, " .-");
                    String formated = "";
                    while(st.hasMoreTokens()) {
                        String token = st.nextToken();
                        formated += token + " ";
                    }
                    formated = formated.trim();
                    searchTextField.setText(formated);

                    currentFile = s;

                    showSearchDialog();

                }
                } else {
                    textArea.setText(rep.toFormatedString());
                    mediaInfoTextArea.setText(rep.getMediaInfoOutput());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this.getFrame(), ex.toString(),
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_fileListValueChanged

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxItemStateChanged
        fileListValueChanged(null);
    }//GEN-LAST:event_comboBoxItemStateChanged

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        showSettingsDialog();
}//GEN-LAST:event_settingsMenuItemActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        URL aurl = null;
        if (announceTextField.getText() == null ||
                announceTextField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(UploadCheckerView.this.getFrame(),
                    "First introduce your announce URL",
                    "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try {
                aurl = new URL(announceTextField.getText());
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(UploadCheckerView.this.getFrame(),
                        "Malformed URL",
                        "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        final URL announceURL = aurl;

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fileChooser.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();

            final JFrame torrFrame = new JFrame();
            final JProgressBar progressBar = new JProgressBar(0, 100);
            final JLabel taskLabel = new JLabel();

            torrFrame.setCursor(
                    new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            torrFrame.setName("torrFrame");
            torrFrame.setResizable(false);

            progressBar.setName("progressBar");

            taskLabel.setName("taskLabel");

            javax.swing.GroupLayout torrFrameLayout =
                    new javax.swing.GroupLayout(torrFrame.getContentPane());
            torrFrame.getContentPane().setLayout(torrFrameLayout);
            torrFrameLayout.setHorizontalGroup(
                    torrFrameLayout.createParallelGroup(
                    javax.swing.GroupLayout.Alignment.LEADING).
                    addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    torrFrameLayout.createSequentialGroup().addContainerGap().
                    addGroup(torrFrameLayout.createParallelGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING).
                    addComponent(taskLabel, javax.swing.GroupLayout.
                    Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
                    187, Short.MAX_VALUE).addComponent(progressBar,
                    javax.swing.GroupLayout.Alignment.LEADING,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 187,
                    Short.MAX_VALUE)).addGap(10, 10, 10)));
            torrFrameLayout.setVerticalGroup(
                    torrFrameLayout.createParallelGroup(
                    javax.swing.GroupLayout.Alignment.LEADING).
                    addGroup(torrFrameLayout.createSequentialGroup().
                    addContainerGap().addComponent(taskLabel,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE).
                    addGap(9, 9, 9).addComponent(progressBar,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE).
                    addContainerGap()));

            progressBar.setValue(0);
            torrFrame.setResizable(false);
            torrFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            torrFrame.pack();
            torrFrame.setLocationRelativeTo(this.getFrame());
            torrFrame.setVisible(true);
            torrFrame.setTitle("Creating torrent");
            new Thread() {

                @Override
                public void run() {
                    try {
                        TOTorrentProgressListener list =
                                new TOTorrentProgressListener() {

                            public void reportProgress(int p) {
                                progressBar.setValue(p);
                            }

                            public void reportCurrentTask(
                                    String task_description) {
                                taskLabel.setText("Task: " + task_description);
                            }
                        };

                        TOTorrentCreator c = TOTorrentFactory.
                                createFromFileOrDirWithFixedPieceLength(
                                file, announceURL, 4 * 1024 * 1024);

                        c.addListener(list);

                        TOTorrent t = c.create();

                        t.setPrivate(true);

                        //t.print();

                        t.serialiseToBEncodedFile(new File(file.getParent() + 
                                UploadCheckerApp.FILE_SEPARATOR +
                                file.getName() + ".torrent"));

                        torrFrame.dispose();

                        JOptionPane.showMessageDialog(
                                UploadCheckerView.this.getFrame(),
                                "Torrent Complete",
                                "Done!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (TOTorrentException ex) {
                        JOptionPane.showMessageDialog(
                                UploadCheckerView.this.getFrame(),
                                ex.toString(),
                                "Error!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }.start();
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
}//GEN-LAST:event_createButtonActionPerformed

    private void mediaInfoFrameWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_mediaInfoFrameWindowClosing
        mediaInfoCheckBoxMenuItem.setSelected(false);
    }//GEN-LAST:event_mediaInfoFrameWindowClosing

    private void mediaInfoCheckBoxMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mediaInfoCheckBoxMenuItemItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            mediaInfoFrame.setVisible(true);
            mediaInfoFrame.pack();
            mediaInfoFrame.setLocationRelativeTo(this.getFrame());
        } else mediaInfoFrame.setVisible(false);
}//GEN-LAST:event_mediaInfoCheckBoxMenuItemItemStateChanged

    private void searchResListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_searchResListValueChanged
        if (evt == null || evt.getValueIsAdjusting()) return;
        int index = searchResList.getSelectedIndex();
        if (index < 0) return ;
        DefaultListModel dlm = (DefaultListModel) searchResList.getModel();
        final SearchResult sr = (SearchResult) dlm.get(index);
        if (sr.getImgURL() == null) {
            setImgIcon(MISSING_ICON);
        } else {
            try {
                URL imgURL = imgsCache.get(sr.getMovieID());
                if (imgURL == null) {
                    setImgIcon(MISSING_ICON);
                } else {
                    BufferedImage img = ImageIO.read(imgURL);
                    setImgIcon(new ImageIcon(img));
                }
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_searchResListValueChanged

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String text = searchTextField.getText();
        if (text == null || text.length() <= 0) return;
        //if (!checkMovieFrameState(IDLE)) return ;
        setMovieFrameState(SEARCHING);
        setImgIcon(MISSING_ICON);
        movieSearchDialog.setTitle("Movie Search [searching]");
        try {
            final DefaultListModel dlm =
                    (DefaultListModel) searchResList.getModel();
            dlm.clear();
            final String id = text.replace(" ", "+").trim();
            List<SearchResult> sres = searchCache.get(id);
            if (sres == null) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            List<SearchResult> sres = TMDbSearch.search(id);
                            searchCache.put(id, sres);
                            /*Persistent.persist(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR
                                    + "search.cache", searchCache);*/
                            dlm.clear();
                            for (SearchResult sr : sres) {
                                dlm.addElement(sr);
                            }
                            setMovieFrameState(IDLE);
                            movieSearchDialog.setTitle("Movie Search");
                            if (!sres.isEmpty()) {
                                JOptionPane.showMessageDialog(
                                        movieSearchDialog,
                                        sres.size() + (sres.size() > 1 ?
                                            " Results" : " Result") + " Found",
                                "Done!", JOptionPane.INFORMATION_MESSAGE);
                                downloadImages(sres,10,2);
                            } else {
                                JOptionPane.showMessageDialog(
                                        movieSearchDialog,
                                        "Nothing Found",
                                "Done!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            //new DownloadImagesThread(sres).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                for (SearchResult sr : sres) {
                    dlm.addElement(sr);
                }
                setMovieFrameState(IDLE);
                movieSearchDialog.setTitle("Movie Search");
                //new DownloadImagesThread(sres).start();
                if (!sres.isEmpty()) {
                    JOptionPane.showMessageDialog(
                                        movieSearchDialog,
                                        sres.size() + (sres.size() > 1 ?
                                            " Results" : " Result") + " Found",
                                "Done!", JOptionPane.INFORMATION_MESSAGE);
                    downloadImages(sres,10,2);
                } else {
                    JOptionPane.showMessageDialog(
                                        movieSearchDialog,
                                        "Nothing Found",
                                "Done!", JOptionPane.INFORMATION_MESSAGE);
                    
                }
            }
        } catch (Exception e) {
            
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void notFoundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notFoundButtonActionPerformed
        //if (!checkMovieFrameState(IDLE)) return ;
        //System.out.println(evt);
        if (currentFile == null) return;
        try {
            Media med = new Media(currentFile);
            Report res = med.check(getCurrentType());
            textArea.setText(res.toFormatedString());
            mediaInfoTextArea.setText(res.getMediaInfoOutput());
            repCache.put(new Pair<File, Media.Type>(med.getFile(),
                    getCurrentType()), res);
        } catch(Exception e) {
        }
        searchResList.setSelectedIndex(-1);
        DefaultListModel dlm = (DefaultListModel) searchResList.getModel();
        dlm.clear();
        setImgIcon(MISSING_ICON);
        searchTextField.setText("");
        movieSearchDialog.setVisible(false);
    }//GEN-LAST:event_notFoundButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        if (currentFile == null) return;
        //if (!checkMovieFrameState(IDLE)) return;
        final int index = searchResList.getSelectedIndex();
        if (index < 0) return;
        new Thread() {
            @Override
            public void run() {
                try {
                    setMovieFrameState(SELECTING);
                    movieSearchDialog.setTitle("Movie Search [selecting]");
                    DefaultListModel dlm =
                            (DefaultListModel) searchResList.getModel();
                    Media med = new Media(currentFile);
                    movieSearchDialog.setTitle(
                            "Movie Search [fetching original languages]");
                    SearchResult m = (SearchResult) dlm.get(index);
                    List<String> langs = languageCache.get(m.movieID);
                    if (langs == null) {
                        langs =
                            TMDbSearch.getOriginalLanguages(m);
                        languageCache.put(m.getMovieID(), langs);
                        languageCache.store(new FileOutputStream(
                                new File(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR
                                    + "langs.cache")));
                        /*Persistent.persist(languageCache,
                                new File(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR
                                    + "langs.cache"));*/
                    }
                    med.setOriginalLanguages(langs);
                    Report res = med.check(getCurrentType());
                    textArea.setText(res.toFormatedString());
                    mediaInfoTextArea.setText(res.getMediaInfoOutput());
                    repCache.put(new Pair<File, Media.Type>(med.getFile(),
                    getCurrentType()), res);
                    searchResList.setSelectedIndex(-1);
                    dlm.clear();
                    setImgIcon(MISSING_ICON);
                    searchTextField.setText("");
                    movieSearchDialog.setVisible(false);
                    movieSearchDialog.setTitle("Movie Search");
                    setMovieFrameState(IDLE);
                } catch (Exception e) {
                }
            }
        }.start();
        
    }//GEN-LAST:event_selectButtonActionPerformed

    private void addFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFilterButtonActionPerformed
        String text = filterTextField.getText();
        if (text == null) return;
        text = text.toLowerCase().trim();
        if (text.length() <= 0) return;
        DefaultListModel dlm = (DefaultListModel) filterList.getModel();
        if (!dlm.contains(text)) {
            dlm.addElement(text);
            List<String> list = (List<String>) settings.get("nameFilters");
            if (!list.contains(text)) list.add(text);
            settings.put("nameFilters", list);
            try {
                Persistent.persist(settings,
                        new File(settingsDir.getAbsolutePath()
                                    + UploadCheckerApp.FILE_SEPARATOR
                                    + "settings.db"));
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_addFilterButtonActionPerformed

    private void removeFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFilterButtonActionPerformed
        int index = filterList.getSelectedIndex();
        if (index < 0) return;
        DefaultListModel dlm = (DefaultListModel) filterList.getModel();
        Object remove = dlm.remove(index);
        String toRemove = null;
        if (remove != null) toRemove = (String) remove;
        List<String> list = (List<String>) settings.get("nameFilters");
        list.remove(toRemove);
        //settings.removeNameFilter(toRemove);
        try {
            Persistent.persist(settings, new File(settingsDir.getAbsolutePath()
                                + UploadCheckerApp.FILE_SEPARATOR
                                + "settings.db"));
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_removeFilterButtonActionPerformed

    private void movieSearchDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_movieSearchDialogWindowClosing
        notFoundButtonActionPerformed(null);
    }//GEN-LAST:event_movieSearchDialogWindowClosing

    private void settingsDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_settingsDialogWindowClosing
        if (announceTextField.getText() != null) {
            try {
                String trim = announceTextField.getText().trim();
                settings.setProperty("announceURL", trim);
                //settings.setAnnounceURL(trim);
                Persistent.persist(settings, new File(settingsDir.getAbsolutePath() +
                        UploadCheckerApp.FILE_SEPARATOR +
                        "settings.db"));
            } catch (Exception ex) { }
        }
    }//GEN-LAST:event_settingsDialogWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFilterButton;
    private javax.swing.JMenuItem addMenuItem;
    private javax.swing.JLabel announceLabel;
    private javax.swing.JTextField announceTextField;
    private javax.swing.JComboBox comboBox;
    private javax.swing.JButton createButton;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JList fileList;
    private javax.swing.JScrollPane filesScrollPane;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JList filterList;
    private javax.swing.JScrollPane filterScrollPane;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBoxMenuItem mediaInfoCheckBoxMenuItem;
    private javax.swing.JFrame mediaInfoFrame;
    private javax.swing.JScrollPane mediaInfoScrollPane;
    private javax.swing.JTextArea mediaInfoTextArea;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JDialog movieSearchDialog;
    private javax.swing.JButton notFoundButton;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JCheckBoxMenuItem originalAudioCheckBoxMenuItem;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeFilterButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JList searchResList;
    private javax.swing.JScrollPane searchResScrollPane;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JButton selectButton;
    private javax.swing.JDialog settingsDialog;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JLabel taskLabel;
    private javax.swing.JTextArea textArea;
    private javax.swing.JFrame torrFrame;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;

    private File settingsDir;

    private int maxImgWidth;
    private int maxImgHeight;

    private Cache<String, URL> imgsCache;

    private Cache<String, List<SearchResult>> searchCache;

    private Cache<String, List<String>> languageCache;

    private Cache<Pair<File, Media.Type>, Report> repCache;

    private String currentFile;

    private static final MissingIcon MISSING_ICON = new MissingIcon();

    private Integer movieFrameState;

    //private Settings settings;

    private Properties settings;

    private static final int IDLE = 0x01;
    private static final int SEARCHING = 0x02;
    private static final int FETCHING = 0x03;
    private static final int SELECTING = 0x04;

    private static final long SIZE_5MB = 5L*1024L*1024L;

    private void setMovieFrameState(int state) {
        synchronized(movieFrameState) {
            movieFrameState = state;
            //System.out.println(movieFrameState);
        }
    }

    private boolean checkMovieFrameState(int state) {
        synchronized(movieFrameState) {
            return (movieFrameState == state);
        }
    }

    private void setImgIcon(Icon ico) {
        synchronized (imgLabel) {
            if (ico == null) ico = MISSING_ICON;
            movieSearchDialog.pack();
            movieSearchDialog.remove(imgLabel);
            imgLabel.setIcon(ico);
            imgLabel.setBounds((int) ((movieSearchDialog.getSize().getWidth() -
                    searchResScrollPane.getWidth()) / 2.0 +
                    searchResScrollPane.getWidth() - ico.getIconWidth() / 2.0),
                    (int) (searchResScrollPane.getHeight() / 2.0 -
                    ico.getIconHeight() / 2.0), ico.getIconWidth(),
                    ico.getIconHeight());
            imgLabel.setPreferredSize(new Dimension(imgLabel.getSize()));
            movieSearchDialog.add(imgLabel);
            movieSearchDialog.validate();
            movieSearchDialog.repaint();
        }
    }

    private Media.Type getCurrentType() {
        int index = comboBox.getSelectedIndex();
        if (index < 0) return Media.Type.Movie1080p;
        else {
            switch(index) {
                case 0: return Media.Type.Movie1080p;
                case 1: return Media.Type.Animation1080p;
                case 2: return Media.Type.Movie720p;
                case 3: return Media.Type.Animation720p;
                default: return Media.Type.Movie1080p;
            }
        }
    }

    private void downloadImages(List<SearchResult> lsr,
            int maxThreads, int logBase) {
        if (lsr.size() == 0) return;
        int d = (int)Math.ceil(Math.log(lsr.size())/Math.log(logBase * 1.0));
        if (d == 0 || maxThreads == 1) {
            /*WaitThread wt = new WaitThread(1);
            wt.start();*/
            new DownloadImagesThread(lsr,0,lsr.size()).start();
        } else {
            int step = (int) Math.ceil(lsr.size()/((d + 1)*1.0));
            int threads = (int) Math.ceil(lsr.size()/(step*1.0));
            //System.out.println(maxThreads + ", " + logBase + ", " + threads);
            if (threads > maxThreads)
                downloadImages(lsr, maxThreads, logBase*2);
            else {
                /*WaitThread wt = new WaitThread(threads);
                wt.start();*/
                int left = lsr.size();
                int count = 0;
                while(left > 0) {
                    int start = lsr.size() - left;
                    int end = Math.min(start + step, lsr.size());
                    //System.out.println(start + " " + end);
                    new DownloadImagesThread(
                            lsr.subList(start,end),start,lsr.size()).start();
                    left -= (end - start);
                    count++;
                }
                //System.out.println("Created: " + count);
            }
        }
    }

    private class WaitThread extends Thread {
        private int threads;
        private ReentrantLock lock;
        private Condition c;

        public WaitThread(int threads) {
            this.threads = threads;
            lock = new ReentrantLock();
            c = lock.newCondition();
        }

        @Override
        public void run() {
            lock.lock();
            while (threads > 0) {
                try {
                    System.out.println("Threads " + threads);
                    c.await();
                } catch (InterruptedException ex) {
                }
            }
            movieSearchDialog.setTitle("Movie Search [wait done]");
            lock.unlock();
        }

        public void dec() {
            lock.lock();
            threads--;
            c.signalAll();
            lock.unlock();
        }
    }

    private class DownloadImagesThread extends Thread {

        private List<SearchResult> lsr;
        private int start;
        private int total;
        //private WaitThread wt;

        public DownloadImagesThread(List<SearchResult> lsr,
                int start, int total/*, WaitThread wt*/) {
            this.lsr = lsr;
            this.start = start;
            this.total = total;
            //this.wt = wt;
        }

        @Override
        public void run() {
            try {
                setMovieFrameState(FETCHING);
                movieSearchDialog.setTitle("Movie Search [fetching images]");
                int count = start;
                for (SearchResult sr : lsr) {
                    movieSearchDialog.setTitle("Movie Search [fetching image " +
                            (count + 1) + "/" + total + "]");
                    URL url = imgsCache.get(sr.getMovieID());
                    if (url != null || sr.getImgURL() == null) {
                        count++;
                        continue;
                    }
                    BufferedImage orig = ImageIO.read(sr.getImgURL());
                    int origWidth = orig.getWidth();
                    int origHeight = orig.getHeight();
                    double wr = (origWidth * 1.0) / maxImgWidth;
                    double hr = (origHeight * 1.0) / maxImgHeight;
                    double max = Math.max(wr, hr);
                    int newWidth = origWidth;
                    int newHeight = origHeight;
                    if (max > 1) {
                        newWidth = (int) (origWidth / max);
                        newHeight = (int) (origHeight / max);
                    }
                    BufferedImage img = null;
                    if (newWidth != origWidth || newHeight != origHeight) {
                        BufferedImage bimg = new BufferedImage(newWidth,
                                                    newHeight, orig.getType());
                        Graphics graphics = bimg.getGraphics();
                        graphics.drawImage(orig, 0, 0, newWidth,
                                            newHeight, null);
                        img = bimg;
                    } else {
                        img = orig;
                    }
                    String urlAsString = sr.getImgURL().toString();
                    int index = urlAsString.lastIndexOf('.');
                    URL newURL = sr.getImgURL();
                    if (index > 0) {
                        String type = sr.getImgURL().toString().substring(index + 1);
                        newURL = new URL("file:///" +
                                settingsDir.getAbsolutePath() +
                                UploadCheckerApp.FILE_SEPARATOR +
                                sr.getMovieID() + "." + type);
                        ImageIO.write(img, type, new File(
                                settingsDir.getAbsolutePath() +
                                UploadCheckerApp.FILE_SEPARATOR +
                                sr.getMovieID() + "." + type));
                    }
                    imgsCache.put(sr.getMovieID(), newURL);
                    count++;
                    
                    int selected = searchResList.getSelectedIndex();
                    if (selected < 0) continue;
                    DefaultListModel dlm =
                            (DefaultListModel) searchResList.getModel();
                    SearchResult r = (SearchResult) dlm.get(selected);
                    if (r.getMovieID().equals(sr.getMovieID())) {
                        BufferedImage image =
                                ImageIO.read(imgsCache.get(sr.getMovieID()));
                        setImgIcon(new ImageIcon(image));
                    }
                }
                movieSearchDialog.setTitle("Movie Search");
                //wt.dec();
                setMovieFrameState(IDLE);
            } catch (Exception e) {
            }
        }
    }
}