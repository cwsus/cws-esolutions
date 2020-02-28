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
package com.cws.esolutions.rest.controllers;

import org.apache.commons.lang.StringUtils;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.rest.controllers
 * File: PaymentController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cws.esolutions.rest.model.PaymentRequest;
import com.cws.esolutions.rest.model.PaymentResponse;

@RestController
@RequestMapping("/payment")
public class PaymentController
{
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static final String ERROR_STATUS = "error";
	private static final String SHARED_KEY = "SHARED_KEY";
	private static final String SUCCESS_STATUS = "success";
 
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public PaymentResponse pay(@RequestParam(value = "key") final String key, @RequestBody final PaymentRequest request)
	{
		PaymentResponse response = new PaymentResponse();

		if (StringUtils.equalsIgnoreCase(PaymentController.SHARED_KEY, key))
		{
			response.setStatus(PaymentController.SUCCESS_STATUS);
			response.setCode(PaymentController.CODE_SUCCESS);
		}
		else
		{
			response.setStatus(PaymentController.ERROR_STATUS);
			response.setCode(PaymentController.AUTH_FAILURE);
		}

		return response;
	}
}
