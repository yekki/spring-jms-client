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
package me.yekki.jmx.creation_extension;

import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.Attribute;
import javax.management.ObjectName;
import java.util.Properties;

public class MailConfiguration {

    private JMXWrapper myJMXWrapper = null;

    public MailConfiguration(JMXWrapper _wrapper) throws WLSJMXException {
        myJMXWrapper = _wrapper;
    }


    public void createMailSession(String mailSessionName,
                                  String jndiName,
                                  ObjectName[] targets,
                                  Properties properties) throws WLSJMXException {
        try {
            // e.g.: com.bea:Name=TestDomain,Type=Domain
            ObjectName myDomainMBean = myJMXWrapper.getDomainConfigRoot();

            // Operation: javax.management.ObjectName  lookupMailSession(name:java.lang.String  )
            ObjectName myMailSessionMBean = (ObjectName) myJMXWrapper.invoke(myDomainMBean,
                    "lookupMailSession",
                    new Object[]{new String(mailSessionName)},
                    new String[]{String.class.getName()});
            if (myMailSessionMBean == null) {
                // create
                // Operation: javax.management.ObjectName  createMailSession(name:java.lang.String  )
                myMailSessionMBean = (ObjectName) myJMXWrapper.invoke(myDomainMBean,
                        "createMailSession",
                        new Object[]{new String(mailSessionName)},
                        new String[]{String.class.getName()});

                //target to targets
                // e.g. set('Targets',jarray.array([ObjectName('com.bea:Name=MartinTest_Cluster,Type=Cluster')], ObjectName))
                myJMXWrapper.setAttribute(myMailSessionMBean, new Attribute("Targets", targets));

                // configure
                myJMXWrapper.setAttribute(myMailSessionMBean, new Attribute("JNDIName", jndiName));
                myJMXWrapper.setAttribute(myMailSessionMBean, new Attribute("Properties", properties));
            } else
                throw new WLSJMXException("Mail session with name " + mailSessionName + " already exist  -  cannot create !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }


    public void deleteMailSession(String mailSessionName) throws WLSJMXException {
        try {
            // e.g.: com.bea:Name=TestDomain,Type=Domain
            ObjectName myDomainMBean = myJMXWrapper.getDomainConfigRoot();

            ObjectName myMailSessionMBean = (ObjectName) myJMXWrapper.invoke(myDomainMBean,
                    "lookupMailSession",
                    new Object[]{new String(mailSessionName)},
                    new String[]{String.class.getName()});
            if (myMailSessionMBean != null) {
                // delete
                // Operation: java.lang.Void  destroyMailSession(ms:javax.management.ObjectName  )
                myJMXWrapper.invoke(myDomainMBean, "destroyMailSession", new Object[]{myMailSessionMBean}, new String[]{ObjectName.class.getName()});
            } else
                throw new WLSJMXException("Mail session with name " + mailSessionName + " does not exist  -  cannot delete !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }


}