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
package com.cws.esolutions.security.valves;

import java.io.IOException;
import org.apache.catalina.Session;
import javax.servlet.ServletException;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.SavedRequest;
/*
 * SessionFixationValve
 * Application constants
 *
 * History
 *
 * Author               Date                           Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20            Created.
 */
public class SessionFixationValve extends ValveBase
{
	private String authenticationUrl = "/j_acegi_security_check";

	public void setAuthenticationUrl(String authenticationUrl)
	{
		if(authenticationUrl.equals(""))
		{
			throw new IllegalArgumentException("String is empty.");
		}

		this.authenticationUrl = authenticationUrl;
	}

	@Override
	public void invoke(Request req, Response response) throws IOException, ServletException
	{
		// check for the login URI, only after a login
		// we want to renew the session  
		if (req.getRequestURI().contains(authenticationUrl))
		{
			// save old session
			Session oldSession = req.getSessionInternal(true);
			SavedRequest saved = (SavedRequest) oldSession.getNote(Constants.FORM_REQUEST_NOTE);

			// invalidate old session
			req.getSession(true).invalidate();
			req.setRequestedSessionId(null);
			req.clearCookies();

			// create a new session and set it to the request
			Session newSession = req.getSessionInternal(true);
			req.setRequestedSessionId(newSession.getId());

			// copy data from the old session
			// to the new one
			if (saved != null)
			{
				newSession.setNote(Constants.FORM_REQUEST_NOTE, saved);
			}   
		}

		// after processing the request forward it
		getNext().invoke(req, response);
	}
}
