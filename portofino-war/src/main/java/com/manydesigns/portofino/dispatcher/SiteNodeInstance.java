/*
 * Copyright (C) 2005-2011 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.dispatcher;

import com.manydesigns.portofino.context.Application;
import com.manydesigns.portofino.model.site.SiteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla - alessio.stalla@manydesigns.com
*/
public class SiteNodeInstance {

    protected final Application application;
    protected final SiteNode siteNode;
    protected final String mode;
    protected final List<SiteNodeInstance> childNodeInstances;

    //**************************************************************************
    // Logging
    //**************************************************************************

    public static final Logger logger = LoggerFactory.getLogger(SiteNodeInstance.class);

    public SiteNodeInstance(Application application, SiteNode siteNode, String mode) {
        this.application = application;
        this.siteNode = siteNode;
        this.mode = mode;
        childNodeInstances = new ArrayList<SiteNodeInstance>();
    }

    public SiteNode getSiteNode() {
        return siteNode;
    }

    public String getMode() {
        return mode;
    }

    public Application getApplication() {
        return application;
    }

    public void realize() {
    }

    //**************************************************************************
    // Utility Methods
    //**************************************************************************

    public SiteNodeInstance findChildNode(String id) {
        for(SiteNodeInstance node : getChildNodeInstances()) {
            if(id.equals(node.getSiteNode().getId())) {
                return node;
            }
        }
        logger.debug("Child node not found: {}", id);
        return null;
    }

    public String getUrlFragment() {
        return siteNode.getId();
    }

    public List<SiteNodeInstance> getChildNodeInstances() {
        return childNodeInstances;
    }

    public List<SiteNode> getChildNodes() {
        return siteNode.getChildNodes();
    }

    public String getLayoutContainer() {
        return siteNode.getLayoutContainer();
    }

    public void setLayoutContainer(String layoutContainer) {
        siteNode.setLayoutContainer(layoutContainer);
    }

    public int getLayoutOrder() {
        return siteNode.getActualLayoutOrder();
    }

    public void setLayoutOrder(int order) {
        siteNode.setLayoutOrder(Integer.toString(order));
    }
}