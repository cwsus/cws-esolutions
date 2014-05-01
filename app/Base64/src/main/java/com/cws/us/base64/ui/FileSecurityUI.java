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
import java.awt.Insets;

import org.slf4j.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.EventQueue;

import javax.swing.JButton;

import java.net.InetAddress;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.JTextField;

import org.slf4j.LoggerFactory;

import javax.swing.JFileChooser;
import javax.swing.BorderFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import javax.swing.WindowConstants;

import java.awt.event.MouseListener;
import java.net.UnknownHostException;
import java.awt.event.ActionListener;

import org.apache.log4j.helpers.Loader;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import javax.swing.UnsupportedLookAndFeelException;

import com.cws.us.base64.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.processors.impl.FileSecurityProcessorImpl;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
import com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class FileSecurityUI extends JFrame
{
    private JLabel jLabel = null;
    private JButton btnCancel = null;
    private JButton btnProcess = null;
    private JCheckBox chkEncrypt = null;
    private JCheckBox chkDecrypt = null;
    private JTextField txtSourceFile = null;
    private JCheckBox chkAddSignature = null;
    private JCheckBox chkVerifySignature = null;

    private static final long serialVersionUID = 8263218051002073731L;
    private static final String CNAME = FileSecurityUI.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public FileSecurityUI()
    {
        super();

        final String methodName = FileSecurityUI.CNAME + "()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.jLabel = new JLabel();
        this.btnProcess = new JButton();
        this.btnCancel = new JButton();
        this.chkEncrypt = new JCheckBox();
        this.chkDecrypt = new JCheckBox();
        this.txtSourceFile = new JTextField();
        this.chkAddSignature = new JCheckBox();
        this.chkVerifySignature = new JCheckBox();

        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setTitle("Find");

        this.jLabel.setText("Filename:");

        this.chkAddSignature.setText("Sign File");
        this.chkAddSignature.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkAddSignature.setMargin(new Insets(0, 0, 0, 0));

        this.chkVerifySignature.setText("Verify Signature");
        this.chkVerifySignature.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkVerifySignature.setMargin(new Insets(0, 0, 0, 0));

        this.chkEncrypt.setText("Encrypt File");
        this.chkEncrypt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkEncrypt.setMargin(new Insets(0, 0, 0, 0));

        this.chkDecrypt.setText("Decrypt File");
        this.chkDecrypt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkDecrypt.setMargin(new Insets(0, 0, 0, 0));

        this.btnProcess.setText("Process");
        this.btnProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                processFileOperation(event);
            }
        });

        this.btnCancel.setText("Cancel");
        this.btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                System.exit(0);
            }
        });

        this.txtSourceFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug(methodName);
                    DEBUGGER.debug("MouseEvent: ", event);
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showDialog(FileSecurityUI.this, "OK") == 0)
                {
                    FileSecurityUI.this.txtSourceFile.setText(fileChooser.getSelectedFile().toString());
                }
            }
        });

        GroupLayout layout = new GroupLayout(super.getContentPane());
        super.getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.LEADING)
                    .add(GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(this.jLabel)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(this.txtSourceFile, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(GroupLayout.LEADING)
                                    .add(this.chkEncrypt)
                                    .add(this.chkAddSignature))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(GroupLayout.LEADING)
                                    .add(this.chkVerifySignature)
                                    .add(this.chkDecrypt))))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(this.btnProcess, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(GroupLayout.TRAILING, this.btnCancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap()));

            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.LEADING)
                    .add(GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(this.jLabel)
                            .add(this.txtSourceFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(this.btnProcess))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                    .add(this.chkAddSignature)
                                    .add(this.chkVerifySignature))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                    .add(this.chkEncrypt)
                                    .add(this.chkDecrypt)))
                            .add(this.btnCancel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

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

            for (UIManager.LookAndFeelInfo installedLookAndFeel : installedLookAndFeels)
            {
                if (StringUtils.equals("Nimbus", installedLookAndFeel.getName()))
                {
                    UIManager.setLookAndFeel(installedLookAndFeel.getClassName());
                    break;
                }
            }

            UserAccount userAccount = new UserAccount();
            userAccount.setUsername(System.getProperty("user.name"));

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
            reqInfo.setHostName(InetAddress.getLocalHost().getHostName());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);
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
        finally
        {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run()
                {
                    new FileSecurityUI().setVisible(true);
                }
            });
        }
    }

    private final void processFileOperation(final ActionEvent event)
    {
        final String methodName = FileSecurityUI.CNAME + "#processFileOperation(final ActionEvent event)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ActionEvent: {}", event);
        }

        final IFileSecurityProcessor processor = new FileSecurityProcessorImpl();

        FileSecurityRequest request = new FileSecurityRequest();
        request.setAppName(CNAME);
        request.setApplicationId(CNAME);
        request.setHostInfo(null);
        request.setUserAccount(null);
        request.setRequestFile(FileUtils.getFile(this.txtSourceFile.getText()));

        if (DEBUG)
        {
            DEBUGGER.debug("FileSecurityRequest: {}", request);
        }

        try
        {
            FileSecurityResponse response = processor.encryptFile(request);

            if (DEBUG)
            {
                DEBUGGER.debug("FileSecurityResponse: {}", response);
            }
        }
        catch (FileSecurityException fsx)
        {
            ERROR_RECORDER.error(fsx.getMessage(), fsx);
        }
    }
}
