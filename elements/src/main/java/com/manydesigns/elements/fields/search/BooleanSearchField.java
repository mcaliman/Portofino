/*
 * Copyright (C) 2005-2010 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.elements.fields.search;

import com.manydesigns.elements.fields.BooleanSearchValue;
import com.manydesigns.elements.logging.LogUtil;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.xml.XhtmlBuffer;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class BooleanSearchField extends AbstractSearchField {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected BooleanSearchValue value = BooleanSearchValue.ANY;

    //**************************************************************************
    // Costruttori
    //**************************************************************************

    public BooleanSearchField(PropertyAccessor accessor) {
        this(accessor, null);
    }

    public BooleanSearchField(PropertyAccessor accessor, String prefix) {
        super(accessor, prefix);
    }

    //**************************************************************************
    // SearchField implementation
    //**************************************************************************

    public void toSearchString(StringBuilder sb) {
        if (value != null) {
            appendToSearchString(sb, inputName, value.getStringValue());
        }
    }

    public void configureCriteria(Criteria criteria) {
        if (value == null) {
            return;
        }

        switch(value) {
            case ANY:
                break;
            case NULL:
                criteria.isNull(accessor);
                break;
            case FALSE:
                criteria.eq(accessor, Boolean.FALSE);
                break;
            case TRUE:
                criteria.eq(accessor, Boolean.TRUE);
                break;
            default:
                LogUtil.severeMF(logger,
                        "Unknown BooleanSearchValue: {0}", value.name());
        }
    }

    //**************************************************************************
    // Element implementation
    //**************************************************************************

    public void readFromRequest(HttpServletRequest req) {
        String stringValue = req.getParameter(inputName);
        value = BooleanSearchValue.ANY; // default
        for (BooleanSearchValue current : BooleanSearchValue.values()) {
            if (current.getStringValue().equals(stringValue)) {
                value = current;
            }
        }
    }

    public boolean validate() {
        return true;
    }

    public void toXhtml(XhtmlBuffer xb) {
        xb.openElement("fieldset");
        xb.writeLegend(StringUtils.capitalize(label), ATTR_NAME_HTML_CLASS);

        for (BooleanSearchValue current : BooleanSearchValue.values()) {
            // don't print null if the attribute is required
            if (required && current == BooleanSearchValue.NULL) {
                continue;
            }
            String idStr = id + "_" + current.name();
            String stringValue = current.getStringValue();
            boolean checked = (value == current);
            xb.openElement("div");
            xb.writeInputRadio(idStr, inputName, stringValue, checked);
            String label = getText(current.getLabelI18N());
            xb.writeLabel(label, idStr, null);
            xb.closeElement("div");
        }
        xb.closeElement("fieldset");
    }

    //**************************************************************************
    // Getter/setter
    //**************************************************************************

    public BooleanSearchValue getValue() {
        return value;
    }

    public void setValue(BooleanSearchValue value) {
        this.value = value;
    }
}