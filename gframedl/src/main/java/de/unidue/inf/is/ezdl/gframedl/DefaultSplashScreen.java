/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.EzPasswordField;
import de.unidue.inf.is.ezdl.gframedl.components.GroupBox;
import de.unidue.inf.is.ezdl.gframedl.events.OnlineToggle;



final class DefaultSplashScreen implements SplashScreen {

    private class EventHandler extends WindowAdapter implements ActionListener, ItemListener, KeyListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == getLoginButton()) {
                login();
            }
            else if (e.getSource() == getCancelButton()) {
                closeApplication();
            }
        }


        @Override
        public void itemStateChanged(ItemEvent e) {
        }


        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getSource() == getLoginField()) {
                handleKeyPressedLogin(e);
            }
            if (e.getSource() == getTextPwd()) {
                handleKeyPressedPassword(e);
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
        }


        @Override
        public void keyTyped(KeyEvent e) {
        }


        @Override
        public void windowClosing(WindowEvent e) {
            if (e.getSource() == this) {
                closeApplication();
            }
        }

    }


    private static final long serialVersionUID = -5565252746742327178L;

    private static final Logger logger = Logger.getLogger(DefaultSplashScreen.class);

    private static final Color FOREGROUND_COLOR = new Color(0, 13, 60);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR_DEBUG = new Color(164, 40, 40);

    private JFrame frame;

    private Config conf = Config.getInstance();
    private I18nSupport i18n = I18nSupport.getInstance();

    private Application app;

    private JButton buttonCancel;
    private JButton buttonLogin;
    private JPanel buttonPanel;
    private JPanel contentPanel;

    private EventHandler eventHandler = new EventHandler();

    // components that have text should be put into this hashmap so that they
    // can be revalidated when the language changes
    private Map<JComponent, String> langChangeComps = new HashMap<JComponent, String>();
    private JLabel ezdlLabel;
    private JLabel languageLabel;
    private JComboBox languages;
    private JPanel loginAndChoicePanel;
    private JPanel loginDataPanel;
    private JTextField loginField;
    private JPanel loginPanel;
    private JPanel logoPanel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JCheckBox rememberingBox;
    private JPanel rememberPanel;
    private JLabel usernameLabel;
    private boolean remembering;
    private Color backgroundColor = BACKGROUND_COLOR;


    public DefaultSplashScreen(Application app, GraphicsConfiguration graphicsConfiguration, SessionType sessionType) {
        super();
        setSessionType(sessionType);
        this.frame = new JFrame(graphicsConfiguration);
        this.app = app;
        initialize();
        setListeners();
    }


    private void closeApplication() {
        User user = new User();
        user.setLogin(getLoginField().getText());
        user.setPwd(new String(getTextPwd().getPassword()));
        savePreferences(user);

        frame.dispose();
        System.exit(0);
    }


    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            SpringLayout layout = new SpringLayout();
            buttonPanel = new JPanel(layout);
            buttonPanel.setPreferredSize(new Dimension(240, 30));

            buttonPanel.setOpaque(false);

            buttonPanel.add(getCancelButton());
            buttonPanel.add(getLoginButton());

            layout.putConstraint(SpringLayout.EAST, getLoginButton(), -11, SpringLayout.EAST, buttonPanel);
            layout.putConstraint(SpringLayout.SOUTH, getLoginButton(), -10, SpringLayout.SOUTH, buttonPanel);
            layout.putConstraint(SpringLayout.EAST, getCancelButton(), -4, SpringLayout.WEST, getLoginButton());
            layout.putConstraint(SpringLayout.SOUTH, getCancelButton(), -10, SpringLayout.SOUTH, buttonPanel);
        }
        return buttonPanel;
    }


    private String getDefaultPassword() {
        return conf.getUserProperty(PASSWORD_KEY, "visitor");
    }


    private String getDefaultUsername() {
        return conf.getUserProperty(USERNAME_KEY, "visitor");
    }


    private JLabel getEzdlLabel() {
        if (ezdlLabel == null) {
            ezdlLabel = new JLabel();
            ezdlLabel.setOpaque(false);
            // ezdlLabel.setText("<html>Easy Access to <br>Digital Libraries");
            ezdlLabel.setForeground(FOREGROUND_COLOR);
            ezdlLabel.setFont(ezdlLabel.getFont().deriveFont(Font.BOLD));
            ezdlLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            ezdlLabel.setVerticalTextPosition(SwingConstants.CENTER);
            ezdlLabel.setIcon(new ImageIcon(Images.LOGO_EZDL.getImage()));
        }
        return ezdlLabel;
    }


    private JButton getCancelButton() {
        if (buttonCancel == null) {
            buttonCancel = new JButton();
            buttonCancel.setIcon(Icons.CANCEL_ACTION.get16x16());
            buttonCancel.setText(i18n.getLocString("ezdl.controls.cancel"));
            registerComponent(buttonCancel, "ezdl.controls.cancel");
        }
        return buttonCancel;
    }


    private JButton getLoginButton() {
        if (buttonLogin == null) {
            buttonLogin = new JButton();
            buttonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonLogin.setIcon(Icons.OK_ACTION.get16x16());
            buttonLogin.setText(i18n.getLocString("ezdl.controls.login"));
            registerComponent(buttonLogin, "ezdl.controls.login");
        }
        return buttonLogin;
    }


    private JLabel getLanguageLabel() {
        if (languageLabel == null) {
            languageLabel = new JLabel();
            languageLabel.setText(i18n.getLocString("ezdl.splash.language") + ":");
            registerComponent(languageLabel, "ezdl.splash.language");
            languageLabel.setForeground(FOREGROUND_COLOR);
            languageLabel.setFont(languageLabel.getFont().deriveFont(Font.PLAIN));
            languageLabel.setLabelFor(languages);
        }
        return languageLabel;
    }


    private JComboBox getLanguageChooser() {
        if (languages == null) {

            Map<String, Locale> localesMap = I18nSupport.getInstance().getLocalesMap();
            languages = new JComboBox(localesMap.values().toArray());
            languages.setRenderer(new DefaultListCellRenderer() {

                private static final long serialVersionUID = 1L;


                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                boolean cellHasFocus) {
                    Locale displayingLocale = (Locale) value;
                    Locale currentLocale = i18n.getLocale();

                    String name = displayingLocale.getDisplayName(currentLocale);
                    return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                }
            });

            languages.setEditable(false);

            String lang;
            if (conf.getUserPropertyAsBoolean(REMEMBERING_KEY, false)) {
                lang = conf.getUserProperty(LANGUAGE_KEY);
            }
            else {
                lang = i18n.getLocale().getLanguage();
            }

            languages.setSelectedItem(localesMap.get(lang));

            languages.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    i18n.setLocale((Locale) languages.getSelectedItem());
                    if (conf.getUserPropertyAsBoolean(REMEMBERING_KEY, false)) {
                        conf.setUserProperty(LANGUAGE_KEY, ((Locale) languages.getSelectedItem()).getLanguage());
                    }
                    updateLocale();
                }
            });
        }
        return languages;

    }


    private JPanel getLoginAndChoicePanel() {
        if (loginAndChoicePanel == null) {
            loginAndChoicePanel = new JPanel(new BorderLayout());
            loginAndChoicePanel.add(getLoginDataPanel(), BorderLayout.CENTER);
            loginAndChoicePanel.setBackground(backgroundColor);
        }
        return loginAndChoicePanel;

    }


    private JPanel getLoginDataPanel() {
        if (loginDataPanel == null) {
            loginDataPanel = new GroupBox(i18n.getLocString("ezdl.splash.border"));

            loginDataPanel.setPreferredSize(new Dimension(430, 120));

            SpringLayout layout = new SpringLayout();
            loginDataPanel.setLayout(layout);

            loginDataPanel.setBackground(backgroundColor);

            getLanguageChooser().setPreferredSize(new Dimension(300, 25));
            getLanguageChooser().setMaximumSize(new Dimension(300, 25));
            getLanguageChooser().setMinimumSize(new Dimension(300, 25));

            getLoginField().setPreferredSize(new Dimension(300, 25));
            getLoginField().setMaximumSize(new Dimension(300, 25));
            getLoginField().setMinimumSize(new Dimension(300, 25));

            getTextPwd().setPreferredSize(new Dimension(300, 25));
            getTextPwd().setMaximumSize(new Dimension(300, 25));
            getTextPwd().setMinimumSize(new Dimension(300, 25));

            getLoginDataPanel().add(getLoginField());
            getLoginDataPanel().add(getUsernameLabel());
            getLoginDataPanel().add(getTextPwd());
            getLoginDataPanel().add(getPasswordLabel());
            getLoginDataPanel().add(getLanguageChooser());
            getLoginDataPanel().add(getLanguageLabel());

            layout.putConstraint(SpringLayout.NORTH, getUsernameLabel(), 10, SpringLayout.NORTH, getLoginDataPanel());
            layout.putConstraint(SpringLayout.WEST, getLoginField(), -310, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getLoginField(), -5, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getUsernameLabel(), -8, SpringLayout.WEST, getLoginField());
            layout.putConstraint(SpringLayout.NORTH, getLoginField(), 5, SpringLayout.NORTH, getLoginDataPanel());

            layout.putConstraint(SpringLayout.NORTH, getPasswordLabel(), 15, SpringLayout.SOUTH, getUsernameLabel());
            layout.putConstraint(SpringLayout.NORTH, getTextPwd(), 5, SpringLayout.SOUTH, getLoginField());
            layout.putConstraint(SpringLayout.WEST, getTextPwd(), -310, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getTextPwd(), -5, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getPasswordLabel(), -8, SpringLayout.WEST, getTextPwd());

            layout.putConstraint(SpringLayout.NORTH, getLanguageLabel(), 15, SpringLayout.SOUTH, getPasswordLabel());

            if (SystemUtils.OS == OperatingSystem.MAC_OS) {
                layout.putConstraint(SpringLayout.NORTH, getLanguageChooser(), 8, SpringLayout.SOUTH, getTextPwd());
            }
            else {
                layout.putConstraint(SpringLayout.NORTH, getLanguageChooser(), 5, SpringLayout.SOUTH, getTextPwd());
            }

            layout.putConstraint(SpringLayout.WEST, getLanguageChooser(), -310, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getLanguageChooser(), -5, SpringLayout.EAST, getLoginDataPanel());
            layout.putConstraint(SpringLayout.EAST, getLanguageLabel(), -8, SpringLayout.WEST, getLanguageChooser());
        }
        return loginDataPanel;
    }


    private JTextField getLoginField() {
        if (loginField == null) {
            loginField = new JTextField();
            loginField.setPreferredSize(new Dimension(200, 25));
            loginField.setText(getDefaultUsername());
        }
        return loginField;
    }


    private JPanel getLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new JPanel();
            loginPanel.setLayout(new BorderLayout());
            loginPanel.setPreferredSize(new Dimension(430, 45));
            loginPanel.setBackground(backgroundColor);
            loginPanel.add(getButtonPanel(), BorderLayout.EAST);
            loginPanel.add(getRememberPanel(), BorderLayout.WEST);
        }
        return loginPanel;
    }


    private JPanel getLogoPanel() {
        if (logoPanel == null) {

            SpringLayout layout = new SpringLayout();
            logoPanel = new JPanel(layout);
            logoPanel.setPreferredSize(new Dimension(430, 95));
            logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            logoPanel.setBackground(backgroundColor);

            logoPanel.add(getEzdlLabel(), SpringLayout.WEST);

            layout.putConstraint(SpringLayout.WEST, getEzdlLabel(), 35, SpringLayout.WEST, logoPanel);
            layout.putConstraint(SpringLayout.NORTH, getEzdlLabel(), 20, SpringLayout.NORTH, logoPanel);
        }
        return logoPanel;
    }


    private JLabel getPasswordLabel() {
        if (passwordLabel == null) {
            passwordLabel = new JLabel();
            passwordLabel.setText(i18n.getLocString("ezdl.splash.password") + ":");
            registerComponent(passwordLabel, "ezdl.splash.password");
            passwordLabel.setForeground(FOREGROUND_COLOR);
            passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.PLAIN));
            passwordLabel.setLabelFor(passwordField);
        }
        return passwordLabel;
    }


    private JCheckBox getRememberingBox() {
        if (rememberingBox == null) {
            rememberingBox = new JCheckBox();
            rememberingBox.setText(i18n.getLocString("ezdl.splash.remember"));
            registerComponent(rememberingBox, "ezdl.splash.remember");
            rememberingBox.setOpaque(false);
            rememberingBox.setSelected(conf.getUserPropertyAsBoolean(REMEMBERING_KEY, false));

            rememberingBox.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    setRemembering(getRememberingBox().isSelected());
                }
            });
        }
        return rememberingBox;
    }


    private JPanel getRememberPanel() {
        if (rememberPanel == null) {
            SpringLayout layout = new SpringLayout();
            rememberPanel = new JPanel(layout);
            rememberPanel.setOpaque(false);
            rememberPanel.setPreferredSize(new Dimension(200, 45));
            rememberPanel.add(getRememberingBox());

            layout.putConstraint(SpringLayout.WEST, getRememberingBox(), 20, SpringLayout.WEST, rememberPanel);
            layout.putConstraint(SpringLayout.SOUTH, getRememberingBox(), -15, SpringLayout.SOUTH, rememberPanel);

        }
        return rememberPanel;
    }


    private JPanel getSplashScreenContentPane() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            c.weighty = 0;
            c.fill = GridBagConstraints.NONE;
            contentPanel.add(getLogoPanel(), c);
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            contentPanel.add(getLoginAndChoicePanel(), c);
            c.gridx = 0;
            c.gridy = 2;
            c.weightx = 0;
            c.weighty = 0;
            c.fill = GridBagConstraints.NONE;
            contentPanel.add(getLoginPanel(), c);
            contentPanel.setBackground(backgroundColor);
            contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        return contentPanel;
    }


    private JPasswordField getTextPwd() {
        if (passwordField == null) {
            passwordField = new EzPasswordField();
            passwordField.setText(getDefaultPassword());
            passwordField.setPreferredSize(new Dimension(200, 25));
        }
        return passwordField;
    }


    private JLabel getUsernameLabel() {
        if (usernameLabel == null) {
            usernameLabel = new JLabel();
            usernameLabel.setForeground(FOREGROUND_COLOR);
            usernameLabel.setText(i18n.getLocString("ezdl.splash.login") + ":");
            usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.PLAIN));
            usernameLabel.setLabelFor(loginField);
            registerComponent(usernameLabel, "ezdl.splash.login");
        }
        return usernameLabel;
    }


    private void handleKeyPressedLogin(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            getCancelButton().doClick(50);
        }
        else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            FocusManager.getCurrentManager().focusNextComponent(keyEvent.getComponent());
            keyEvent.consume();
        }
        return;
    }


    private void handleKeyPressedPassword(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            getCancelButton().doClick(50);
        }
        else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            getLoginButton().doClick();
            keyEvent.consume();
        }
    }


    private void setListeners() {
        frame.addWindowListener(eventHandler);
        getLoginButton().addActionListener(eventHandler);
        getCancelButton().addActionListener(eventHandler);
        getLoginField().addKeyListener(eventHandler);
        getTextPwd().addKeyListener(eventHandler);
    }


    private void initialize() {
        frame.setSize(432, 300);
        frame.setTitle("ezDL");
        Image logo = Images.LOGO_EZDL_APP_ICON.getImage();
        frame.setIconImages(Arrays.asList(logo.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(128, 128, Image.SCALE_SMOOTH)));
        if (!frame.isDisplayable()) {
            frame.setUndecorated(true);
        }
        frame.setContentPane(getSplashScreenContentPane());

        frame.setBackground(backgroundColor);
        frame.getRootPane().setDefaultButton(getLoginButton());
        updateLocale();
    }


    @Override
    public void reset() {
        initSplashScreen();
        loginAndChoicePanel.removeAll();
        loginAndChoicePanel = null;
        contentPanel.removeAll();
        contentPanel = null;
        initialize();
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }


    private boolean isRemembering() {
        return remembering;
    }


    private void login() {
        User user = new User();
        user.setLogin(getLoginField().getText());
        user.setPwd(new String(getTextPwd().getPassword()));
        app.setUserInfo(user);
        savePreferences(user);

        app.setVisible(false);
        frame.setVisible(false);

        Dispatcher.postEvent(new OnlineToggle(this, true));
    }


    private void savePreferences(User user) {
        if (isRemembering()) {
            conf.setUserProperty(USERNAME_KEY, user.getLogin());
            conf.setUserProperty(PASSWORD_KEY, user.getPwd());
            conf.setUserProperty(LANGUAGE_KEY, i18n.getLocale().getLanguage());
        }
        conf.setUserProperty(REMEMBERING_KEY, String.valueOf(isRemembering()));

        conf.writeUserPreferences();
        conf.refreshProperties();
    }


    private void registerComponent(JComponent comp, String property) {
        langChangeComps.put(comp, property);
    }


    private void setRemembering(boolean remembering) {
        this.remembering = remembering;
    }


    private void updateLocale() {
        for (JComponent comp : langChangeComps.keySet()) {
            try {
                Method setText = comp.getClass().getMethod("setText", String.class);
                setText.invoke(comp, i18n.getLocString(langChangeComps.get(comp)));
                int mnemonicPos = i18n.getMnemonicPos(langChangeComps.get(comp));
                if (comp instanceof JButton) {
                    ((JButton) comp).setMnemonic(((JButton) comp).getText().charAt(mnemonicPos));
                }
                else if (comp instanceof JLabel) {
                    ((JLabel) comp).setDisplayedMnemonic(((JLabel) comp).getText().charAt(mnemonicPos));
                }
                else if (comp instanceof JCheckBox) {
                    ((JCheckBox) comp).setMnemonic(((JCheckBox) comp).getText().charAt(mnemonicPos));
                }
            }
            catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
            }
        }
    }


    private void initSplashScreen() {
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void initSplashScreen(String user, String pass) {
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });

        frame.setLocationRelativeTo(null);
        getTextPwd().setText(pass);
        getLoginField().setText(user);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                login();
            }
        });

        frame.setVisible(true);
    }


    @Override
    public void showSplash() {
        initSplashScreen();
    }


    @Override
    public void showSplash(String user, String pass) {
        initSplashScreen(user, pass);
    }


    @Override
    public void close() {
        frame.dispose();
    }


    @Override
    public boolean isSplashVisible() {
        return frame.isVisible();
    }


    @Override
    public Window getWindow() {
        return frame;
    }


    private void setSessionType(SessionType sessionType) {
        switch (sessionType) {
            case DEBUG: {
                backgroundColor = BACKGROUND_COLOR_DEBUG;
                break;

            }
            default: {
                backgroundColor = BACKGROUND_COLOR;
            }
        }
    }

}
