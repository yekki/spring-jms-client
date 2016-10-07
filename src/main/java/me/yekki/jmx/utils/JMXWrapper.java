/**
 * WebLogic Automation Book Source Code (JMX sources)
 * <p>
 * This file is part of the WLS-Automation book sourcecode software distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Martin Heinzl
 * Copyright (C) 2013 MH-EnterpriseConsulting, All rights reserved.
 */
package me.yekki.jmx.utils;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Predicate;


public interface JMXWrapper {


    public Object getAttribute(ObjectName name, String attributeName) throws Exception;

    public void setAttribute(ObjectName name, String attributeName, Object attributeValue) throws Exception;

    public void setAttribute(ObjectName name, Attribute myAttribute) throws Exception;


    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws Exception;

    public void disconnectFromAdminServer() throws Exception;

    public Hashtable<String, String> getMainServerDomainValues() throws Exception;


    public ArrayList<ObjectName> getManagedServerObjectNames() throws Exception;


    public ArrayList<String> getManagedServerNames() throws Exception;

    public String getServerState(String serverName) throws Exception;

    public ObjectName getServerRuntime(String serverName) throws Exception;


    public MBeanServerConnection getConnection();


    public JMXConnector getConnector();

    public ObjectName getService();


    public ObjectName getDomainConfigRoot();

    public ObjectName getDomainRuntimeRoot();

    public Predicate<ObjectName> getNamePredicate(String name);
}
