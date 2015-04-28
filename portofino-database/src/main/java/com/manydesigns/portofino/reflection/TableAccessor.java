/*
 * Copyright (C) 2005-2015 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.manydesigns.portofino.reflection;

import com.manydesigns.elements.annotations.ShortName;
import com.manydesigns.elements.annotations.impl.ShortNameImpl;
import com.manydesigns.elements.reflection.ClassAccessor;
import com.manydesigns.elements.reflection.JavaClassAccessor;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.portofino.model.database.Column;
import com.manydesigns.portofino.model.database.PrimaryKey;
import com.manydesigns.portofino.model.database.Table;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class TableAccessor
        extends AbstractAnnotatedAccessor
        implements ClassAccessor {
    public static final String copyright =
            "Copyright (c) 2005-2015, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final Table table;
    protected final ColumnAccessor[] columnAccessors;
    protected final ColumnAccessor[] keyColumnAccessors;
    protected ClassAccessor javaClassAccessor = null;

    public final static Logger logger =
            LoggerFactory.getLogger(TableAccessor.class);

    //**************************************************************************
    // Constructors and initialization
    //**************************************************************************

    public TableAccessor(@NotNull Table table) {
        super(table.getAnnotations());

        if(!StringUtils.isEmpty(table.getShortName())) {
            ShortName shortName = new ShortNameImpl(table.getShortName());
            annotations.put(ShortName.class, shortName);
        }

        Class clazz = table.getActualJavaClass();
        if (clazz != null) {
            javaClassAccessor = JavaClassAccessor.getClassAccessor(clazz);
        }

        this.table = table;
        List<Column> columns = table.getColumns();
        List<Column> pkColumns = table.getPrimaryKey().getColumns();
        PrimaryKey pk = table.getPrimaryKey();
        columnAccessors = new ColumnAccessor[columns.size()];
        keyColumnAccessors = new ColumnAccessor[pkColumns.size()];

        setupColumns(columns, pkColumns, pk);
        setupKeyColumns(columns, pkColumns);
    }

    private void setupColumns(List<Column> columns, List<Column> pkColumns,
                              PrimaryKey pk) {
        int i = 0;
        for (Column current : columns) {
            boolean inPk = pkColumns.contains(current);
            PropertyAccessor nestedPropertyAccessor;
            if (javaClassAccessor == null) {
                nestedPropertyAccessor = null;
            } else {
                String propertyName = current.getActualPropertyName();
                try {
                    nestedPropertyAccessor =
                            javaClassAccessor.getProperty(propertyName);
                } catch (NoSuchFieldException e) {
                    nestedPropertyAccessor = null;
                    logger.error("Could not access nested property: " +
                            propertyName, e);
                }
            }

            boolean autoGenerated = inPk && (pk.getPrimaryKeyColumns().get(0).getGenerator()!=null);
            ColumnAccessor columnAccessor =
                    new ColumnAccessor(current,
                            inPk, autoGenerated, nestedPropertyAccessor);
            columnAccessors[i] = columnAccessor;
            i++;
        }
    }

    private void setupKeyColumns(List<Column> columns, List<Column> pkColumns) {
        int i = 0;
        for (Column current : pkColumns) {
            int index = columns.indexOf(current);
            ColumnAccessor columnAccessor = columnAccessors[index];
            keyColumnAccessors[i] = columnAccessor;
            i++;
        }
    }


    //**************************************************************************
    // ClassAccessor implementation
    //**************************************************************************

    public String getName() {
        return table.getQualifiedName();
    }

    public PropertyAccessor getProperty(String propertyName)
            throws NoSuchFieldException {
        for (ColumnAccessor current : columnAccessors) {
            if (current.getName().equals(propertyName)) {
                return current;
            }
        }

        throw new NoSuchFieldException(propertyName);
    }

    
    public PropertyAccessor[] getProperties() {
        return columnAccessors.clone();
    }


    public PropertyAccessor[] getKeyProperties() {
        return keyColumnAccessors.clone();
    }

    public Object newInstance() {
        if (javaClassAccessor == null) {
            HashMap<String, Object> obj =  new HashMap<String, Object>();
            obj.put("$type$", table.getEntityName());
            return obj;
        } else {
            return javaClassAccessor.newInstance();
        }
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************

    public Table getTable() {
        return table;
    }
}
