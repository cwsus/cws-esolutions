/*
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
package com.cws.esolutions.android.ui;
/*
 * Project: eSolutions
 * Package: com.cws.esolutions.android.ui
 * File: LoginActivityTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * 35033355              Feb 10, 2014                         Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import android.test.ActivityInstrumentationTestCase2;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    public LoginActivityTest(final Class<MainActivity> activityClass)
    {
        super(activityClass);
    }

    @Override @Before protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Test public final void testLoadForgotUsername()
    {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test public final void testLoadForgotPassword()
    {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test public final void testExecuteUserLogin()
    {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Override @After protected void tearDown() throws Exception
    {
        super.tearDown();
    }
}
