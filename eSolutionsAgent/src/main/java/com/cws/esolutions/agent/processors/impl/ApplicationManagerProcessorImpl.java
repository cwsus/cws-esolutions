/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
package com.cws.esolutions.agent.processors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.impl
 * File: ApplicationManagerProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.exec.DefaultExecuteResultHandler;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerRequest;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerResponse;
import com.cws.esolutions.agent.processors.exception.ApplicationManagerException;
import com.cws.esolutions.agent.processors.interfaces.IApplicationManagerProcessor;
/**
 * @see com.cws.esolutions.agent.processors.interfaces.IApplicationManagerProcessor
 */
public class ApplicationManagerProcessorImpl implements IApplicationManagerProcessor
{
    /**
     * @see com.cws.esolutions.agent.processors.interfaces.IApplicationManagerProcessor#installApplication(com.cws.esolutions.agent.processors.dto.ApplicationManagerRequest)
     */
    @Override
    public ApplicationManagerResponse installApplication(final ApplicationManagerRequest request) throws ApplicationManagerException
    {
        final String methodName = IApplicationManagerProcessor.CNAME + "#installApplication(final ApplicationManagerRequest request) throws ApplicationManagerException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagerRequest: {}", request);
        }

        BufferedWriter writer = null;
        ApplicationManagerResponse response = new ApplicationManagerResponse();

        final double version = request.getVersion();
        final DefaultExecutor executor = new DefaultExecutor();
        final String installerOptions = request.getInstallerOptions();
        final File installPath = FileUtils.getFile(request.getInstallPath());
        final ExecuteWatchdog watchdog = new ExecuteWatchdog(CONNECT_TIMEOUT * 1000);
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        final File packageInstaller = FileUtils.getFile(request.getPackageInstaller());

        if (DEBUG)
        {
            DEBUGGER.debug("double: {}", version);
            DEBUGGER.debug("DefaultExecutor: {}", executor);
            DEBUGGER.debug("String: {}", installerOptions);
            DEBUGGER.debug("File: {}", installPath);
            DEBUGGER.debug("ExecuteWatchdog: {}", watchdog);
            DEBUGGER.debug("DefaultExecuteResultHandler: {}", resultHandler);
            DEBUGGER.debug("File:{}", packageInstaller);
        }

        try
        {
            if (!(packageInstaller.canExecute()))
            {
                throw new ApplicationManagerException("Unable to execute package installer. Cannot continue.");
            }

            if (!(installPath.canWrite()) && (!(installPath.mkdirs())))
            {
                throw new ApplicationManagerException("Unable to create installation target. Cannot continue."); 
            }

            CommandLine command = CommandLine.parse(packageInstaller.getAbsolutePath());
            command.addArgument(installerOptions, false);
            command.addArgument(request.getPackageLocation(), false);

            if (DEBUG)
            {
                DEBUGGER.debug("CommandLine: {}", command);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            streamHandler.start();

            executor.setWatchdog(watchdog);
            executor.setStreamHandler(streamHandler);
            
            if (DEBUG)
            {
                DEBUGGER.debug("ExecuteStreamHandler: {}", streamHandler);
                DEBUGGER.debug("ExecuteWatchdog: {}", watchdog);
                DEBUGGER.debug("DefaultExecuteResultHandler: {}", resultHandler);
                DEBUGGER.debug("DefaultExecutor: {}", executor);
            }

            executor.execute(command, resultHandler);

            resultHandler.waitFor();
            int exitCode = resultHandler.getExitValue();

            writer = new BufferedWriter(new FileWriter(LOGS_DIRECTORY + "/" + request.getPackageName() + ".log"));
            writer.write(outputStream.toString());
            writer.flush();

            if (DEBUG)
            {
                DEBUGGER.debug("exitCode: {}", exitCode);
            }

            if (executor.isFailure(exitCode))
            {
                throw new ApplicationManagerException("Application installation failed: Result Code: " + exitCode);
            }

            response.setResponse(outputStream.toString());
            response.setRequestStatus(AgentStatus.SUCCESS);
        }
        catch (ExecuteException eex)
        {
            ERROR_RECORDER.error(eex.getMessage(), eex);

            throw new ApplicationManagerException(eex.getMessage(), eex);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new ApplicationManagerException(iox.getMessage(), iox);
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new ApplicationManagerException(ix.getMessage(), ix);
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
        
        return response;
    }
}
