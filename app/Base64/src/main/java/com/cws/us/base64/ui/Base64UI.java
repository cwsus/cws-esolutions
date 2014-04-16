/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.us.base64.ui;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.utils
 * File: PasswordUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.EventQueue;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.JTextArea;
import org.slf4j.LoggerFactory;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import javax.swing.WindowConstants;
import java.awt.event.ActionListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import javax.swing.UnsupportedLookAndFeelException;

import com.cws.us.base64.Constants;
import com.cws.esolutions.security.utils.PasswordUtils;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class Base64UI extends JFrame implements ActionListener
{
    private JPanel jPanel2 = null;
    private JPanel jPanel3 = null;
    private JButton btnExit = null;
    private JButton btnDecryptText = null;
    private JButton btnEncryptText = null;
    private JTextArea txtSourceData = null;
    private JTextArea txtTargetData = null;
    private JScrollPane jScrollPane1 = null;
    private JScrollPane jScrollPane2 = null;

    private static final long serialVersionUID = 8263218051002073731L;
    private static final String CNAME = Base64UI.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public Base64UI()
    {
        super();

        final String methodName = Base64UI.CNAME + "()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.jPanel2 = new JPanel();
        this.jPanel3 = new JPanel();
        this.btnExit = new JButton();
        this.btnDecryptText = new JButton();
        this.btnEncryptText = new JButton();
        this.txtSourceData = new JTextArea();
        this.txtTargetData = new JTextArea();
        this.jScrollPane2 = new JScrollPane();
        this.jScrollPane1 = new JScrollPane();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Base64 Encoder/Decoder");

        this.jPanel2.setBorder(BorderFactory.createTitledBorder("Source Data"));

        this.jScrollPane1.setViewportView(this.txtSourceData);

        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(this.jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(this.jScrollPane1, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.btnExit.setText("Exit");
        this.btnExit.addActionListener(this);

        this.btnDecryptText.setText("Decrypt");
        this.btnDecryptText.addActionListener(this);

        this.jPanel3.setBorder(BorderFactory.createTitledBorder("Target Data"));

        this.jScrollPane2.setViewportView(this.txtTargetData);

        GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(this.jScrollPane2, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(this.jScrollPane2, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.btnEncryptText.setText("Encrypt");
        this.btnEncryptText.addActionListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(GroupLayout.TRAILING, this.jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GroupLayout.TRAILING, this.jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(this.btnEncryptText)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(this.btnDecryptText, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(this.btnExit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(this.jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(this.jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(this.btnExit)
                    .add(this.btnDecryptText)
                    .add(this.btnEncryptText))
                .addContainerGap())
        );

        this.jPanel2.getAccessibleContext().setAccessibleName("Source Data");
        this.jPanel3.getAccessibleContext().setAccessibleName("Target Data");

        this.setIconImage(new ImageIcon(this.getClass().getResource("/resources/img/icon.png")).getImage());

        this.pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[])
    {
        // Load logging
        try
        {
            DOMConfigurator.configure(Loader.getResource("/resources/logging/logging.xml"));
        }
        catch (NullPointerException npx)
        {
            // don't do anything
        }

        try
        {
            UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            for (int idx = 0; idx < installedLookAndFeels.length; idx++)
            {
                if ("Nimbus".equals(installedLookAndFeels[idx].getName()))
                {
                    UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());

                    break;
                }
            }
        }
        catch (UnsupportedLookAndFeelException ulfx)
        {
            ERROR_RECORDER.error(ulfx.getMessage(), ulfx);
        }
        catch (ClassNotFoundException cnfx)
        {
            ERROR_RECORDER.error(cnfx.getMessage(), cnfx);
        }
        catch (InstantiationException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);
        }
        catch (IllegalAccessException iax)
        {
            ERROR_RECORDER.error(iax.getMessage(), iax);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                new Base64UI().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(final ActionEvent aEvent)
    {
        try
        {
            if (aEvent.getSource() == this.btnEncryptText)
            {
                this.txtTargetData.setText(PasswordUtils.base64Encode(this.txtSourceData.getText()));
            }
            else if (aEvent.getSource() == this.btnDecryptText)
            {
                this.txtTargetData.setText(PasswordUtils.base64Decode(this.txtSourceData.getText()));
            }
            else if (aEvent.getSource() == this.btnExit)
            {
                System.exit(0);
            }
        }
        catch (SecurityException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);
        }
    }
}
