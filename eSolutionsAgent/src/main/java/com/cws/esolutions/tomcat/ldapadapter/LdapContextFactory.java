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
package com.cws.esolutions.tomcat.ldapadapter;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.tomcat.ldapadapter
 * File: AgentDaemon.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.Hashtable;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @see org.apache.commons.daemon.Daemon
 */
public class LdapContextFactory implements ObjectFactory
{
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable<?, ?> environment) throws NamingException
    {
    	LdapContext ldapContext = null;
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        Reference reference = (Reference) obj;
        Enumeration<RefAddr> references = reference.getAll();

        while (references.hasMoreElements()) {

            RefAddr address = references.nextElement();
            String type = address.getType();
            String content = (String) address.getContent();

            switch (type) {

            case Context.INITIAL_CONTEXT_FACTORY:
                env.put(Context.INITIAL_CONTEXT_FACTORY, content);

                break;
            case Context.PROVIDER_URL:
                env.put(Context.PROVIDER_URL, content);

                break;
            case Context.SECURITY_AUTHENTICATION:
                env.put(Context.SECURITY_AUTHENTICATION, content);

                break;
            case Context.SECURITY_PRINCIPAL:
                env.put(Context.SECURITY_PRINCIPAL, content);

                break;
            case Context.SECURITY_CREDENTIALS:
                env.put(Context.SECURITY_CREDENTIALS, content);

                break;
            default:
                break;
            }
        }

        try
        {
        	ldapContext = new InitialLdapContext(env, null);
        }
        catch (NamingException nx)
        {
        	throw new NamingException(nx.getMessage());
        }

        return ldapContext;
    }
}
