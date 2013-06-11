/*
 * Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.portofino.model;

import com.manydesigns.portofino.AbstractPortofinoTest;
import com.manydesigns.portofino.model.database.*;

import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class DBParserTest extends AbstractPortofinoTest {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testParseJpetStorePostgresql() {
        assertNotNull(model);

        List<Database> databases = model.getDatabases();
         assertEquals(3, databases.size());

        Database database = databases.get(1);
        assertEquals("jpetstore", database.getDatabaseName());

        List<Schema> schemas = database.getSchemas();
        assertEquals(1, schemas.size());

        Schema schema = schemas.get(0);
        assertEquals("jpetstore", schema.getDatabaseName());
        assertEquals("PUBLIC", schema.getSchemaName());
        assertEquals("jpetstore.PUBLIC", schema.getQualifiedName());

        List<Table> tables = schema.getTables();
        assertEquals(6, tables.size());

        // tabella 0
        Table table0 = tables.get(1);
        checkTable(table0, "jpetstore", "PUBLIC", "category");

        List<Column> columns0 = table0.getColumns();
        assertEquals(3, columns0.size());

        checkColumn(columns0.get(0),
                "jpetstore", "PUBLIC", "category", "catid", null,
                "catid", "varchar", false, 10, 0);
        checkColumn(columns0.get(1),
                "jpetstore", "PUBLIC", "category", "name", null,
                "name", "varchar", true, 80, 0);
        checkColumn(columns0.get(2),
                "jpetstore", "PUBLIC", "category", "descn", null,
                "descn", "varchar", true, 255, 0);

        PrimaryKey primaryKey0 = table0.getPrimaryKey();
        assertEquals("pk_category", primaryKey0.getPrimaryKeyName());
        List<Column> pkColumns0 = primaryKey0.getColumns();
        assertEquals(1, pkColumns0.size());
        assertEquals(columns0.get(0), pkColumns0.get(0));
        assertEquals(2, table0.getOneToManyRelationships().size());
        checkRelationships(table0.getOneToManyRelationships()
                , 0, "fk_product_1", "PUBLIC" ,
                "category", "NO ACTION", "CASCADE");
        List<Reference> references =
                table0.getOneToManyRelationships().get(0).getReferences();
        checkReference(references, 0, "category", "catid");


        // tabella 1
        Table table1 = tables.get(2);
        checkTable(table1, "jpetstore", "PUBLIC", "product");

        int idxRel = 0;
        checkRelationships(table1.getForeignKeys()
                , idxRel, "fk_product_1", "PUBLIC" ,
                "category", "NO ACTION", "CASCADE");
        assertEquals(1, table1.getForeignKeys().size());
        List<Reference> references2 =
                table1.getForeignKeys().get(idxRel).getReferences();
        checkReference(references2, 0, "category", "catid");

        List<Column> columns1 = table1.getColumns();
        assertEquals(4, columns1.size());

        checkColumn(columns1.get(0),
                "jpetstore", "PUBLIC", "product", "productid", null,
                "productid", "varchar", false, 10, 0);
        checkColumn(columns1.get(1),
                "jpetstore", "PUBLIC", "product", "category", null,
                "category", "varchar", false, 10, 0);
        checkColumn(columns1.get(2),
                "jpetstore", "PUBLIC", "product", "name", null,
                "name", "varchar", true, 80, 0);
        checkColumn(columns1.get(3),
                "jpetstore", "PUBLIC", "product", "descn", null,
                "descn", "varchar", true, 255, 0);

        // tabella 2
        Table table2 = tables.get(0);
        checkTable(table2, "jpetstore", "PUBLIC", "lineitem");

        List<Column> columns2 = table2.getColumns();
        assertEquals(3, columns0.size());

        checkColumn(columns2.get(0),
                "jpetstore", "PUBLIC", "lineitem", "orderid", null,
                "orderid", "INTEGER", false, 8, 0);
        checkColumn(columns2.get(1),
                "jpetstore", "PUBLIC", "lineitem", "linenum", null,
                "linenum", "INTEGER", false, 8, 0);
        checkColumn(columns2.get(2),
                "jpetstore", "PUBLIC", "lineitem", "itemid", null,
                "itemid", "varchar", false, 255, 0);
        checkColumn(columns2.get(3),
                "jpetstore", "PUBLIC", "lineitem", "quantity", null,
                "quantity", "INTEGER", false, 8, 0);
        checkColumn(columns2.get(4),
                "jpetstore", "PUBLIC", "lineitem", "unitprice", null,
                "unitprice", "numeric", false, 10, 2);

        PrimaryKey primaryKey2 = table2.getPrimaryKey();
        assertEquals("pk_lineitem", primaryKey2.getPrimaryKeyName());
        List<Column> pkColumns2 = primaryKey2.getColumns();
        assertEquals(2, pkColumns2.size());
        assertEquals(columns2.get(0), pkColumns2.get(0));
        assertEquals(columns2.get(1), pkColumns2.get(1));
    }

    private void checkReference(List<Reference> references, int idx,
                                String fromColumn, String toColumn) {
        Reference ref = references.get(idx);
        assertEquals(fromColumn, ref.getFromColumn());
        assertEquals(toColumn, ref.getToColumn());
    }

    private void checkRelationships(List<ForeignKey> relationships, int idx, String name,
                                    String toSchema, String toTable,
                                    String onUpdate, String onDelete) {
        ForeignKey rel = relationships.get(idx);
        assertEquals(name, rel.getName());
        assertEquals(toSchema, rel.getToSchema());
        assertEquals(toTable, rel.getToTable());
        assertEquals(onUpdate, rel.getOnUpdate());
        assertEquals(onDelete, rel.getOnDelete());

    }

    private void checkColumn(Column column, String databaseName,
                             String schemaName, String tableName,
                             String columnName, String propertyName,
                             String name, String columnType, 
                             boolean nullable, int length, int scale) {
        assertEquals(databaseName, column.getDatabaseName());
        assertEquals(schemaName, column.getSchemaName());
        assertEquals(tableName, column.getTableName());
        assertEquals(columnName, column.getColumnName());
        assertEquals(propertyName, column.getPropertyName());
        assertEquals(name, column.getActualPropertyName());
        assertEquals(columnType.toUpperCase(), column.getColumnType().toUpperCase());
        assertEquals(nullable, column.isNullable());
        assertEquals(length, column.getLength().intValue());
        assertEquals(scale, column.getScale().intValue());
        assertEquals(databaseName + "." + schemaName + "." +
                tableName + "." + columnName, column.getQualifiedName());
    }

    private void checkTable(Table table, String databaseName,
                            String schemaName, String tableName) {
        assertEquals(databaseName, table.getDatabaseName());
        assertEquals(schemaName, table.getSchemaName());
        assertEquals(tableName, table.getTableName());
        assertEquals(databaseName + "." + schemaName + "." +
                tableName, table.getQualifiedName());
    }
}