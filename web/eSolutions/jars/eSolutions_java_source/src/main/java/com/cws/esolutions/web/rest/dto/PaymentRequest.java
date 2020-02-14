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
package com.cws.esolutions.web.rest.dto;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.impl
 * File: ApplicationManagerProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServicesConstants;

public class PaymentRequest implements Serializable
{
	private int userId = 0;
	private String itemId = null;
	private double discount = 0.0;

	private static final long serialVersionUID = -3058977128179960507L;
	private static final String CNAME = PaymentRequest.class.getName();
	private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

	public void setItemId(final String itemId)
	{
		final String methodName = PaymentRequest.CNAME + "#setItemId(final String itemId)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", itemId);
		}
			
		this.itemId = itemId;
	}

	public void setDiscount(final double discount)
	{
		final String methodName = PaymentRequest.CNAME + "#setDiscount(final double discount)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", itemId);
		}

		this.discount = discount;
	}

	public void setUserId(final int userId)
	{
		final String methodName = PaymentRequest.CNAME + "#setUserId(final String userId)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", userId);
		}

		this.userId = userId;
	}

	public double getDiscount()
	{
		final String methodName = PaymentRequest.CNAME + "#setItemId(final String itemId)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", this.discount);
		}

		return this.discount;
	}

	public int getUserId()
	{
		final String methodName = PaymentRequest.CNAME + "#setItemId(final String itemId)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", this.userId);
		}

		return this.userId;
	}

	public String getItemId()
	{
		final String methodName = PaymentRequest.CNAME + "#setItemId(final String itemId)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", itemId);
		}

		return this.itemId;
	}
}
