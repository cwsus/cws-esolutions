/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
package com.cws.esolutions.utility.coreutils;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: NetworkUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.net.Socket;
import java.util.Arrays;
import org.xbill.DNS.Name;
import org.xbill.DNS.Type;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;
import org.xbill.DNS.Lookup;
import java.net.InetAddress;
import org.xbill.DNS.Record;
import java.security.Security;
import java.net.SocketException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import org.xbill.DNS.SimpleResolver;
import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import org.apache.logging.log4j.Logger;
import org.xbill.DNS.TextParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.utility.UtilityConstants;
import com.cws.esolutions.utility.exception.UtilityException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public final class NetworkUtils
{
    private static final String CRLF = "\r\n";
    private static final String TERMINATE_TELNET = "^]";
    private static final String CNAME = NetworkUtils.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(UtilityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(UtilityConstants.ERROR_LOGGER + CNAME);

    /**
     * Creates an telnet connection to a target host and port number. Silently
     * succeeds if no issues are encountered, if so, exceptions are logged and
     * re-thrown back to the requestor.
     *
     * If an exception is thrown during the <code>socket.close()</code> operation,
     * it is logged but NOT re-thrown. It's not re-thrown because it does not indicate
     * a connection failure (indeed, it means the connection succeeded) but it is
     * logged because continued failures to close the socket could result in target
     * system instability.
     * 
     * @param hostName - The target host to make the connection to
     * @param portNumber - The port number to attempt the connection on
     * @param timeout - The timeout for the connection
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized void executeTelnetRequest(final String hostName, final int portNumber, final int timeout) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeTelnetRequest(final String hostName, final int portNumber, final int timeout) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(hostName);
            DEBUGGER.debug("portNumber: {}", portNumber);
            DEBUGGER.debug("timeout: {}", timeout);
        }

        Socket socket = null;

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) == null)
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }

                InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                socket = new Socket();
                socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                socket.setSoLinger(false, 0);
                socket.setKeepAlive(false);
                socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

                if (!(socket.isConnected()))
                {
                    throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                }

                PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                pWriter.println(NetworkUtils.TERMINATE_TELNET + NetworkUtils.CRLF);

                pWriter.flush();
                pWriter.close();
            }
        }
        catch (final ConnectException cx)
        {
            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (final UnknownHostException ux)
        {
            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (final SocketException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (final IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            try
            {
                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (final IOException iox)
            {
                // log it - this could cause problems later on
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
    }

    /**
     * Creates an telnet connection to a target host and port number. Silently
     * succeeds if no issues are encountered, if so, exceptions are logged and
     * re-thrown back to the requestor.
     *
     * If an exception is thrown during the <code>socket.close()</code> operation,
     * it is logged but NOT re-thrown. It's not re-thrown because it does not indicate
     * a connection failure (indeed, it means the connection succeeded) but it is
     * logged because continued failures to close the socket could result in target
     * system instability.
     * 
     * @param hostName - The target host to make the connection to
     * @param portNumber - The port number to attempt the connection on
     * @param timeout - How long to wait for a connection to establish or a response from the target
     * @param object - The serializable object to send to the target
     * @return <code>Object</code> as output from the request
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized Object executeTcpRequest(final String hostName, final int portNumber, final int timeout, final Object object) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeTcpRequest(final String hostName, final int portNumber, final int timeout, final Object object) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(hostName);
            DEBUGGER.debug("portNumber: {}", portNumber);
            DEBUGGER.debug("timeout: {}", timeout);
            DEBUGGER.debug("object: {}", object);
        }

        Socket socket = null;
        Object resObject = null;

        try
        {
            synchronized(new Object())
            {
                if (StringUtils.isEmpty(InetAddress.getByName(hostName).toString()))
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }

                InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                socket = new Socket();
                socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                socket.setSoLinger(false, 0);
                socket.setKeepAlive(false);
                socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

                if (!(socket.isConnected()))
                {
                    throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                }

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                if (DEBUG)
                {
                    DEBUGGER.debug("ObjectOutputStream: {}", objectOut);
                }

                objectOut.writeObject(object);

                resObject = new ObjectInputStream(socket.getInputStream()).readObject();

                if (DEBUG)
                {
                    DEBUGGER.debug("resObject: {}", resObject);
                }

                PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                pWriter.println(NetworkUtils.TERMINATE_TELNET + NetworkUtils.CRLF);

                pWriter.flush();
                pWriter.close();
            }
        }
        catch (final ConnectException cx)
        {
            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (final UnknownHostException ux)
        {
            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (final SocketException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (final IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
        }
        catch (final ClassNotFoundException cnfx)
        {
            throw new UtilityException(cnfx.getMessage(), cnfx);
        }
        finally
        {
            try
            {
                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (final IOException iox)
            {
                // log it - this could cause problems later on
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return resObject;
    }

    /**
     * Performs a DNS lookup of a given name and type against a provided server
     * (or if no server is provided, the default system resolver).
     *
     * If an error occurs during the lookup, a <code>UtilityException</code> is
     * thrown containing the error response text.
     * 
     * @param resolverHost - The target host to use for resolution. Can be null, if not provided the
     * the default system resolver is used.
     * @param recordName - The DNS hostname/IP address to lookup.
     * @param recordType - The type of record to look up.
     * @param searchList - A search list to utilize if a short name is provided.
     * @return An ArrayList as output from the request
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized List<List<String>> executeDNSLookup(final String resolverHost, final String recordName, final String recordType, final String[] searchList) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeDNSLookup(final String resolverHost, final String recordName, final String recordType, final String[] searchList) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", resolverHost);
            DEBUGGER.debug("String: {}", recordName);
            DEBUGGER.debug("String: {}", recordType);
            DEBUGGER.debug("String: {}", (Object) searchList);
        }

        Lookup lookup = null;
        String responseName = null;
        String responseType = null;
        Record[] recordList = null;
        String responseAddress = null;
        SimpleResolver resolver = null;
        List<String> lookupData = null;
        List<List<String>> response = null;

        final String currentTimeout = Security.getProperty("networkaddress.cache.ttl");

        if (DEBUG)
        {
            DEBUGGER.debug("currentTimeout: {}", currentTimeout);
        }

        try
        {
            // no authorization required for service lookup
            Name name = Name.fromString(recordName);
            lookup = new Lookup(name, Type.value(recordType));

            if (DEBUG)
            {
                DEBUGGER.debug("Name: {}", name);
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            if (StringUtils.isNotEmpty(resolverHost))
            {
                resolver = new SimpleResolver(resolverHost);

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }
            }
            else
            {
                resolver = new SimpleResolver();

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }
            }

            lookup.setResolver(resolver);
            lookup.setCache(null);

            if (searchList != null)
            {
                lookup.setSearchPath(searchList);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            recordList = lookup.run();

            if (DEBUG)
            {
                if (recordList != null)
                {
                    for (Record dRecord : recordList)
                    {
                        DEBUGGER.debug("Record: {}", dRecord);
                    }
                }
            }

            if (lookup.getResult() != Lookup.SUCCESSFUL)
            {
                throw new UtilityException("An error occurred during the lookup. The response obtained is: " + lookup.getErrorString());
            }

            response = new ArrayList<List<String>>();

            if ((recordList == null) || (recordList.length == 0))
            {
                throw new UtilityException("No results were found for the provided information.");
            }

            switch (recordList.length)
            {
                case 1:
                    Record sRecord = recordList[0];

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Record: {}", sRecord);
                    }

                    responseAddress = sRecord.rdataToString();
                    responseName = sRecord.getName().toString();
                    responseType = Type.string(sRecord.getType());

                    lookupData = new ArrayList<String>(
                            Arrays.asList(
                                    responseAddress,
                                    responseName,
                                    responseType));
                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseAddress: {}", responseAddress);
                        DEBUGGER.debug("responseName: {}", responseName);
                        DEBUGGER.debug("responseType: {}", responseType);
                    }

                    response.add(lookupData);

                    break;
                default:
                    for (Record mRecord : recordList)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Record: {}", mRecord);
                        }

                        responseAddress = mRecord.rdataToString();
                        responseName = mRecord.getName().toString();
                        responseType = Type.string(mRecord.getType());

                        lookupData = new ArrayList<String>(
                                Arrays.asList(
                                        responseAddress,
                                        responseName,
                                        responseType));
                        if (DEBUG)
                        {
                            DEBUGGER.debug("responseAddress: {}", responseAddress);
                            DEBUGGER.debug("responseName: {}", responseName);
                            DEBUGGER.debug("responseType: {}", responseType);
                        }

                        response.add(lookupData);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("response: {}", response);
                        }
                    }

                    break;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("response: {}", response);
            }
        }
        catch (final TextParseException tpx)
        {
            ERROR_RECORDER.error(tpx.getMessage(), tpx);

            throw new UtilityException(tpx.getMessage(), tpx);
        }
        catch (final UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            throw new UtilityException(uhx.getMessage(), uhx);
        }
        finally
        {
            // reset java dns timeout
            try
            {
                Security.setProperty("networkaddress.cache.ttl", currentTimeout);
            }
            catch (final NullPointerException npx) {}
        }

        return response;
    }
}
