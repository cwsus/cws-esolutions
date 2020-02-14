package com.cws.esolutions.web.rest.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.web.Constants;

public class PaymentResponse implements Serializable
{
	private int code = 0;
	private String status = null;

	private static final long serialVersionUID = -6260275024158839895L;
	private static final String CNAME = PaymentResponse.class.getName();
	private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

	public void setCode(final int code)
	{
		final String methodName = PaymentResponse.CNAME + "#setCode(final int code)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", code);
		}

		this.code = code;
	}

	public void setStatus(final String status)
	{
		final String methodName = PaymentResponse.CNAME + "#setStatus(final String status)";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", status);
		}

		this.status = status;
	}

	public Integer getCode()
	{
		final String methodName = PaymentResponse.CNAME + "#getCode()";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", this.code);
		}

		return this.code;
	}

	public String getStatus()
	{
		final String methodName = PaymentResponse.CNAME + "#getStatus()";

		if (DEBUG)
		{
			DEBUGGER.debug("Value: {}", methodName);
			DEBUGGER.debug("Value: {}", this.status);
		}

		return this.status;
	}

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        final String methodName = PaymentResponse.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                    (!(field.getName().equals("userKeys"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
