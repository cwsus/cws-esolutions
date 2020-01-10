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
 *
 */
package com.cws.esolutions.web.webservices.impl;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: RandomQuoteServiceImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import javax.jws.WebService;
import javax.annotation.PostConstruct;

import com.cws.esolutions.web.webservices.interfaces.IQuoteService;
/**
 * @author cws-khuntly
 * @version 1.0
 */
@WebService(endpointInterface = "com.cws.esolutions.web.webservices.interfaces.IQuoteService")
public class QuoteServiceImpl implements IQuoteService
{
    private static List<String> quoteList = null;
    private static final String CNAME = QuoteServiceImpl.class.getName();

    @PostConstruct
    private void createList()
    {
        final String methodName = QuoteServiceImpl.CNAME + "#createList()";

        if (DEBUG)
        {
            DEBUGGER.debug("Value: {}", methodName);
        }

        quoteList = new ArrayList<String>();
        quoteList.add("You cannot escape the responsibility of tomorrow by evading it today");
        quoteList.add("I think therefore I am");
        quoteList.add("It was the best of times, it was the worst of times...");
        quoteList.add("Don't cry because it's over, smile because it happened");
        quoteList.add("Be yourself; everyone else is already taken");
        quoteList.add("So many books, so little time");
    }

    /**
     * @see com.cws.esolutions.web.webservices.interfaces.IQuoteService#getQuote()
     */
    public final String getQuote()
    {
        final String methodName = QuoteServiceImpl.CNAME + "#getQuote()";

        if (DEBUG)
        {
            DEBUGGER.debug("Value: ", methodName);
        }

        int index = 0;
        String quote = null;
        Random random = new Random();

        index = random.nextInt(quoteList.size());
        quote = (String) quoteList.get(index);

        if (DEBUG)
        {
            DEBUGGER.debug("Value: ", quote);
        }

        return quote;
    }
}