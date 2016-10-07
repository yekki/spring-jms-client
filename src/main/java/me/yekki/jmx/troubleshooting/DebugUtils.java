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
package me.yekki.jmx.troubleshooting;

import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.Attribute;
import javax.management.ObjectName;
import java.util.Iterator;
import java.util.Properties;

public class DebugUtils {

    private JMXWrapper myJMXWrapper = null;


    public DebugUtils(JMXWrapper _wrapper) throws WLSJMXException {
        myJMXWrapper = _wrapper;
    }

    public void setDebugFlags(String serverName, Properties debugProps) throws WLSJMXException {
        try {
            // get the server runtime(!)
            ObjectName myServer = (ObjectName) myJMXWrapper.invoke(myJMXWrapper.getDomainConfigRoot(),
                    "lookupServer",
                    new Object[]{new String(serverName)},
                    new String[]{String.class.getName()});

            // get the ServerDebug mbean
            ObjectName myServerDebugMBean = (ObjectName) myJMXWrapper.getAttribute(myServer, "ServerDebug");

            // iterate of debugProps and set them !
            Iterator<Object> it = debugProps.keySet().iterator();
            while (it.hasNext()) {
                String nextkey = (String) it.next();
                String nextvalue = debugProps.getProperty(nextkey);

                // set debug value
                myJMXWrapper.setAttribute(myServerDebugMBean, new Attribute(nextkey, new Boolean(nextvalue)));
                System.out.println("Setting " + nextkey + " to value " + nextvalue);
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error while getThreadDump of server " + serverName + " : " + ex.getMessage());
        }

    }

}

