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

package me.yekki.jmx.security;

import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.ObjectName;
import java.util.Properties;


public class MigrateSecurityDataUtils {

    private JMXWrapper myJMXWrapper = null;

    private String realmName = "myrealm";

    public MigrateSecurityDataUtils(JMXWrapper _wrapper) throws WLSJMXException {
        myJMXWrapper = _wrapper;
    }

    public MigrateSecurityDataUtils(JMXWrapper _wrapper, String _realmName) throws WLSJMXException {
        myJMXWrapper = _wrapper;
        realmName = _realmName;
    }

    // export authentication data based on XACML
    public void exportAuthenticatorData(String securityProviderName, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myAuthenticationProviderMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupAuthenticationProvider",
                    new Object[]{new String(securityProviderName)},
                    new String[]{String.class.getName()});
            if (myAuthenticationProviderMBean != null) {
                // # export DefaultAtn type of data
                // cmo.exportData("DefaultAtn",fileName,Properties())
                myJMXWrapper.invoke(myAuthenticationProviderMBean,
                        "exportData",
                        new Object[]{"DefaultAtn", fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("AuthenticationProvider with name " + securityProviderName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }

    // import authentication data based on XACML
    public void importAuthenticatorData(String securityProviderName, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myAuthenticationProviderMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupAuthenticationProvider",
                    new Object[]{new String(securityProviderName)},
                    new String[]{String.class.getName()});
            if (myAuthenticationProviderMBean != null) {
                // # import DefaultAtn type of data
                // cmo.importData("DefaultAtn",fileName,Properties())
                myJMXWrapper.invoke(myAuthenticationProviderMBean,
                        "importData",
                        new Object[]{"DefaultAtn", fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("AuthenticationProvider with name " + securityProviderName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }

    // export authorizer data based on XACML
    public void exportAuthorizerData(String securityProviderName, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myAuthorizerMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupAuthorizer",
                    new Object[]{new String(securityProviderName)},
                    new String[]{String.class.getName()});
            if (myAuthorizerMBean != null) {
                // # export DefaultAtn type of data
                // cmo.exportData("DefaultAtn",fileName,Properties())
                myJMXWrapper.invoke(myAuthorizerMBean,
                        "exportData",
                        new Object[]{"XACML", fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("Authorizer with name " + securityProviderName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }


    // import authorizer data based on XACML
    public void importAuthorizerData(String securityProviderName, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myAuthorizerMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupAuthorizer",
                    new Object[]{new String(securityProviderName)},
                    new String[]{String.class.getName()});
            if (myAuthorizerMBean != null) {
                // # export DefaultAtn type of data
                // cmo.exportData("DefaultAtn",fileName,Properties())
                myJMXWrapper.invoke(myAuthorizerMBean,
                        "importData",
                        new Object[]{"XACML", fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("Authorizer with name " + securityProviderName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }


    // export rolemapper data: type can either bei XACML or DefaultRoles
    public void exportRoleMapperData(String roleMapperName, String exportFormat, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myRoleMapperMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupRoleMapper",
                    new Object[]{new String(roleMapperName)},
                    new String[]{String.class.getName()});
            if (myRoleMapperMBean != null) {
                // export <exportFormat> type of data
                // cmo.exportData(exportFormat,fileName,Properties())
                myJMXWrapper.invoke(myRoleMapperMBean,
                        "exportData",
                        new Object[]{exportFormat, fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("RoleMapper with name " + roleMapperName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }

    // import rolemapper data: type can either bei XACML or DefaultRoles
    public void importRoleMapperData(String roleMapperName, String exportFormat, String fileName) throws WLSJMXException {
        try {
            ObjectName securityRealmMBean = new ObjectName("Security:Name=" + realmName);

            ObjectName myRoleMapperMBean = (ObjectName) myJMXWrapper.invoke(securityRealmMBean,
                    "lookupRoleMapper",
                    new Object[]{new String(roleMapperName)},
                    new String[]{String.class.getName()});
            if (myRoleMapperMBean != null) {
                // import <exportFormat> type of data
                // cmo.importData(exportFormat,fileName,Properties())
                myJMXWrapper.invoke(myRoleMapperMBean,
                        "importData",
                        new Object[]{exportFormat, fileName, new Properties()},
                        new String[]{String.class.getName(), String.class.getName(), Properties.class.getName()});
            } else
                throw new WLSJMXException("RoleMapper with name " + roleMapperName + "  does not exist !");

        } catch (WLSJMXException ex) {
            throw ex;  // just re-throw
        } catch (Exception ex) {
            throw new WLSJMXException(ex);
        }
    }
}
