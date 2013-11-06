/**
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
package com.cws.esolutions.core.processors.impl;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.core.processors.dto.SearchResult;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.dao.processors.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.processors.impl.ProjectDataDAOImpl;
import com.cws.esolutions.core.processors.interfaces.ISearchProcessor;
import com.cws.esolutions.core.dao.processors.interfaces.IMessagingDAO;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
import com.cws.esolutions.core.dao.processors.impl.KnowledgeBaseDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO;
import com.cws.esolutions.core.dao.processors.impl.ApplicationDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.core.dao.processors.impl.ServiceMessagingDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * SearchProcessorImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class SearchProcessorImpl implements ISearchProcessor
{
    @Override
    public SearchResponse doArticleSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ISearchProcessor.CNAME + "#doArticleSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
        }

        SearchResponse response = new SearchResponse();

        try
        {
            IKnowledgeBaseDAO articleDao = new KnowledgeBaseDAOImpl();
            List<String[]> articleList = articleDao.getArticlesByAttribute(request.getSearchTerms());

            if (DEBUG)
            {
                DEBUGGER.debug("articleList: {}", articleList);
            }

            if ((articleList != null) && (articleList.size() != 0))
            {
                List<SearchResult> responseList = new ArrayList<SearchResult>();

                for (String[] data : articleList)
                {
                    if (DEBUG)
                    {
                        if (data != null)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }
                    }

                    if ((data != null) && (data.length >= 2))
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setPath(data[1]);
                        searchResult.setTitle(data[5]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResult: {}", searchResult);
                        }

                        responseList.add(searchResult);
                    }
                    else
                    {
                        throw new SearchRequestException("No results were located for the provided data");
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                response.setResults(responseList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Search completed successfully.");
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No results were located with the provided search terms.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SearchRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }

    @Override
    public SearchResponse doMessageSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ISearchProcessor.CNAME + "#doMessageSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
        }

        SearchResponse response = new SearchResponse();

        try
        {
            IMessagingDAO dao = new ServiceMessagingDAOImpl();
            List<String[]> messageList = dao.getMessagesByAttribute(request.getSearchTerms());

            if (DEBUG)
            {
                DEBUGGER.debug("messageList: {}", messageList);
            }

            if ((messageList != null) && (messageList.size() != 0))
            {
                List<SearchResult> responseList = new ArrayList<SearchResult>();

                for (String[] data : messageList)
                {
                    if (DEBUG)
                    {
                        if (data != null)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }
                    }

                    if ((data != null) && (data.length >= 2))
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setPath(data[0]);
                        searchResult.setTitle(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResult: {}", searchResult);
                        }

                        responseList.add(searchResult);
                    }
                    else
                    {
                        throw new SearchRequestException("No results were located for the provided data");
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                response.setResults(responseList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Search completed successfully.");
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No results were located with the provided search terms.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SearchRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }

    @Override
    public SearchResponse doServerSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ISearchProcessor.CNAME + "#doServerSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
        }

        SearchResponse response = new SearchResponse();

        try
        {
            IServerDataDAO serverDao = new ServerDataDAOImpl();
            List<String[]> serverList = serverDao.getServersByAttribute(request.getSearchTerms(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("serverList: {}", serverList);
            }

            if ((serverList != null) && (serverList.size() != 0))
            {
                List<SearchResult> responseList = new ArrayList<SearchResult>();

                for (String[] data : serverList)
                {
                    if (DEBUG)
                    {
                        if (data != null)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }
                    }

                    if ((data != null) && (data.length >= 16))
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setPath(data[0]);
                        searchResult.setTitle(data[16]); // proper ordinal for oper hostname

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResult: {}", searchResult);
                        }

                        responseList.add(searchResult);
                    }
                    else
                    {
                        throw new SearchRequestException("No results were located for the provided data");
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                response.setResults(responseList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Search completed successfully.");
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No results were located with the provided search terms.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SearchRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }

    @Override
    public SearchResponse doApplicationSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ISearchProcessor.CNAME + "#doApplicationSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
        }

        SearchResponse response = new SearchResponse();

        try
        {
            IApplicationDataDAO appDao = new ApplicationDataDAOImpl();
            List<String[]> applicationList = appDao.getApplicationsByAttribute(request.getSearchTerms(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("applicationList: {}", applicationList);
            }

            if ((applicationList != null) && (applicationList.size() != 0))
            {
                List<SearchResult> responseList = new ArrayList<SearchResult>();

                for (String[] data : applicationList)
                {
                    if (DEBUG)
                    {
                        if (data != null)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }
                    }

                    if ((data != null) && (data.length >= 2))
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setPath(data[0]);
                        searchResult.setTitle(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResult: {}", searchResult);
                        }

                        responseList.add(searchResult);
                    }
                    else
                    {
                        throw new SearchRequestException("No results were located for the provided data");
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                response.setResults(responseList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Search completed successfully.");
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No results were located with the provided search terms.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SearchRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }

    @Override
    public SearchResponse doProjectSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ISearchProcessor.CNAME + "#doProjectSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
        }

        SearchResponse response = new SearchResponse();

        try
        {
            IProjectDataDAO projectDao = new ProjectDataDAOImpl();
            List<String[]> projectList = projectDao.getProjectsByAttribute(request.getSearchTerms(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("projectList: {}", projectList);
            }

            if ((projectList != null) && (projectList.size() != 0))
            {
                List<SearchResult> responseList = new ArrayList<SearchResult>();

                for (String[] data : projectList)
                {
                    if (DEBUG)
                    {
                        if (data != null)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }
                    }

                    if ((data != null) && (data.length >= 2))
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setPath(data[0]);
                        searchResult.setTitle(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResult: {}", searchResult);
                        }

                        responseList.add(searchResult);
                    }
                    else
                    {
                        throw new SearchRequestException("No results were located for the provided data");
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                response.setResults(responseList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Search completed successfully.");
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No results were located with the provided search terms.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SearchRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
