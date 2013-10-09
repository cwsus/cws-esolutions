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
package com.cws.us.pws.processors.impl;

import java.util.List;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cws.us.pws.processors.dto.Product;
import com.cws.us.pws.processors.dto.ProductRequest;
import com.cws.us.pws.processors.dto.ProductResponse;
import com.cws.us.pws.dao.impl.ProductReferenceDAOImpl;
import com.cws.us.pws.processors.interfaces.IProductReference;
import com.cws.us.pws.processors.exception.ProductRequestException;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.processors.impl
 * ProductReferenceImpl.java
 *
 * TODO: Add class description
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
 * 35033355 @ Apr 16, 2013 11:50:28 AM
 *     Created.
 */
public class ProductReferenceImpl implements IProductReference
{
    private String methodName = null;
    @Autowired private ProductReferenceDAOImpl productDAO = null;

    public final void setProductDAO(final ProductReferenceDAOImpl value)
    {
        this.methodName = IProductReference.CNAME + "#setProductDAO(final ProductReferenceDAOImpl value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productDAO = value;
    }

    @Override
    public List<Product> getProductList()
    {
        this.methodName = IProductReference.CNAME + "#getProductList()";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(this.methodName);
        }
        
        return null;
    }

    public ProductResponse getProductData(final ProductRequest request) throws ProductRequestException
    {
        this.methodName = IProductReference.CNAME + "#getProductData(final ProductRequest request) throws ProductRequestException";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("request: {}", request);
        }

        ProductResponse response = new ProductResponse();

        final Product reqProduct = request.getProduct();

        if (DEBUG)
        {
            DEBUGGER.debug("reqProduct: {}", reqProduct);
        }

        try
        {
            if (reqProduct != null)
            {
                List<String> productList = this.productDAO.getProductData(reqProduct.getProductId());

                if (DEBUG)
                {
                    DEBUGGER.debug("productList: {}", productList);
                }

                if (productList.size() != 0)
                {
                    Product product = new Product();
                    product.setProductId(Integer.valueOf(productList.get(0)));
                    product.setProductName(productList.get(1));
                    product.setProductDesc(productList.get(2));
                    product.setProductCost(productList.get(3));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Product: {}", product);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded product " + reqProduct.getProductId());
                    response.setProduct(product);
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Failed to load product with ID " + reqProduct.getProductId());
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("ProductResponse: {}", response);
                }
            }
            else
            {
                throw new ProductRequestException("No product data was provided. Unable to continue");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ProductRequestException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
