/*
* Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.portofino.pageactions.login;

import com.manydesigns.portofino.RequestAttributes;
import com.manydesigns.portofino.actions.user.LoginAction;
import com.manydesigns.portofino.application.Application;
import com.manydesigns.portofino.di.Inject;
import com.manydesigns.portofino.dispatcher.Dispatch;
import com.manydesigns.portofino.dispatcher.PageAction;
import com.manydesigns.portofino.dispatcher.PageInstance;
import com.manydesigns.portofino.model.Model;
import com.manydesigns.portofino.pageactions.PageActionName;
import com.manydesigns.portofino.pageactions.annotations.ScriptTemplate;
import net.sourceforge.stripes.action.Resolution;

/**
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
@ScriptTemplate("script_template.groovy")
@PageActionName("Login")
public class DefaultLoginAction extends LoginAction implements PageAction {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    //--------------------------------------------------------------------------
    // Properties
    //--------------------------------------------------------------------------

    /**
     * The dispatch property. Injected.
     */
    public Dispatch dispatch;

    /**
     * The PageInstance property. Injected.
     */
    public PageInstance pageInstance;

    /**
     * The application object. Injected.
     */
    @Inject(RequestAttributes.APPLICATION)
    public Application application;

    /**
     * The model object. Injected.
     */
    @Inject(RequestAttributes.MODEL)
    public Model model;

    //--------------------------------------------------------------------------
    // PageAction implementation
    //--------------------------------------------------------------------------

    @Override
    public String getApplicationName() {
        return application.getName();
    }

    @Override
    public Resolution preparePage() {
        return null;
    }

    @Override
    public String getDescription() {
        return pageInstance.getName();
    }

    @Override
    public PageInstance getPageInstance() {
        return pageInstance;
    }

    @Override
    public void setPageInstance(PageInstance pageInstance) {
        this.pageInstance = pageInstance;
    }

    @Override
    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    @Override
    public Dispatch getDispatch() {
        return dispatch;
    }

    @Override
    public boolean isEmbedded() {
        return false;
    }
}