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
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.agent.processors.exception.FileManagerException;
import com.cws.esolutions.agent.processors.interfaces.IFileManagerProcessor;
/**
 * @see com.cws.esolutions.agent.processors.interfaces.IFileManagerProcessor
 */
public class FileManagerProcessorImpl implements IFileManagerProcessor
{
    @Override
    public FileManagerResponse retrieveFile(final FileManagerRequest request) throws FileManagerException
    {
        final String methodName = IFileManagerProcessor.CNAME + "#retrieveFile(final ApplicationManagerRequest request) throws FileManagerException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileManagerRequest: {}", request);
        }

        FileManagerResponse response = new FileManagerResponse();

        try
        {
            if (!(FileUtils.getFile(request.getRequestFile()).canRead()))
            {
                throw new FileManagerException("Unable to read the requested file.");
            }
                
            if (FileUtils.getFile(request.getRequestFile()).isDirectory())
            {
                // list
                File[] fileList = FileUtils.getFile(request.getRequestFile()).listFiles();

                if ((fileList != null) && (fileList.length != 0))
                {
                    List<String> fileData = new ArrayList<>();

                    for (File file : fileList)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("File: {}", file);
                        }

                        fileData.add(file.getName());
                    }

                    response.setDirListing(fileData);
                    response.setFilePath(request.getRequestFile());
                    response.setRequestStatus(AgentStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(AgentStatus.FAILURE);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("FileManagerResponse: {}", response);
                }
            }
            else
            {
                // file
                File retrievableFile = FileUtils.getFile(request.getRequestFile());

                if (DEBUG)
                {
                    DEBUGGER.debug("File: {}", retrievableFile);
                }

                if ((retrievableFile.exists()) && (retrievableFile.canRead()))
                {
                    byte[] fileBytes = FileUtils.readFileToByteArray(retrievableFile);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("File data: {}", fileBytes);
                    }

                    response.setChecksum(FileUtils.checksumCRC32(retrievableFile));
                    response.setFileData(fileBytes);
                    response.setFileName(retrievableFile.getName());
                    response.setFilePath(retrievableFile.getPath());
                    response.setRequestStatus(AgentStatus.SUCCESS);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("FileManagerResponse: {}", response);
                    }
                }
                else
                {
                    response.setRequestStatus(AgentStatus.FAILURE);
                }
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
            
            throw new FileManagerException(iox.getMessage(), iox);
        }

        return response;
    }

    @Override
    public FileManagerResponse deployFile(final FileManagerRequest request) throws FileManagerException
    {
        final String methodName = IFileManagerProcessor.CNAME + "#deployFile(final FileManagerRequest request) throws FileManagerException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileManagerRequest: {}", request);
        }

        FileManagerResponse response = new FileManagerResponse();

        try
        {
            boolean isSourceList = ((request.getSourceFiles() != null) && (request.getSourceFiles().size() != 0));
            boolean isTargetList = ((request.getTargetFiles() != null) && (request.getTargetFiles().size() != 0));
            boolean sourceMatchesTarget = request.getSourceFiles().size() == request.getTargetFiles().size();

            if (DEBUG)
            {
                DEBUGGER.debug("isSourceList: {}", isSourceList);
                DEBUGGER.debug("isTargetList: {}", isTargetList);
                DEBUGGER.debug("sourceMatchesTarget: {}", sourceMatchesTarget);
            }

            if ((isSourceList) && (isTargetList))
            {
                List<String> failedFiles = new ArrayList<>();

                for (int x = 0; x < request.getSourceFiles().size(); x++)
                {
                    byte[] sourceFile = request.getSourceFiles().get(x);
                    String targetFile = FileUtils.getFile(request.getTargetFiles().get(x)).getName();
                    String targetPath = FileUtils.getFile(request.getTargetFiles().get(x)).getParent();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("sourceFile: {}", sourceFile);
                        DEBUGGER.debug("targetPath: {}", targetPath);
                        DEBUGGER.debug("targetFile: {}", targetFile);
                    }

                    boolean canWrite = (FileUtils.getFile(targetPath).exists()) ? FileUtils.getFile(targetPath).canWrite() : FileUtils.getFile(targetPath).mkdirs();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("canWrite: {}", canWrite);
                    }

                    if (canWrite)
                    {
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(targetPath, targetFile), sourceFile);

                        if (!(FileUtils.sizeOf(FileUtils.getFile(targetPath, targetFile)) == request.getSourceFiles().get(x).length))
                        {
                            ERROR_RECORDER.error("Failed to properly write file " + targetFile + " to path " + targetPath);

                            FileUtils.deleteQuietly(FileUtils.getFile(targetPath, targetFile));

                            failedFiles.add(targetFile);
                        }
                    }
                    else
                    {
                        // can't write
                        throw new FileManagerException("Cannot write to provided directory. Possible permissions problem.");
                    }
                }

                if (failedFiles.size() != 0)
                {
                    response.setRequestStatus(AgentStatus.FAILURE);
                }
                else
                {
                    response.setRequestStatus(AgentStatus.SUCCESS);
                }
            }
            else
            {
                // only one file
                byte[] sourceFile = request.getFileData();
                // String targetPath = request.getFilePath();
                // String targetFile = request.getFileName();
                String targetPath = null;
                String targetFile = null;

                if (DEBUG)
                {
                    DEBUGGER.debug("sourceFile: {}", sourceFile);
                    DEBUGGER.debug("targetPath: {}", targetPath);
                    DEBUGGER.debug("targetFile: {}", targetFile);
                }

                boolean canWrite = (FileUtils.getFile(targetPath).exists()) ? FileUtils.getFile(targetPath).canWrite() : FileUtils.getFile(targetPath).mkdirs();

                if (DEBUG)
                {
                    DEBUGGER.debug("canWrite: {}", canWrite);
                }

                if (canWrite)
                {
                    FileUtils.writeByteArrayToFile(FileUtils.getFile(targetPath, targetFile), sourceFile);

                    if (FileUtils.sizeOf(FileUtils.getFile(targetPath, targetFile)) == sourceFile.length)
                    {
                        // match
                        response.setRequestStatus(AgentStatus.SUCCESS);
                    }
                    else
                    {
                        FileUtils.deleteQuietly(FileUtils.getFile(targetPath, targetFile));

                        throw new FileManagerException("Unable to complete file deployment, written content does NOT match source.");
                    }
                }
                else
                {
                    throw new FileManagerException("Cannot write to provided directory. Possible permissions problem.");
                }
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new FileManagerException(iox.getMessage(), iox);
        }

        return response;
    }

    @Override
    public FileManagerResponse deleteFile(final FileManagerRequest request) throws FileManagerException
    {
        final String methodName = IFileManagerProcessor.CNAME + "#deleteFile(final FileManagerRequest request) throws FileManagerException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileManagerRequest: {}", request);
        }

        FileManagerResponse response = new FileManagerResponse();

        boolean isTargetList = ((request.getTargetFiles() != null) && (request.getTargetFiles().size() != 0));

        if (DEBUG)
        {
            DEBUGGER.debug("isTargetList: {}", isTargetList);
        }

        if ((request.getTargetFiles() != null) && (request.getTargetFiles().size() != 0))
        {
            List<String> failedFiles = new ArrayList<>();

            for (String target : request.getTargetFiles())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("target: {}", target);
                }

                String targetPath = FileUtils.getFile(target).getParent();
                String targetFile = FileUtils.getFile(target).getName();

                if (DEBUG)
                {
                    DEBUGGER.debug("targetPath: {}", targetPath);
                    DEBUGGER.debug("targetFile: {}", targetFile);
                }

                if ((FileUtils.getFile(targetPath).canWrite()) && (FileUtils.getFile(targetPath, targetFile).exists()))
                {
                    boolean isDeleted = FileUtils.deleteQuietly(FileUtils.getFile(targetPath, targetFile));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isDeleted: {}", isDeleted);
                    }

                    if (!(isDeleted))
                    {
                        failedFiles.add(targetFile);
                    }
                }
                else
                {
                    failedFiles.add(targetFile);
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("failedFiles: {}", failedFiles);
            }

            if (failedFiles.size() != 0)
            {
                response.setRequestStatus(AgentStatus.FAILURE);
            }
            else
            {
                response.setRequestStatus(AgentStatus.SUCCESS);
            }
        }
        else
        {
            if ((FileUtils.getFile(request.getRequestFile()).canWrite()) && (FileUtils.getFile(request.getRequestFile()).canWrite()))
            {
                boolean isFileDeleted = FileUtils.deleteQuietly(FileUtils.getFile(request.getRequestFile()));

                if (DEBUG)
                {
                    DEBUGGER.debug("isFileDeleted: {}", isFileDeleted);
                }

                if (!(isFileDeleted))
                {
                    response.setRequestStatus(AgentStatus.FAILURE);
                }
                else
                {
                    response.setRequestStatus(AgentStatus.SUCCESS);
                }
            }
            else
            {
                throw new FileManagerException("Requested target does not exist or cannot be written to. Possible permissions problem.");
            }
        }

        return response;
    }
}
