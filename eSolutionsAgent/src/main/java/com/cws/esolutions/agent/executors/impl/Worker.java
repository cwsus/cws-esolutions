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
package com.cws.esolutions.agent.executors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.executors.impl
 * File: Worker.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import com.cws.esolutions.agent.Constants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class Worker extends Thread
{
    private Integer exitValue = -1;
    private ProcessBuilder pBuilder = null;
    private StringBuffer errorStream = null;
    private StringBuffer outputStream = null;

    private static final String CNAME = Worker.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + Worker.CNAME);

    public Worker(final ProcessBuilder value)
    {
        final String methodName = Worker.CNAME + "#Worker(final ProcessBuilder value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pBuilder = value;
    }

    public Integer getExitValue()
    {
        final String methodName = Worker.CNAME + "#getExitValue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.exitValue);
        }

        return this.exitValue;
    }

    public StringBuffer getOutputStream()
    {
        final String methodName = Worker.CNAME + "#getOutputStream()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.outputStream);
        }

        return this.outputStream;
    }

    public StringBuffer getErrorStream()
    {
        final String methodName = Worker.CNAME + "#getOutputStream()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.errorStream);
        }

        return this.errorStream;
    }

    @Override
    public void run()
    {
        final String methodName = Worker.CNAME + "#run()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        int x = 0;
        int y = 0;
        Process process = null;
        StringBuffer oBuffer = new StringBuffer();
        StringBuffer eBuffer = new StringBuffer();

        try
        {
            process = this.pBuilder.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Process:" , process);
            }

            while ((x = IOUtils.read(process.getInputStream(), new byte[1024])) != -1)
            {
                oBuffer.append((char) x);
            }

            while ((x = IOUtils.read(process.getErrorStream(), new byte[1024])) != -1)
            {
                eBuffer.append((char) y);
            }

            this.exitValue = process.waitFor();

            if (this.exitValue != null)
            {
                this.outputStream = oBuffer;
                this.errorStream = eBuffer;
            }
            else
            {
                throw new InterruptedException("No return code was obtained");
            }
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
        }
        finally
        {
            if (!(process == null))
            {
                process.destroy();
            }
        }
    }
}