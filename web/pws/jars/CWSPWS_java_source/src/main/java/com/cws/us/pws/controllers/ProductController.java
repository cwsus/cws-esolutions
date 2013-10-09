/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.us.pws.controllers;

import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.pws.Constants;
import com.cws.us.pws.processors.dto.Product;
import com.cws.us.pws.processors.dto.ProductRequest;
import com.cws.us.pws.processors.dto.ProductResponse;
import com.cws.us.pws.processors.impl.ProductReferenceImpl;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.us.pws.processors.exception.ProductRequestException;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.controllers
 * HomeController.java
 *
 * This is a VERY basic controller because it really doesn't need to do
 * anything. It loads the home page and that's pretty much it - nothing
 * fancy.
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 10, 2013 12:59:40 PM
 *     Created.
 */
@Controller
@RequestMapping("/products")
public class ProductController
{
    private String methodName = null;
    private ProductReferenceImpl productRefSvc = null;

    private static final String CNAME = ProductController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setProductRefSvc(final ProductReferenceImpl value)
    {
        this.methodName = ProductController.CNAME + "#setProductRefSvc(final ProductReferenceImpl value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productRefSvc = value;
    }

    @RequestMapping(value = "/default.htm", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
    {
        this.methodName = ProductController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        return new ModelAndView("ShowProducts");
    }

    @RequestMapping(value = "/products.htm", method = RequestMethod.GET)
    public ModelAndView showProductsPage()
    {
        this.methodName = ProductController.CNAME + "#showProductsPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        return new ModelAndView("ShowProducts");
    }

    @RequestMapping(value = "/products.htm/product/{product}", method = RequestMethod.GET)
    public ModelAndView getProductInfo(@PathVariable(value = "product") final int productId)
    {
        this.methodName = ProductController.CNAME + "#getProductInfo(@PathVariable(value = \"product\") final int productId)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Product: {}", productId);
        }

        String viewName = null;
        Product product = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        try
        {
            Product reqProduct = new Product();
            reqProduct.setProductId(productId);

            if (DEBUG)
            {
                DEBUGGER.debug("Product: {}", reqProduct);
            }

            ProductRequest productRequest = new ProductRequest();
            productRequest.setProduct(reqProduct);

            if (DEBUG)
            {
                DEBUGGER.debug("ProductRequest: {}", productRequest);
            }

            ProductResponse productResponse = this.productRefSvc.getProductData(productRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ProductResponse: {}", productResponse);
            }

            if (productResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                product = productResponse.getProduct();

                if (DEBUG)
                {
                    DEBUGGER.debug("Product: {}", product);
                }
            }
        }
        catch (ProductRequestException prx)
        {
            ERROR_RECORDER.error(prx.getMessage(), prx);

            viewName = "errorResponse";
        }

        return new ModelAndView(viewName, null, product);
    }

    @RequestMapping(value = "/products.htm", method = RequestMethod.POST)
    public ModelAndView getProductInfo(@RequestParam("product") final Product request)
    {
        this.methodName = ProductController.CNAME + "#getProductInfo(@RequestParam(\"product\") final ProductRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Product: {}", request);
        }

        // String viewName = null;
        ProductResponse productResponse = null;
        ModelAndView modelView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        try
        {
            ProductRequest productRequest = new ProductRequest();
            productRequest.setProduct(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ProductRequest: {}", productRequest);
            }

            productResponse = this.productRefSvc.getProductData(productRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ProductResponse: {}", productResponse);
            }

            if (productResponse.getRequestStatus() == CoreServicesStatus.FAILURE)
            {
                modelView.setViewName("searchProducts");
                modelView.addObject("errorResponse", "An error occurred during the request.");
            }
        }
        catch (ProductRequestException prx)
        {
            ERROR_RECORDER.error(prx.getMessage(), prx);

            modelView.setViewName("errorResponse");
        }

        return modelView;
        // return new ModelAndView(viewName, null, productResponse);
    }
}
